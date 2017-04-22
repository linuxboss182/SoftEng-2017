package controllers.user;

import com.jfoenix.controls.JFXButton;
import entities.FloorProxy;
import controllers.shared.MapDisplayController;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.*;
import javafx.scene.text.TextFlow;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.Collator;
import java.text.Normalizer;
import java.util.HashSet;
import java.util.Set;

import entities.Room;
import javafx.stage.Stage;
import main.ApplicationController;

import entities.FloorImage;


public abstract class UserMasterController
		extends MapDisplayController
		implements Initializable
{
	@FXML private JFXButton logAsAdmin;
	@FXML private ListView<Room> directoryView;
	@FXML private Button getDirectionsBtn;
	@FXML private Button changeStartBtn;
	@FXML protected Pane linePane;
	@FXML protected Pane nodePane;
	@FXML protected TextField searchBar;
	@FXML protected TextFlow directionsTextField;
	@FXML private ComboBox<FloorProxy> floorComboBox;
	@FXML private BorderPane parentBorderPane;
	@FXML private SplitPane mapSplitPane;
	@FXML private GridPane destGridPane;
	@FXML private Button aboutBtn;
	@FXML private ImageView logoImageView;

	private double clickedX, clickedY;
	protected static Room startRoom;
	protected static Room endRoom;
	// TODO: Are these still needed? They shouldn't be, because of UserStartController being a separate class.
	protected static boolean choosingStart = false;
	protected static boolean choosingEnd = true; // Default this to true because that's the screen we start on


	/* ABSTRACT METHODS */
	/**
	 * Function called when a room is left clicked on the map
	 * @param room
	 */
	protected abstract void clickRoomAction(Room room);


	/* NON-ABSTRACT METHODS */

	/**
	 * Get the scene this is working on
	 */
	protected Scene getScene() {
		// The parentBorderPane should always exist, so use it to get the scene
		return this.parentBorderPane.getScene();
	}

	public void initialize() {

		mapScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
		mapScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

		this.directory = ApplicationController.getDirectory();
		iconController = ApplicationController.getIconController();

		this.changeFloor(this.directory.getFloor());
		this.imageViewMap.setPickOnBounds(true);

		// Set buttons to default
		this.enableOrDisableNavigationButtons();

		// I tested this value, and we want it to be defaulted here because the map does not start zoomed out all the way
		// TODO: Move zoom stuff to MapDisplayController
		// TODO: Set zoom based on window size
		zoomSlider.setValue(0);
		setZoomSliding();
		/*
		(x, y) -> {
			x += 1;
			return x+y;
		}
		 */

		if(floorComboBox != null) {
			initfloorComboBox();
		}

		this.displayRooms();
		iconController.resetAllRooms();
		if(this.directoryView != null) {
			this.populateListView();
		}

		// TODO: Move to MapDisplayController
		setScrollZoom();

		// TODO: See if there's a way to include this in the OnMouseDragged listener
		getCoordsFromMouseClick();

		setMouseDragPanning();

		// TODO: Use ctrl+plus/minus for zooming
		setHotkeys();
	}

	private void getCoordsFromMouseClick() {
		contentAnchor.setOnMousePressed(event -> {
			clickedX = event.getX();
			clickedY = event.getY();
		});
	}

	private void setMouseDragPanning() {
		contentAnchor.setOnMouseDragged(event -> {
			contentAnchor.setTranslateX(contentAnchor.getTranslateX() + event.getX() - clickedX);
			contentAnchor.setTranslateY(contentAnchor.getTranslateY() + event.getY() - clickedY);
			event.consume();
		});
	}

	/**
	 * Filter the room list for the search bar
	 *
	 * @param searchString The new string in the search bar
	 */
	public void filterRoomsByName(String searchString) {
		if((this.searchBar == null) || (searchString == null) || (searchString.length() == 0)) {
			this.populateListView();
		} else {
			// The Collator allows case-insensitie comparison
			Collator coll = Collator.getInstance();
			coll.setStrength(Collator.PRIMARY);
			// coll.setDecomposition(Collator.FULL_DECOMPOSITION); <- done by Normalizer

			// Normalize accents, remove leading spaces, remove duplicate spaces elsewhere
			String normed = Normalizer.normalize(searchString, Normalizer.Form.NFD).toLowerCase()
					.replaceAll("^\\s*", "").replaceAll("\\s+", " ");

			Set<Room> roomSet = directory.filterRooms(room ->
					(room.getLocation() != null) && // false if room has no location
					Normalizer.normalize(room.getName(), Normalizer.Form.NFD).toLowerCase()
					          .contains(normed)); // check with unicode normalization

			this.directoryView.setItems(FXCollections.observableArrayList(roomSet));
		}
	}


	/**
	 * Initialize the floor's choice box with 1-7 (the floors)
	 * Ideally this shouldn't be hard coded
	 */
	public void initfloorComboBox() {
//		// We are able to change what this list is of.
//		this.floorComboBox.setItems(FXCollections.observableArrayList("Floor 1", "Floor 2", "Floor 3", "Floor 4", "Floor 5", "Floor 6", "Floor 7"));
//		this.floorComboBox.setValue(this.floorComboBox.getItems().get(floor-1)); // default the selection to be whichever floor we start on
		this.floorComboBox.setItems(FXCollections.observableArrayList(FloorProxy.getFloors()));
		this.floorComboBox.getSelectionModel().selectedItemProperty().addListener(
				(ignored, ignoredOld, choice) -> this.changeFloor(choice));
		//this.floorComboBox.setConverter(FloorImage.FLOOR_STRING_CONVERTER);

		this.floorComboBox.setValue(this.floorComboBox.getItems().get(this.directory.getFloorNum() - 1)); // default the selection to be whichever floor we start on

	}

	@FXML
	public void logAsAdminClicked()
			throws IOException, InvocationTargetException {
		// Unset navigation targets for after logout
		startRoom = null;
		endRoom = null;
		Parent loginPrompt = (BorderPane) FXMLLoader.load(this.getClass().getResource("/LoginPrompt.fxml"));
		this.getScene().setRoot(loginPrompt);


	}

	/**
	 * Display all rooms on the current floor of the current building
	 */
	public void displayRooms() {
		Set<javafx.scene.Node> roomShapes = new HashSet<>();
		for (Room room : directory.getRoomsOnFloor(directory.getFloor())) {
			roomShapes.add(room.getUserSideShape());

			// Add listener to select rooms on click
			room.getUserSideShape().getSymbol().setOnMouseClicked((MouseEvent e) -> {
				if (e.getButton() == MouseButton.PRIMARY) this.clickRoomAction(room);
			});

			// Add listener for context menus (right click)
			room.getUserSideShape().getSymbol().setOnContextMenuRequested(e -> {

				ContextMenu optionsMenu = new ContextMenu();

				MenuItem startRoomItem = new MenuItem("Set as starting location");
				startRoomItem.setOnAction(e1 -> selectStartRoom(room));
				MenuItem endRoomItem = new MenuItem("Set as destination");
				endRoomItem.setOnAction(e2-> selectEndRoom(room));
				optionsMenu.getItems().addAll(startRoomItem, endRoomItem);
				optionsMenu.show(room.getUserSideShape(), e.getScreenX(), e.getScreenY());
			});
		}
		this.nodePane.getChildren().setAll(roomShapes);
	}

	/**
	 * Populates the list of rooms
	 */
	public void populateListView() {
		this.directoryView.setItems(this.listProperty);
		this.listProperty.set(FXCollections.observableArrayList(directory.filterRooms(r -> r.getLocation() != null)));

		this.directoryView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Room>() {
			@Override
			public void changed(ObservableValue<? extends Room> observable, Room oldValue, Room newValue) {
				// These variables are set in the controllers when the scene is switched...
				if(choosingEnd) {
					selectEndRoom(directoryView.getSelectionModel().getSelectedItem());
				} else if(choosingStart) {
					selectStartRoom(directoryView.getSelectionModel().getSelectedItem());

				}
			}
		});
	}

	/**
	 * Enable or disable the "get directions" and "set starting location" buttons
	 *
	 * If both start and end locations are set, enable the "get directions" button
	 *
	 * If The end room is set, enable the "set starting location" button
	 */
	protected void enableOrDisableNavigationButtons() {
		if (this.getDirectionsBtn != null) {
			if (endRoom == null || startRoom == null) {
				this.getDirectionsBtn.setDisable(true);
			} else {
				this.getDirectionsBtn.setDisable(false);
			}
		}
		if (this.changeStartBtn != null) {
			this.changeStartBtn.setDisable((endRoom != null) ? false : true);
		}
	}




	@FXML
	public void getDirectionsClicked() throws IOException, InvocationTargetException {
		Parent userPath = (BorderPane) FXMLLoader.load(this.getClass().getResource("/UserPath.fxml"));
		this.getScene().setRoot(userPath);
	}


	protected void changeFloor(FloorImage floor) {
		Image map = this.directory.switchFloors(floor);
		this.imageViewMap.setImage(map);
		this.displayRooms();
	}


	/**
	 * Below are helper methods to select and deselect the starting rooms for a path
	 */

	protected void selectStartRoom(Room r) {
		if(r == null) return;
		startRoom = r;
		this.enableOrDisableNavigationButtons();
//		this.enableDirectionsBtn();
		iconController.selectStartRoom(r);
		this.displayRooms();
	}

	protected void selectEndRoom(Room r) {
		if(r == null) return;
		endRoom = r;
		this.enableOrDisableNavigationButtons();
//		this.enableDirectionsBtn();
//		this.enableChangeStartBtn();
		iconController.selectEndRoom(r);
		this.displayRooms();
	}

	@FXML
	public void aboutBtnClicked () throws IOException {
		UserAboutPage aboutPageController = new UserAboutPage();
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(this.getClass().getResource("/aboutPage.fxml"));
		Scene addAboutScene = new Scene(loader.load());
		Stage addAboutStage = new Stage();
		addAboutStage.initOwner(contentAnchor.getScene().getWindow());
		addAboutStage.setScene(addAboutScene);
		addAboutStage.showAndWait();
	}
}
