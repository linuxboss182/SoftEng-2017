package ui.user;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTabPane;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXToolbar;

import entities.Node;
import entities.Professional;
import entities.RoomType;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Tab;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.*;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;


import main.ApplicationController;
import memento.TimeoutTimer;
import org.apache.commons.lang3.StringUtils;

import entities.Room;
import entities.floor.FloorProxy;
import algorithms.PathNotFoundException;
import algorithms.Pathfinder;


public class UserMasterController
		extends DrawerController
		implements Initializable
{
	@FXML private ImageView logAsAdmin;
	@FXML private JFXTabPane destinationTypeTabs;
	@FXML private JFXListView<Room> roomSearchResults;
	@FXML private JFXListView<Professional> profSearchResults;
	@FXML private JFXListView<RoomType> servicesList;
	@FXML private Tab roomTab;
	@FXML private Tab profTab;
	@FXML private Tab servicesTab;
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
	@FXML private JFXButton helpBtn;


	private Room startRoom;
	private Room endRoom;
	private Image destRoomImage = new Image("/bPin.png");
	private Image destProfImage = new Image("/professional.png");

	private boolean selectingStart = false; // TODO: Find a way to remove this state

	@FXML private HBox startHBox;
	@FXML private HBox destHBox;
	@FXML private HBox goHBox;

	private TimeoutTimer timer = TimeoutTimer.getTimeoutTimer();

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

		if (startRoom == null) {
			startRoom = directory.getKiosk();
		}

		this.initializeIcons();

		// Set buttons to default
		this.enableOrDisableNavigationButtons();

		initfloorComboBox();
		initDestinationTypeTabs();

		setupSearchFields();
		initImages();

		// Slightly delay the call so that the bounds aren't screwed up
		Platform.runLater(() -> {
			resizeDrawerListener();
			destinationField.requestFocus();

		});
		resizeDrawerListener();
		System.out.println("drawerParentPane: " + drawerParentPane.getHeight());


		mainDrawer.open();

		//Enable panning again
		floatingBorderPane.setPickOnBounds(false);

		initFocusTraversables();

		this.displayRooms();

		setServicesList();
	}

	private void initImages() {
		if (directory.isProfessional()) {
			logAsAdmin.setImage(new Image("/logout.png"));
		} else {
			logAsAdmin.setImage(new Image("/lock.png"));
		}
		startImageView.setImage(new Image("/aPin.png"));
		destImageView.setImage(destRoomImage);
		aboutBtn.setImage(new Image("/about.png"));
	}

	private void resizeDrawerListener() {
		destinationTypeTabs.setPrefHeight(drawerParentPane.getHeight() - startHBox.getHeight() - destHBox.getHeight() - goHBox.getHeight());
		drawerParentPane.heightProperty().addListener((ignored, old, newHeight) -> {
			destinationTypeTabs.setPrefHeight((double)newHeight - startHBox.getHeight() - destHBox.getHeight() - goHBox.getHeight());
			System.out.println("drawerParentPane: " + drawerParentPane.getHeight());
		});

	}

	private void setStyleIDs() {
		startHBox.getStyleClass().add("hbox");
		destHBox.getStyleClass().add("hbox");
		goHBox.getStyleClass().add("hbox-go");
		getDirectionsBtn.getStyleClass().add("jfx-button");
		topToolBar.getStyleClass().add("tool-bar");
		drawerParentPane.getStyleClass().add("drawer");
		helpBtn.getStyleClass().add("blue-button");
		profSearchResults.getStyleClass().add("jfx-list-view");
		roomSearchResults.getStyleClass().add("jfx-list-view");
		profTab.getStyleClass().add("jfx-tab");
		roomTab.getStyleClass().add("jfx-tab");
		servicesTab.getStyleClass().add("jfx-tab");
		destinationTypeTabs.getStyleClass().add("jfx-tab-pane");
	}


	private void initializeIcons() {
		iconManager.setOnMouseClickedOnSymbol((room, event) -> {
			if (event.getButton() == MouseButton.PRIMARY) this.selectRoomAction(room);
			event.consume();
			System.out.println("event consumed");
		});
		iconManager.updateListeners(directory.getRooms());
		iconManager.getIcons(directory.getRooms());
	}


	/**
	 * Filter the active search list, if any
	 */
	private void filterRoomsOrProfessionals(String searchString) {
		Tab tabContent = destinationTypeTabs.getSelectionModel().getSelectedItem();
		if (tabContent == roomTab) {
			filterRoomsByName(searchString);
		} else if (tabContent == profTab) {
			filterProfessionalsByName(searchString);
		}
	}


	/**
	 * Filter the room list to show only rooms matching the given string
	 */
	private void filterRoomsByName(String searchString) {
		if ((searchString == null) || (searchString.length() == 0)) {
			this.resetRoomSearchResults();
		} else {
			String search = searchString.toLowerCase();
			Set<Room> rooms = directory.getUserRooms();
			rooms.removeIf(room -> ! room.getName().toLowerCase().contains(search));
			this.roomSearchResults.setItems(FXCollections.observableArrayList(rooms));
		}
	}

	/**
	 * Filter the professional list to show only rooms matching the given string
	 */
	private void filterProfessionalsByName(String searchString) {
		if ((searchString == null) || (searchString.length() == 0)) {
			this.resetProfessionalSearchResults();
		} else {
			String search = searchString.toLowerCase();
			search = StringUtils.removeStart(search, "dr. ");
			search = StringUtils.removeStart(search, "doctor ");
			String finalSearch = search;

			Set<Professional> surnameMatches = directory.getProfessionals();
			surnameMatches.removeIf(p -> ! p.getSurname().toLowerCase().contains(finalSearch));

			Set<Professional> fullNameMatches = directory.getProfessionals();
			fullNameMatches.removeIf(p -> ! p.getFullName().toLowerCase().contains(finalSearch));
			fullNameMatches.removeAll(surnameMatches);

			List<Professional> results = new LinkedList<>(surnameMatches);
			results.addAll(fullNameMatches);

			this.profSearchResults.setItems(FXCollections.observableList(results));
		}
	}

	@Override
	protected void setHotkeys(){
		parentBorderPane.setOnKeyPressed(e -> {
			//TODO add functionality for zooming with hotkeys

			if (e.getCode() == KeyCode.RIGHT && e.isShiftDown()) {
				contentAnchor.setTranslateX(contentAnchor.getTranslateX() - 10);
			} else if (e.getCode() == KeyCode.LEFT && e.isShiftDown()) {
				contentAnchor.setTranslateX(contentAnchor.getTranslateX() + 10);
			} else if (e.getCode() == KeyCode.UP && e.isShiftDown()) {
				contentAnchor.setTranslateY(contentAnchor.getTranslateY() + 10);
			} else if (e.getCode() == KeyCode.DOWN && e.isShiftDown()) {
				contentAnchor.setTranslateY(contentAnchor.getTranslateY() - 10);
			} else if (e.getCode() == KeyCode.DIGIT1 && e.isShiftDown()){
				FloorProxy floor = FloorProxy.getFloor("OUTSIDE", 1);
				if (floor != null) floorComboBox.getSelectionModel().select(floor);
			} else if (e.getCode() == KeyCode.DIGIT1 && e.isControlDown()){
				FloorProxy floor = FloorProxy.getFloor("BELKIN", 1);
				if (floor != null) floorComboBox.getSelectionModel().select(floor);
			} else if (e.getCode() == KeyCode.DIGIT2 && e.isControlDown()){
				FloorProxy floor = FloorProxy.getFloor("BELKIN", 2);
				if (floor != null) floorComboBox.getSelectionModel().select(floor);
			} else if (e.getCode() == KeyCode.DIGIT3 && e.isControlDown()){
				FloorProxy floor = FloorProxy.getFloor("BELKIN", 3);
				if (floor != null) floorComboBox.getSelectionModel().select(floor);
			} else if (e.getCode() == KeyCode.DIGIT4 && e.isControlDown()){
				FloorProxy floor = FloorProxy.getFloor("BELKIN", 4);
				if (floor != null) floorComboBox.getSelectionModel().select(floor);
			} else if (e.getCode() == KeyCode.DIGIT1){
				FloorProxy floor = FloorProxy.getFloor("FAULKNER", 1);
				if (floor != null) floorComboBox.getSelectionModel().select(floor);
			} else if (e.getCode() == KeyCode.DIGIT2){
				FloorProxy floor = FloorProxy.getFloor("FAULKNER", 2);
				if (floor != null) floorComboBox.getSelectionModel().select(floor);
			} else if (e.getCode() == KeyCode.DIGIT3){
				FloorProxy floor = FloorProxy.getFloor("FAULKNER", 3);
				if (floor != null) floorComboBox.getSelectionModel().select(floor);
			} else if (e.getCode() == KeyCode.DIGIT4){
				FloorProxy floor = FloorProxy.getFloor("FAULKNER", 4);
				if (floor != null) floorComboBox.getSelectionModel().select(floor);
			} else if (e.getCode() == KeyCode.DIGIT5){
				FloorProxy floor = FloorProxy.getFloor("FAULKNER", 5);
				if (floor != null) floorComboBox.getSelectionModel().select(floor);
			} else if (e.getCode() == KeyCode.DIGIT6){
				FloorProxy floor = FloorProxy.getFloor("FAULKNER", 6);
				if (floor != null) floorComboBox.getSelectionModel().select(floor);
			} else if (e.getCode() == KeyCode.DIGIT7){
				FloorProxy floor = FloorProxy.getFloor("FAULKNER", 7);
				if (floor != null) floorComboBox.getSelectionModel().select(floor);
			}
			e.consume();
		});
	}


	/**
	 * Initialize the floor's choice box with 1-7 (the floors)
	 * Ideally this shouldn't be hard coded
	 */
	private void initfloorComboBox() {
		this.floorComboBox.setItems(FXCollections.observableArrayList(FloorProxy.getFloors()));
		this.floorComboBox.getSelectionModel().selectedItemProperty().addListener(
				(ignored, ignoredOld, choice) -> this.changeFloor(choice));
		//this.floorComboBox.setConverter(FloorImage.FLOOR_STRING_CONVERTER); // <- for choiceBox, not comboBox

		this.floorComboBox.setValue(this.floorComboBox.getItems().get(this.directory.getFloorNum() - 1)); // default the selection to be whichever floor we start on
	}

	private void initDestinationTypeTabs() {
		resetRoomSearchResults();
		resetProfessionalSearchResults();

		destinationTypeTabs.getSelectionModel().select(servicesTab);

		destinationTypeTabs.getSelectionModel().selectedItemProperty().addListener(
				(ignored, old, selection) -> {
					System.out.println(destinationField);
					destinationField.setText("");
					if (selection == profTab) {
						destinationField.setPromptText("Choose a professional");
						destImageView.setImage(destProfImage);
					} else {
						destinationField.setPromptText("Choose destination");
						destImageView.setImage(destRoomImage);
					}
					if (selection != roomTab) {
						endRoom = null;
						enableOrDisableNavigationButtons();
						resetRoomSearchResults();
					}
				});

		// Set the selection actions for the search results
		this.roomSearchResults.getSelectionModel().selectedItemProperty().addListener(
				(ignored, oldValue, selection) -> this.selectRoomAction(selection));
		this.profSearchResults.getSelectionModel().selectedItemProperty().addListener(
				(ignored, oldValue, selection) -> {
					if (selection == null) {
						this.resetProfessionalSearchResults();
						return;
					}

					Set<Room> rooms = selection.getLocations();
					rooms.removeIf(r -> (! directory.isLoggedIn()) && r.getLocation().isRestricted());
					if (rooms.isEmpty()) {
						//no rooms for this professional
						Alert alert = new Alert(Alert.AlertType.INFORMATION);
						alert.setTitle("No Rooms Found");
						alert.setHeaderText(null);
						alert.setContentText("There are no rooms available for this professional. \nPlease change your selection and try again");
						alert.showAndWait();
					} else {
						this.roomSearchResults.setItems(FXCollections.observableArrayList(rooms));
						this.destinationTypeTabs.getSelectionModel().select(roomTab);
					}
				}
		);
		this.servicesList.getSelectionModel().selectedItemProperty().addListener(
				(ignored, oldValue, selection) -> {
					try {
						findService(selection);
					} catch (IOException | InvocationTargetException e) {
						System.err.println("Path found, but another error occurred");
						e.printStackTrace();
					}
				}
		);


	}

	@FXML
	public void logAsAdminClicked()
			throws IOException, InvocationTargetException {
		if(directory.isProfessional()){
			directory.logOut();
			changeFloor(directory.getFloor());
			logAsAdmin.setImage(new Image("/lock.png"));
		}else{
			this.timer.cancelTimer();
			Parent loginPrompt = (BorderPane) FXMLLoader.load(this.getClass().getResource("/LoginPrompt.fxml"));
			this.getScene().setRoot(loginPrompt);
		}
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
	private void displayRooms() {
		this.nodePane.getChildren().setAll(iconManager.getIcons(directory.getRoomsOnFloor()));
	}

	/**
	 * Reset the list of rooms
	 */
	private void resetRoomSearchResults() {
		Set<Room> rooms = directory.getUserRooms();
		rooms.removeIf(r -> r.getLocation() == null);
		this.roomSearchResults.getSelectionModel().clearSelection();
		this.roomSearchResults.setItems(FXCollections.observableArrayList(rooms));
	}

	/**
	 * Reset the list of professionals
	 */
	private void resetProfessionalSearchResults() {
		this.profSearchResults.setItems(FXCollections.observableArrayList(directory.getProfessionals()));
		this.profSearchResults.getSelectionModel().clearSelection();
	}

	private void setServicesList() {
		Set<Room> roomList = directory.getRooms();
		HashSet<RoomType> servicesAvailable = new HashSet<>();
		for (Room room: roomList){
			RoomType type = room.getType();
			if (!servicesAvailable.contains(type) && !type.equals(RoomType.DEFAULT) && !type.equals(RoomType.NONE) && !type.equals(RoomType.KIOSK) && !type.equals(RoomType.HALLWAY)){
				servicesAvailable.add(type);
			}
		}
		this.servicesList.setItems(FXCollections.observableArrayList(servicesAvailable));
	}



	/**
	 * Enable or disable the "get directions" and "set starting location" buttons
	 *
	 * If both start and end locations are set, enable the "get directions" button
	 *
	 * If The end room is set, enable the "set starting location" button
	 */
	private void enableOrDisableNavigationButtons() {
		if (this.getDirectionsBtn != null) {
			if (endRoom == null || startRoom == null) {
				this.getDirectionsBtn.setDisable(true);
			} else {
				this.getDirectionsBtn.setDisable(false);
			}
		}
	}




	@FXML
	public void getDirectionsClicked()
			throws IOException {
		// TODO: Find path before switching scene, so the "no path" alert returns to destination choice
		FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/UserPath.fxml"));
		BorderPane pane = loader.load();
		UserPathController controller = loader.getController();
		this.timer.cancelTimer();
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
	private void selectRoomAction(Room room) {
		if (room == null) return;
		if (this.selectingStart) {
			this.selectStartRoom(room);
		} else {
			this.selectEndRoom(room);
		}
	}

	private void selectStartRoom(Room r) {
		this.startRoom = r;
		this.enableOrDisableNavigationButtons();
		iconController.selectStartRoom(r);
//		iconManager.removeIcon(r);
		startField.setText(r.getName());
		this.displayRooms();
	}

	private void selectEndRoom(Room r) {
		this.endRoom = r;
		this.enableOrDisableNavigationButtons();
		iconController.selectEndRoom(r);
//		iconManager.removeIcon(r);
		destinationField.setText(r.getName());
		this.displayRooms();
	}

	private void setupSearchFields() {
		destinationField.setPromptText("Choose destination");

		this.destinationField.setOnKeyReleased(e -> this.filterRoomsOrProfessionals(this.destinationField.getText()));
		this.startField.setOnKeyReleased(e -> this.filterRoomsOrProfessionals(this.startField.getText()));

		this.destinationField.setOnKeyReleased(e -> {
			this.filterRoomsOrProfessionals(this.destinationField.getText());
			if(e.getCode() == KeyCode.ENTER) {
					if (startRoom == null) {
						this.startField.requestFocus();
					} else if (endRoom != null) {
						try {
							this.getDirectionsClicked();
						} catch (IOException e1) {
							e1.printStackTrace();
						}
					} else if (roomSearchResults.getItems().size() == 1) {
						endRoom = roomSearchResults.getItems().get(0);
						try {
							this.getDirectionsClicked();
						} catch (IOException e1) {
							e1.printStackTrace();
						}
					}
			}
		});

		this.startField.setOnKeyReleased(e -> {
			this.filterRoomsByName(this.startField.getText());
			if(e.getCode() == KeyCode.ENTER) {
				this.destinationField.requestFocus();
			}
		});

		startField.focusedProperty().addListener((ignored, old, nowFocused) -> {
			if (nowFocused) {
				this.selectingStart = true;
				this.startRoom = directory.getKiosk();
				startField.setText("");
				resetRoomSearchResults();
				destinationTypeTabs.getSelectionModel().select(roomTab);
			}
		});

		destinationField.focusedProperty().addListener((ignored, old, nowFocused) -> {
			if (nowFocused) {
				this.selectingStart = false;
				destinationField.setText("");
				resetRoomSearchResults();
				if (destinationTypeTabs.getSelectionModel().getSelectedItem() == servicesTab) {
					destinationTypeTabs.getSelectionModel().select(roomTab);
				}
			}
		});
	}


	@FXML
	public void aboutBtnClicked()
			throws IOException {
		UserAboutPage aboutPageController = new UserAboutPage();
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(this.getClass().getResource("/aboutPage.fxml"));
		Scene addAboutScene = new Scene(loader.load());
		Stage addAboutStage = new Stage();
		addAboutStage.setResizable(false);
		addAboutStage.setTitle("Faulkner Hospital Navigator About Page");
		addAboutStage.getIcons().add(new Image("bwhIcon.png"));
		addAboutStage.initOwner(contentAnchor.getScene().getWindow());
		addAboutStage.setScene(addAboutScene);
		addAboutStage.showAndWait();
		timer.emptyTasks();
		TimeoutTimer.getTimeoutTimer().registerTask(() -> {
			setState(directory.getCaretaker().getState());
		});
	}


	public void setupServiceButtons() {
//		findBathroomBtn.setOnAction(action -> this.findService(RoomType.BATHROOM));
	}

	private void findService(RoomType service)
			throws IOException, InvocationTargetException {
		try {
			Set<Room> services = this.directory.getRoomsOnFloor();
			services.removeIf(room -> room.getType() != service);

			int prevCost = 0;
			Room nearest = null;
			for (Room r : services) {
				List<Node> nodes = Pathfinder.findPath(startRoom.getLocation(), r.getLocation());
				if (prevCost == 0) {
					prevCost = nodes.size();
					nearest = r;
				}
				if (nodes.size() < prevCost) {
					prevCost = nodes.size();
					nearest = r;
				}
				System.out.println(r.getName());
			}
			selectEndRoom(nearest);
			this.getDirectionsClicked();
		} catch (PathNotFoundException e) {
			Alert alert = new Alert(Alert.AlertType.INFORMATION);
			alert.setTitle("No Path Found");
			alert.setHeaderText(null);
			alert.setContentText("There is no existing path to your destination. \n" +
					"Please check your start and end location and try again");
			alert.showAndWait();
			return;
		}
	}


	private void initFocusTraversables() {
		this.floorComboBox.setFocusTraversable(false);
		this.getDirectionsBtn.setFocusTraversable(false);
		this.aboutBtn.setFocusTraversable(false);
		this.helpBtn.setFocusTraversable(false);
		this.zoomSlider.setFocusTraversable(false);
	}
}



