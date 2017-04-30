package controllers.user;

import com.jfoenix.controls.*;
import entities.FloorProxy;

import entities.Node;
import entities.Professional;
import entities.RoomType;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Set;
import java.text.Collator;
import java.text.Normalizer;
import java.util.*;
import java.util.function.Predicate;

import org.apache.commons.lang3.StringUtils;

import entities.Room;
import javafx.stage.Stage;
import main.ApplicationController;
import main.algorithms.PathNotFoundException;
import main.algorithms.Pathfinder;


public class UserMasterController
		extends DrawerController
		implements Initializable
{
	@FXML private ImageView logAsAdmin;
	@FXML private TabPane destinationTypeTabs;
	@FXML private JFXListView<Room> roomSearchResults;
	@FXML private JFXListView<Professional> profSearchResults;
	@FXML private JFXListView<RoomType> commonServicesView;
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
	@FXML private JFXToggleButton professionalSearchToggleBtn;
	@FXML private JFXButton helpBtn;
	@FXML private JFXButton findBathroomBtn;

	private Room startRoom;
	private Room endRoom;

	private boolean selectingStart = false;

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
		initDestinationTypeTabs();

		this.displayRooms();

		setScrollZoom();
		setHotkeys();
		setupSearchFields();

		// Slightly delay the call so that the bounds aren't screwed up
		Platform.runLater(() -> {
			initWindowResizeListener();
			resizeDrawerListener(drawerParentPane.getHeight());
		});

		Platform.runLater(this::fitMapSize);

		// Enable search; if this becomes more than one line, make it a function
		this.destinationField.setOnKeyReleased(e -> this.filterRoomsByName(this.destinationField.getText()));
		this.startField.setOnKeyReleased(e -> this.filterRoomsByName(this.startField.getText()));

		if(directory.isProfessional()){
			logAsAdmin.setImage(new Image("/logout.png"));
		}else{
			logAsAdmin.setImage(new Image("/lock.png"));
		}
		startImageView.setImage(new Image("/aPin.png"));
		destImageView.setImage(new Image("/bPin.png"));
		aboutBtn.setImage(new Image("/about.png"));

		resizeDrawerListener(677.0);

		mainDrawer.open();

		//Enable panning again
		floatingBorderPane.setPickOnBounds(false);

		this.initFocusTraversables();
	}

	private void resizeDrawerListener(Double newSceneHeight) {
		drawerParentPane.heightProperty().addListener((ignored, old, newHeight) -> resizeDrawerListener((double)newHeight));
		destinationTypeTabs.setPrefHeight(newSceneHeight - startHBox.getHeight() - destHBox.getHeight() - goHBox.getHeight() - bottomHBox.getHeight());
	}

	private void setStyleIDs() {
		startHBox.getStyleClass().add("hbox");
		destHBox.getStyleClass().add("hbox");
		goHBox.getStyleClass().add("hbox-go");
		bottomHBox.getStyleClass().addAll("hbox", "hbox-bottom");
		getDirectionsBtn.getStyleClass().add("jfx-button");
		topToolBar.getStyleClass().add("tool-bar");
		drawerParentPane.getStyleClass().add("drawer");
		helpBtn.getStyleClass().add("blue-button");
	}


	private void initializeIcons() {
		iconManager.setOnMouseClickedOnSymbol((room, event) -> {
			if (event.getButton() == MouseButton.PRIMARY) this.selectRoomAction(room);
			event.consume();
		});
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

		// Set the selection actions for the search results
		this.roomSearchResults.getSelectionModel().selectedItemProperty().addListener(
				(ignored, oldValue, selection) -> this.selectRoomAction(selection));
		this.profSearchResults.getSelectionModel().selectedItemProperty().addListener(
				(ignored, oldValue, selection) -> {
					Set<Room> rooms = selection.getLocations();
					rooms.removeIf(r -> (! directory.isLoggedIn()) && r.getLocation().isRestricted());
					this.roomSearchResults.setItems(FXCollections.observableArrayList(rooms));
					this.destinationTypeTabs.getSelectionModel().select(roomTab);
				}
		);

		this.findBathroomBtn.addEventHandler(ActionEvent.ACTION, event -> {
			try {
				this.findService(RoomType.BATHROOM);
			} catch (IOException | PathNotFoundException | InvocationTargetException e) {
				e.printStackTrace();
			}
		});
	}

	@FXML
	public void logAsAdminClicked()
			throws IOException, InvocationTargetException {
		if(directory.isProfessional()){
			directory.logOut();
			changeFloor(directory.getFloor());
			logAsAdmin.setImage(new Image("/lock.png"));
		}else{
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
		this.roomSearchResults.setItems(FXCollections.observableArrayList(directory.filterRooms(r -> r.getLocation() != null)));
		this.roomSearchResults.getSelectionModel().clearSelection();
	}

	/**
	 * Reset the list of professionals
	 */
	private void resetProfessionalSearchResults() {
		this.profSearchResults.setItems(FXCollections.observableArrayList(directory.getProfessionals()));
		this.profSearchResults.getSelectionModel().clearSelection();
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
		startField.setText(r.getName());
		this.displayRooms();
	}

	private void selectEndRoom(Room r) {
		this.endRoom = r;
		this.enableOrDisableNavigationButtons();
		iconController.selectEndRoom(r);
		destinationField.setText(r.getName());
		this.displayRooms();
	}

	private void setupSearchFields() {
		destinationField.setPromptText("Choose destination");

		this.destinationField.setOnKeyReleased(e -> {
			this.filterRoomsOrProfessionals(this.destinationField.getText());
			if(e.getCode() == KeyCode.ENTER) {
				//
					if (startRoom == null) {
						this.startField.requestFocus();
					} else if (endRoom != null) {
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
				resetRoomSearchResults();
				destinationTypeTabs.getSelectionModel().select(roomTab);
			}
		});

		destinationField.focusedProperty().addListener((ignored, old, nowFocused) -> {
			if (nowFocused) {
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
		addAboutStage.setTitle("Faulkner Hospital Navigator About Page");
		addAboutStage.getIcons().add(new Image("bwhIcon.png"));
		addAboutStage.initOwner(contentAnchor.getScene().getWindow());
		addAboutStage.setScene(addAboutScene);
		addAboutStage.showAndWait();
	}


	@FXML
	public void findService(RoomType service)

			throws IOException, InvocationTargetException, PathNotFoundException {
		Set<Room> services = this.directory.getRoomsOnFloor();
		services.removeIf(room -> room.getType() != service);

		int prevCost = 0;
		Room nearest = null;
		for(Room r: services){
			List<Node> nodes = Pathfinder.findPath(startRoom.getLocation(), r.getLocation());
			if(prevCost == 0) {
				prevCost = nodes.size();
				nearest = r;
			}
			if(nodes.size() < prevCost){
				prevCost = nodes.size();
				nearest = r;
			}
			System.out.println(r.getName());
		}
		selectEndRoom(nearest);
		this.getDirectionsClicked();
	}


	private void initFocusTraversables() {
		this.floorComboBox.setFocusTraversable(false);
		this.getDirectionsBtn.setFocusTraversable(false);
		this.aboutBtn.setFocusTraversable(false);
		this.helpBtn.setFocusTraversable(false);
		this.zoomSlider.setFocusTraversable(false);
	}
}



