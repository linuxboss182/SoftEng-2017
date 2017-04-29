package controllers.user;

import com.jfoenix.controls.*;
import entities.FloorProxy;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.text.Collator;
import java.text.Normalizer;
import java.util.ResourceBundle;
import java.util.Set;

import entities.Room;
import javafx.stage.Stage;
import main.ApplicationController;


public class UserMasterController
		extends DrawerController
		implements Initializable
{
	@FXML private ImageView logAsAdmin;
	@FXML private JFXListView<Room> resultsListView;
	@FXML private Button getDirectionsBtn;
	@FXML private Button changeStartBtn;
	@FXML protected Pane linePane;
	@FXML private Pane nodePane;
	@FXML protected JFXTextField destinationField;
	@FXML public ImageView destImageView;
	@FXML protected JFXTextField startField;
	@FXML public ImageView startImageView;
	@FXML private ComboBox<FloorProxy> floorComboBox;
	@FXML private BorderPane parentBorderPane;
	@FXML private GridPane destGridPane;
	@FXML private ImageView aboutBtn;
	@FXML private ImageView logoImageView;
	@FXML private JFXToolbar topToolBar;
	@FXML private BorderPane floatingBorderPane;

	private Room startRoom;
	private Room endRoom;



	@FXML private HBox startHBox;
	@FXML private HBox destHBox;
	@FXML private HBox goHBox;
	@FXML private HBox bottomHBox;

	/**
	 * Get the scene this is working on
	 */
	protected Scene getScene() {
		// The parentBorderPane should always exist, so use it to get the scene
		return this.parentBorderPane.getScene();
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		this.initialize();
	}

	/**
	 * Method used to initialize superclasses
	 *
	 * Not technically related to Initializable::initialize, but used for the same purpose
	 */
	public void initialize() {
		super.initialize();

		//Set IDs for CSS
		setStyleIDs();


		this.directory = ApplicationController.getDirectory();
		iconController = ApplicationController.getIconController();
		if (startRoom == null) {
			startRoom = directory.getKiosk();
			startField.setText("Your Location");
		}

		initializeIcons();

		this.changeFloor(this.directory.getFloor());
		this.imageViewMap.setPickOnBounds(true);

		// Set buttons to default
		this.enableOrDisableNavigationButtons();

		// TODO: Set zoom based on window size
		zoomSlider.setValue(0);
		setZoomSliding();

		initfloorComboBox();

		this.displayRooms();

		this.populateListView();

		setScrollZoom();

		// TODO: Use ctrl+plus/minus for zooming
		setHotkeys();

		// Slightly delay the call so that the bounds aren't screwed up
		Platform.runLater( () -> {
			initWindowResizeListener();
			resizeDrawerListener(drawerParentPane.getHeight());

		});
//		Platform.runLater( () -> this.fitMapSize());
		// Enable search; if this becomes more than one line, make it a function
		this.destinationField.textProperty().addListener((ignored, ignoredOld, contents) -> this.filterRoomsByName(contents));
		this.startField.textProperty().addListener((ignored, ignoredOld, contents) -> this.filterRoomsByName(contents));

		logAsAdmin.setImage(new Image("/lock.png"));
		startImageView.setImage(new Image("/aPin.png"));
		destImageView.setImage(new Image("/bPin.png"));
		aboutBtn.setImage(new Image("/about.png"));

		drawerParentPane.heightProperty().addListener(new ChangeListener<Number>() {
			@Override public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneHeight, Number newSceneHeight) {
				resizeDrawerListener((double)newSceneHeight);
			}
		});
		resizeDrawerListener(677.0);

		mainDrawer.open();

		//Enable panning again
		floatingBorderPane.setPickOnBounds(false);


	}

	public void resizeDrawerListener(Double newSceneHeight) {
		System.out.println("Height: " + newSceneHeight);
		resultsListView.setPrefHeight((double)newSceneHeight - startHBox.getHeight() - destHBox.getHeight() - goHBox.getHeight() - bottomHBox.getHeight());
	}

	public void setStyleIDs() {
		startHBox.getStyleClass().add("hbox");
		destHBox.getStyleClass().add("hbox");
		goHBox.getStyleClass().add("hbox-go");
		bottomHBox.getStyleClass().addAll("hbox", "hbox-bottom");
		getDirectionsBtn.getStyleClass().add("jfx-button");
		topToolBar.getStyleClass().add("tool-bar");
		drawerParentPane.getStyleClass().add("drawer");
	}


	private void initializeIcons() {
		iconManager.setOnMouseClickedOnSymbol((room, event) -> {
			if (event.getButton() == MouseButton.PRIMARY) this.selectRoomAction(room);
			event.consume();
		});
		iconManager.getIcons(directory.getRooms());
	}

	/**
	 * Filter the room list for the search bar
	 *
	 * @param searchString The new string in the search bar
	 */
	public void filterRoomsByName(String searchString) {
		if((searchString == null) || (searchString.length() == 0)) {
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

			this.resultsListView.setItems(FXCollections.observableArrayList(roomSet));
		}
	}


	/**
	 * Initialize the floor's choice box with 1-7 (the floors)
	 * Ideally this shouldn't be hard coded
	 */
	public void initfloorComboBox() {
		this.floorComboBox.setItems(FXCollections.observableArrayList(FloorProxy.getFloors()));
		this.floorComboBox.getSelectionModel().selectedItemProperty().addListener(
				(ignored, ignoredOld, choice) -> this.changeFloor(choice));
		//this.floorComboBox.setConverter(FloorImage.FLOOR_STRING_CONVERTER); // <- for choiceBox, not comboBox

		this.floorComboBox.setValue(this.floorComboBox.getItems().get(this.directory.getFloorNum() - 1)); // default the selection to be whichever floor we start on

	}

	@FXML
	public void logAsAdminClicked()
			throws IOException, InvocationTargetException {
		// Unset navigation targets for after logout
		Parent loginPrompt = (BorderPane) FXMLLoader.load(this.getClass().getResource("/LoginPrompt.fxml"));
		this.getScene().setRoot(loginPrompt);
	}

	/**
	 * Called by MapDisplayController when changing floor
	 */
	@Override
	protected void redisplayMapItems() {
		this.displayRooms();
	}


	/**
	 * Display all rooms on the current floor of the current building
	 */
	public void displayRooms() {
		this.nodePane.getChildren().setAll(iconManager.getIcons(directory.getRoomsOnFloor()));
	}

	/**
	 * Populates the list of rooms
	 */
	public void populateListView() {
		this.resultsListView.setItems(this.listProperty);
		this.listProperty.set(FXCollections.observableArrayList(directory.filterRooms(r -> r.getLocation() != null)));

		this.resultsListView.getSelectionModel().selectedItemProperty().addListener((ignored, oldValue, newValue) -> {
			this.selectRoomAction(resultsListView.getSelectionModel().getSelectedItem());

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
//		if (this.changeStartBtn != null) {
//			this.changeStartBtn.setDisable((endRoom != null) ? false : true);
//		}
	}




	@FXML
	public void getDirectionsClicked() throws IOException, InvocationTargetException {
		// TODO: Find path before switching scene, so the "no path" alert returns to destination choice
		FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/UserPath.fxml"));
		BorderPane pane = loader.load();
		UserPathController controller = loader.getController();

		/* change to a scene with the path if possible */
		if (controller.preparePathSceneSuccess(startRoom, endRoom)) {
			this.getScene().setRoot(pane);
		} else {
			this.redisplayMapItems();
		}
	}

	/*
	 * Below are helper methods to select and deselect the starting rooms for a path
	 */

	/**
	 * Function called to select a room
	 */
	protected void selectRoomAction(Room room) {
		if (this.startField.isFocused()) {
			this.selectStartRoom(room);
			startField.setText(room.getName());
		} else {
			this.selectEndRoom(room);
			destinationField.setText(room.getName());
		}
	}

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
	@FXML
	private void helpBtnClicked() throws IOException{
		UserHelpController helpController = new UserHelpController();
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(this.getClass().getResource("/UserHelp.fxml"));
		Scene userHelpScene = new Scene(loader.load());
		Stage userHelpStage = new Stage();
		userHelpStage.initOwner(contentAnchor.getScene().getWindow());
		userHelpStage.setScene(userHelpScene);
		userHelpStage.showAndWait();
	}
}

