package controllers.user;

import com.jfoenix.controls.JFXButton;
import controllers.icons.IconManager;
import entities.FloorProxy;
import controllers.shared.MapDisplayController;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;

import java.awt.*;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.text.Collator;
import java.text.Normalizer;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import entities.Room;
import javafx.stage.Stage;
import main.ApplicationController;


public class UserMasterController
		extends MapDisplayController
		implements Initializable
{
	@FXML private JFXButton logAsAdmin;
	@FXML private ListView<Room> directoryView;
	@FXML private Button getDirectionsBtn;
	@FXML private Button changeStartBtn;
	@FXML protected Pane linePane;
	@FXML private Pane nodePane;
	@FXML protected TextField searchBar;
	@FXML private ComboBox<FloorProxy> floorComboBox;
	@FXML private BorderPane parentBorderPane;
	@FXML private SplitPane mapSplitPane;
	@FXML private GridPane destGridPane;
	@FXML private Button aboutBtn;
	@FXML private ImageView logoImageView;

	private double clickedX;
	private double clickedY;
	protected Room startRoom;
	protected Room endRoom;

	IconManager iconManager;

	private Timer timer = new Timer();

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
		mapScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
		mapScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

		this.directory = ApplicationController.getDirectory();
		iconController = ApplicationController.getIconController();
		if (startRoom == null) startRoom = directory.getKiosk();

		this.iconManager = new IconManager();
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

		// TODO: See if there's a way to include this in the OnMouseDragged listener

		setMouseMapListeners();

		// TODO: Use ctrl+plus/minus for zooming
		setHotkeys();

		// Slightly delay the call so that the bounds aren't screwed up
		Platform.runLater(this::initWindowResizeListener);
//		Platform.runLater( () -> this.fitMapSize());
		// Enable search; if this becomes more than one line, make it a function
		this.searchBar.textProperty().addListener((ignored, ignoredOld, contents) -> this.filterRoomsByName(contents));

		this.resetTimer();
	}

	private void initializeIcons() {
		iconManager.setOnMouseClickedOnSymbol((room, event) -> {
			if (event.getButton() == MouseButton.PRIMARY) this.selectRoomAction(room);
			event.consume();
		});
		iconManager.getIcons(directory.getRooms());
	}

	private void setMouseMapListeners() {
		contentAnchor.setOnMousePressed(event -> {
			clickedX = event.getX();
			clickedY = event.getY();
		});

		contentAnchor.setOnMouseDragged(event -> {
			// Limits the dragging for x and y coordinates. (panning I mean)
			if (event.getSceneX() >= mapSplitPane.localToScene(mapSplitPane.getBoundsInLocal()).getMinX() && event.getSceneX() <=  mapScroll.localToScene(mapScroll.getBoundsInLocal()).getMaxX()) {
				contentAnchor.setTranslateX(contentAnchor.getTranslateX() + event.getX() - clickedX);
			}
			if(event.getSceneY() >= mapSplitPane.localToScene(mapSplitPane.getBoundsInLocal()).getMinY() && event.getSceneY() <=  mapScroll.localToScene(mapScroll.getBoundsInLocal()).getMaxY()) {
				contentAnchor.setTranslateY(contentAnchor.getTranslateY() + event.getY() - clickedY);
			}
			event.consume();
			this.resetTimer();
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
		this.floorComboBox.setItems(FXCollections.observableArrayList(FloorProxy.getFloors()));
		this.floorComboBox.getSelectionModel().selectedItemProperty().addListener(
				(ignored, ignoredOld, choice) -> this.changeFloor(choice));
		//this.floorComboBox.setConverter(FloorImage.FLOOR_STRING_CONVERTER); // <- for choiceBox, not comboBox

		this.floorComboBox.setValue(this.floorComboBox.getItems().get(this.directory.getFloorNum() - 1)); // default the selection to be whichever floor we start on

	}

	@FXML
	public void logAsAdminClicked()
			throws IOException, InvocationTargetException {
		this.resetTimer();
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
		this.nodePane.getChildren().setAll(iconManager.getIcons(directory.getRoomsOnFloor(directory.getFloor())));
	}

	/**
	 * Populates the list of rooms
	 */
	public void populateListView() {
		this.directoryView.setItems(this.listProperty);
		this.listProperty.set(FXCollections.observableArrayList(directory.filterRooms(r -> r.getLocation() != null)));

		this.directoryView.getSelectionModel().selectedItemProperty().addListener(
				(ignored, oldValue, newValue) -> this.selectRoomAction(directoryView.getSelectionModel().getSelectedItem()));
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
		this.resetTimer();
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
		if (this.changeStartBtn.isDisabled()) {
			this.selectStartRoom(room);
			this.changeStartBtn.setDisable(false);
		} else {
			this.selectEndRoom(room);
		}
	}

	@FXML
	public void changeStartClicked() throws IOException, InvocationTargetException {
		this.resetTimer();
		this.changeStartBtn.setDisable(true);
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
		this.resetTimer();
		UserAboutPage aboutPageController = new UserAboutPage();
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(this.getClass().getResource("/aboutPage.fxml"));
		Scene addAboutScene = new Scene(loader.load());
		Stage addAboutStage = new Stage();
		addAboutStage.initOwner(contentAnchor.getScene().getWindow());
		addAboutStage.setScene(addAboutScene);
		addAboutStage.showAndWait();
	}

	private void resetTimer() {
		if(!this.directory.isLoggedIn()) return;
		try{
			timer.cancel();
		} catch(Exception e) {
			// just please end it. this thing is so annoying
		}

		try{
			timer = new Timer();
			timer.schedule(getTimerTask(), directory.getTimeout());
		} catch(Exception e) {
			// just please end it. this thing is so annoying
		}
	}

	private TimerTask getTimerTask() {
		return new TimerTask()
		{
			public void run() {
				timeout();
			}
		};
	}

	private void timeout() {
		//TODO: MEMENTO CALL GOES HERE

	}

	public UserState getState() {// TODO: adjust this to work with the text field
		return new UserState(this.getScene().getRoot(), this.directory.isLoggedIn(), this.startRoom, this.endRoom, "" );
	}

	public void setState(UserState state) {
		this.getScene().setRoot(state.getRoot());
		if(state.getLoggedIn()) {
			this.directory.logIn();
		} else {
			this.directory.logOut();
		}
	}
}
