package controllers.user;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.transitions.hamburger.HamburgerBackArrowBasicTransition;
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
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.text.Collator;
import java.text.Normalizer;
import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.Set;

import entities.Room;
import javafx.stage.Stage;
import main.ApplicationController;


public class UserMasterController
		extends MapDisplayController
		implements Initializable
{
	//@FXML private JFXButton logAsAdmin;
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
	//@FXML private Button aboutBtn;
	@FXML private ImageView logoImageView;
	@FXML protected JFXDrawer navDrawer;
	@FXML private JFXHamburger navHamburgerBtn;

	////NavDrawer Elements
	@FXML protected JFXTextField startField;
	@FXML protected VBox drawerVBox;
	@FXML protected JFXTextField destinationField;

	private double clickedX;
	private double clickedY;
	protected Room startRoom;
	protected Room endRoom;

	public boolean isStart;
	public boolean isDest;

	HamburgerBackArrowBasicTransition back;





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

		this.changeFloor(this.directory.getFloor());
		this.imageViewMap.setPickOnBounds(true);

		// Set buttons to default
		//this.enableOrDisableNavigationButtons();

		// TODO: Set zoom based on window size
		zoomSlider.setValue(0);
		setZoomSliding();

		initfloorComboBox();

		this.displayRooms();
		iconController.resetAllRooms();

		//this.populateListView();

		setScrollZoom();

		// TODO: See if there's a way to include this in the OnMouseDragged listener

		setMouseMapListeners();

		// TODO: Use ctrl+plus/minus for zooming
		setHotkeys();



		//Set the content for the slide out drawer
		setDrawerContents();
		back = new HamburgerBackArrowBasicTransition();
		back.setRate(-1);



		// Slightly delay the call so that the bounds aren't screwed up
		Platform.runLater( () -> initWindowResizeListener());
//		Platform.runLater( () -> this.fitMapSize());
		// Enable search; if this becomes more than one line, make it a function
		//this.searchBar.textProperty().addListener((ignored, ignoredOld, contents) -> this.filterRoomsByName(contents));

	}

	private void setDrawerContents() {
		try {
			FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/NavDrawer.fxml"));
			VBox box = loader.load();
			NavDrawerController controller = loader.getController();
			this.destinationField = controller.destinationField;
			this.startField = controller.startField;
			box.getStylesheets().add("/User_Style1.css");
			navDrawer.setSidePane(box);
			//navDrawer.prefHeightProperty().bind(parentBorderPane.heightProperty());

			//navDrawer.prefWidthProperty().bind(parentBorderPane.widthProperty());
		} catch (IOException ex) {
			//Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
	@FXML
	public void onNavHamburgerBtnClicked() throws IOException {
		back.setRate(back.getRate() * -1);
		back.play();
		if(navDrawer.isShown()) {

			navDrawer.close();
		} else {
			navDrawer.open();
		}
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
		});
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
		Set<javafx.scene.Node> roomShapes = new HashSet<>();
		for (Room room : directory.getRoomsOnFloor(directory.getFloor())) {
			System.out.println("Adding room: " + room.getName() );
			roomShapes.add(room.getUserSideShape());
			// Add listener to select rooms on click
			roomClickListener(room);

			// Add listener for context menus (right click)
			room.getUserSideShape().getSymbol().setOnContextMenuRequested(e -> {

				ContextMenu optionsMenu = new ContextMenu();

				MenuItem startRoomItem = new MenuItem("Set as starting location");
				startRoomItem.setOnAction(e1 -> {
					selectStartRoom(room);
					//startField.setText(room.getName());
				});
				MenuItem endRoomItem = new MenuItem("Set as destination");
				endRoomItem.setOnAction(e2-> selectEndRoom(room));
				optionsMenu.getItems().addAll(startRoomItem, endRoomItem);
				optionsMenu.show(room.getUserSideShape(), e.getScreenX(), e.getScreenY());
			});
		}
		this.nodePane.getChildren().setAll(roomShapes);
	}

	public void roomClickListener(Room room) {
		room.getUserSideShape().getSymbol().setOnMouseClicked((MouseEvent e) -> {
			if (e.getButton() == MouseButton.PRIMARY) {
				if (isStart) {
					this.selectStartRoom(room);
					System.out.println("startroom = " + room.getName());
					startField.setText(room.getName());
				}
				if (isDest) {
					this.selectEndRoom(room);
					System.out.println("endroom = " + room.getName());
					destinationField.setText(room.getName());
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
//	protected void enableOrDisableNavigationButtons() {
//		if (this.getDirectionsBtn != null) {
//			if (endRoom == null || startRoom == null) {
//				this.getDirectionsBtn.setDisable(true);
//			} else {
//				this.getDirectionsBtn.setDisable(false);
//			}
//		}
//		if (this.changeStartBtn != null) {
//			this.changeStartBtn.setDisable((endRoom != null) ? false : true);
//		}
	//}



	/*
	 * Below are helper methods to select and deselect the starting rooms for a path
	 */

	/**
	 * Function called to select a room
	 */
//	protected void selectRoomAction(Room room) {
//		if (this.changeStartBtn.isDisabled()) {
//
//			this.changeStartBtn.setDisable(false);
//		} else {
//			this.selectEndRoom(room);
//		}
//	}

//	@FXML
//	public void changeStartClicked() throws IOException, InvocationTargetException {
//		System.out.println(this.changeStartBtn.isDisable() +", "+this.changeStartBtn.isDisabled());
//		this.changeStartBtn.setDisable(true);
//		System.out.println(this.changeStartBtn.isDisable() +", "+this.changeStartBtn.isDisabled());
//	}

	protected void selectStartRoom(Room r) {
		if(r == null) return;
		startRoom = r;
		//this.enableOrDisableNavigationButtons();
//		this.enableDirectionsBtn();
		System.out.println("Start: " + r.getName());

		//iconController.selectStartRoom(r);
		//this.displayRooms();
	}

	protected void selectEndRoom(Room r) {
		if(r == null) return;
		endRoom = r;

		//this.enableOrDisableNavigationButtons();
//		this.enableDirectionsBtn();
//		this.enableChangeStartBtn();
		System.out.println("End: " + r.getName());
		//iconController.selectEndRoom(r);
		//this.displayRooms();
	}

//	@FXML
//	public void aboutBtnClicked () throws IOException {
//		UserAboutPage aboutPageController = new UserAboutPage();
//		FXMLLoader loader = new FXMLLoader();
//		loader.setLocation(this.getClass().getResource("/aboutPage.fxml"));
//		Scene addAboutScene = new Scene(loader.load());
//		Stage addAboutStage = new Stage();
//		addAboutStage.initOwner(contentAnchor.getScene().getWindow());
//		addAboutStage.setScene(addAboutScene);
//		addAboutStage.showAndWait();
//	}
}
