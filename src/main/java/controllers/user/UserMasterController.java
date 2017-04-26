package controllers.user;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.transitions.hamburger.HamburgerBackArrowBasicTransition;
import entities.FloorProxy;
import controllers.shared.MapDisplayController;

import entities.Node;
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
import java.util.*;

import entities.Room;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import main.ApplicationController;
import main.DirectionsGenerator;
import main.algorithms.PathNotFoundException;
import main.algorithms.Pathfinder;
import org.w3c.dom.css.Rect;


public class UserMasterController
		extends MapDisplayController
		implements Initializable
{
	//@FXML private JFXButton logAsAdmin;
	@FXML private ListView<Room> directoryView;
	@FXML private Button getDirectionsBtn;
	@FXML private Button changeStartBtn;
	@FXML private Pane nodePane;
	@FXML public Pane linePane;
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
	protected static Room startRoom;
	protected static Room endRoom;

	public boolean isStart;
	public boolean isDest;
	private Rectangle bgRectangle;

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
		System.out.println("this.linePane = " + this.linePane);
		System.out.println("this.nodePane = " + this.nodePane);
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
					startField.setText(room.getName());
				});
				MenuItem endRoomItem = new MenuItem("Set as destination");
				endRoomItem.setOnAction(e2-> {
					selectEndRoom(room);
					destinationField.setText(room.getName());
				});
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

	/** The main method to call if you want to display the directions on the imageView.
	 * The helper methods are below it
	 *
	 * @param startRoom
	 * @param endRoom
	 */
	public void drawDirections(Room startRoom, Room endRoom) {
		if ((startRoom == null) || (endRoom == null)) {
			return;
		}

		LinkedList<LinkedList<Node>> pathSegments = new LinkedList<>();

		Node startNode = startRoom.getLocation();
		UserMasterController.MiniFloor startFloor = new UserMasterController.MiniFloor(startNode.getFloor(), startNode.getBuildingName());
//		this.changeFloor(FloorProxy.getFloor(startNode.getBuildingName(), startNode.getFloor()));

		List<Node> path= this.getPathOrAlert(startRoom, endRoom);
		if (path == null) {
			return;
		}

		/* Draw the buttons for each floor on a multi-floor path. */
		// segment paths by floor and place them in a LinkedList
		LinkedList<Node> seg = new LinkedList<>();
		for(int i = 0; i < path.size()-1; i++){
			seg.add(path.get(i));
			if((path.get(i).getFloor() != path.get(i+1).getFloor()) ||
					!(path.get(i).getBuildingName().equals(path.get(i+1).getBuildingName()))){
				pathSegments.add(seg);
				// TODO: Look over this
				seg = new LinkedList<>();
			}
		}
		seg.add(path.get(path.size()-1));
		pathSegments.addLast(seg);
		System.out.println("pathSegments = " + pathSegments);
		System.out.println("pathSegments = " + pathSegments.get(0));
		paintPath(pathSegments.get(0));
		// pathSegment now has all segments
		drawMiniMaps(path, pathSegments);
	}

	/**
	 * Get a path between the given rooms, showing an alert if there is no path
	 *
	 * @return A list of nodes representing the path, or null if no path is found
	 */
	private List<Node> getPathOrAlert(Room startRoom, Room endRoom) {
		try {
			return Pathfinder.findPath(startRoom.getLocation(), endRoom.getLocation());
		} catch (PathNotFoundException e) {
			Alert alert = new Alert(Alert.AlertType.INFORMATION);
			alert.setTitle("No Path Found");
			alert.setHeaderText(null);
			alert.setContentText("There is no existing path to your destination. \n" +
					"Please check your start and end location and try again");
			alert.showAndWait();

			return null;
		}
	}

	/**
	 * Inner class for generating and comparing floors quickly
	 */
	// TODO: Refactor out in favor of real Floors
	class MiniFloor
	{
		int number;
		String building;
		MiniFloor(int number, String building) {
			this.number = number;
			this.building = building;
		}
		public boolean isSameFloor(UserMasterController.MiniFloor other) {
			return (other != null) && (this.number == other.number) &&
					this.building.equalsIgnoreCase(other.building);
		}
	}

	/**
	 * Draw a simple path between the nodes in the given list
	 *
	 * @param directionNodes A list of the nodes in the path, in order
	 */
	// TODO: Fix bug where separate paths on one floor are connected
	public void paintPath(List<Node> directionNodes) {
		// This can be any collection type;
		Collection<Arrow> path = new HashSet<>();
		for (int i=0; i < directionNodes.size()-1; ++i) {
			Node here = directionNodes.get(i);
			Node there = directionNodes.get(i+1);
			if (here.getFloor() == this.directory.getFloorNum() && here.getFloor() == there.getFloor()) {
				Line line = new Line(here.getX(), here.getY(), there.getX(), there.getY());

				line.setStrokeWidth(4.0);
				line.setStroke(Color.MEDIUMVIOLETRED);
				path.add(new Arrow(line));
			}
		}
		System.out.println("this.linePane = " + this.linePane);
		System.out.println("this.linePane.getChildren() = " + this.linePane.getChildren());
		this.linePane.getChildren().setAll(path);
	}

	/**
	 * Draw the minimaps for floor-switching
	 *
	 * @param path The path to draw floors for
	 */
	private void drawMiniMaps(List<Node> path, LinkedList<LinkedList<Node>> pathSegments) {
		List<UserMasterController.MiniFloor> floors = new ArrayList<>();
		UserMasterController.MiniFloor here = new UserMasterController.MiniFloor(path.get(0).getFloor(), path.get(0).getBuildingName());
		// add starting floor
		floors.add(here);
		for (int i = 0; i < pathSegments.size(); i++) {
			Node n = pathSegments.get(i).get(1);
			here = new UserMasterController.MiniFloor(n.getFloor(), n.getBuildingName());
			floors.add(here);
			LinkedList<Node> seg = pathSegments.get(i);
			this.createNewFloorButton(here, seg, floors.size());
		}

//		this.createNewFloorButton(here, this.getPathOnFloor(here, path), floors.size());
//
//		for (int i = 1; i < path.size()-1; ++i) {
//			last = here;
//			here = new MiniFloor(path.get(i).getFloor(), path.get(i).getBuildingName());
//			next = new MiniFloor(path.get(i+1).getFloor(), path.get(i+1).getBuildingName());
//
//			// Check when there is a floor A -> floor B -> floor B transition and save floor B
//			if ((last.number != here.number && next.number == here.number) || ! last.building.equalsIgnoreCase(here.building)) {
//				floors.add(here);
//				this.createNewFloorButton(here, this.getPathOnFloor(here, path), floors.size());
//			}
//		}
//		// Check that the last node's floor (which will always be 'next') is in the list
//		last = floors.get(floors.size()-1);
//		if (! last.isSameFloor(next)) {
//			floors.add(next);
//			this.createNewFloorButton(next, this.getPathOnFloor(next, path), floors.size());
//		}
	}

	private void createNewFloorButton(UserMasterController.MiniFloor floor, List<Node> path, int buttonCount) {
//		ImageView newFloorButton = new ImageView();
//
//		int buttonWidth = 110;
//		int buttonHeight = 70;
//		int buttonSpread = 140;
//		int buttonY = (int)floorsTraveledAnchorPane.getHeight()/2 + 15;
//		int centerX = 0;
//
//
//		newFloorButton.setLayoutX(floorsTraveledAnchorPane.getLayoutX() + centerX + (buttonSpread)*buttonCount);
//		newFloorButton.setLayoutY(buttonY);
//		newFloorButton.setFitWidth(buttonWidth);
//		newFloorButton.setFitHeight(buttonHeight);
//		FloorProxy map = FloorProxy.getFloor(floor.building, floor.number);
//
//		newFloorButton.setImage(map.displayThumb());
//		newFloorButton.setPickOnBounds(true);
//
//		Rectangle backgroundRectangle = new Rectangle();
//		backgroundRectangle.setWidth(buttonWidth*1.25);
//		backgroundRectangle.setHeight(buttonHeight*1.25);
//		backgroundRectangle.setX(floorsTraveledAnchorPane.getLayoutX() + centerX + (buttonSpread)*buttonCount-10);
//		backgroundRectangle.setY(buttonY - 10);
//		backgroundRectangle.setFill(Color.WHITE);
//		backgroundRectangle.setStroke(Color.BLACK);
//		backgroundRectangle.setStrokeWidth(5);
//
//		newFloorButton.setOnMouseClicked(e-> {
//			// change to the new floor, and draw the path for that floor
//			this.changeFloor(FloorProxy.getFloor(floor.building, floor.number));
//			this.paintPath(path);
//			//Call text directions
//			this.directionsTextField.getChildren().add(textDirections);
//			if(this.bgRectangle != null) this.bgRectangle.setVisible(false);
//			backgroundRectangle.setVisible(true);
//			this.bgRectangle = backgroundRectangle;
//		});
//		if(buttonCount-1 > 1) {
//			backgroundRectangle.setVisible(false);
//		} else {
//			this.bgRectangle = backgroundRectangle;
//			backgroundRectangle.setVisible(true);
//		}
//		floorsTraveledAnchorPane.getChildren().add(backgroundRectangle);
//		floorsTraveledAnchorPane.getChildren().add(newFloorButton);
	}
}
