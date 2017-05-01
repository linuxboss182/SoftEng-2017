package controllers.user;

import com.jfoenix.controls.*;
import controllers.extras.SMSController;
import controllers.icons.Icon;
import controllers.icons.IconManager;
import entities.Direction;
import entities.FloorProxy;
import entities.Node;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.*;

import entities.Room;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import main.ApplicationController;
import main.DirectionsGenerator;
import main.TimeoutTimer;
import main.algorithms.PathNotFoundException;
import main.algorithms.Pathfinder;

// TODO: Put directions in a scroll box
// TODO: Generally improve text directions (see below)
/*
Remove recurring directions

If possible, use things like "follow the sidewalk" (probably not possible)

If possible, get turn direction upon exiting a building (requires changes elsewhere)
 */

public class UserPathController
		extends DrawerController
		implements Initializable
{
	@FXML private JFXButton logAsAdmin;
	@FXML private JFXButton sendToPhoneBtn;
	@FXML protected Pane linePane;
	@FXML private Pane nodePane;
	@FXML private BorderPane parentBorderPane;
	@FXML private SplitPane mapSplitPane;
	@FXML private ImageView logoImageView;
	@FXML private Button doneBtn;
	@FXML private AnchorPane floorsTraveledAnchorPane;
	@FXML private Label startLbl;
	@FXML private HBox directionsLblHBox;
	@FXML private ImageView startImageView;
	@FXML private JFXListView<Direction> directionsListView;
	@FXML private HBox destLblHBox;
	@FXML private Label destLbl;
	@FXML private Label directionsLbl;
	@FXML private VBox pathVBox;
	@FXML private HBox doneBtnHBox;
	@FXML private HBox startLblHBox;
	@FXML private ImageView destImageView;
	@FXML private JFXToolbar topToolBar;
	@FXML private BorderPane floatingBorderPane;
	@FXML private  JFXButton helpBtn;

	private static final double PATH_WIDTH = 2.0;
	private double clickedX;
	private double clickedY;
	private List<Direction> directions;
	private Text textDirections = new Text();
	private Rectangle bgRectangle = null;
	private LinkedList<List<Node>> pathSegments = new LinkedList<>();



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
		public boolean isSameFloor(MiniFloor other) {
			return (other != null) && (this.number == other.number) &&
					this.building.equalsIgnoreCase(other.building);
		}
	}


	@Override
	public void initialize(URL location, ResourceBundle resources) {
		super.initialize();
//		this.initializeDrawer();
		floatingBorderPane.setPickOnBounds(false);

		this.iconManager = new IconManager();
		iconManager.setOnMouseClickedOnPathSegmentEnd((r, e) -> {
			Node node = r.getLocation();
			int floorNum = node.getFloor();
			String buildingName = node.getBuildingName();
			FloorProxy floor = FloorProxy.getFloor(buildingName, floorNum);
			this.changeFloor(floor);
		});
		// iconManager.getIcons(directory.getRooms());

		// iconController.resetAllRooms();

		setScrollZoom();
		setStyleIDs();


		//backImageView.setImage(new Image("/back.png"));
		startImageView.setImage(new Image("/aPin.png"));
		destImageView.setImage(new Image("/bPin.png"));


		Platform.runLater(() -> {
			resizeDrawerListener();
		});


		this.timer.resetTimer();

		this.setUpDirectionListView();
	}

	private void resizeDrawerListener() {
		directionsListView.setPrefHeight(drawerParentPane.getHeight() - startLblHBox.getHeight() - destLblHBox.getHeight() - directionsLblHBox.getHeight() - doneBtnHBox.getHeight());

		drawerParentPane.heightProperty().addListener((ignored, old, newHeight) -> {
			directionsListView.setPrefHeight((double)newHeight - startLblHBox.getHeight() - destLblHBox.getHeight() - directionsLblHBox.getHeight() - doneBtnHBox.getHeight());
			System.out.println("listView Height: "+ directionsListView.getHeight());
			System.out.println("drawerParentPane: " + drawerParentPane.getHeight());
		});
	}

	private void setUpDirectionListView() {
		directionsListView.setCellFactory(d -> new ListCell<Direction>() {
			private final ImageView icon = new ImageView();
			@Override
			public void updateItem(Direction direction, boolean empty) {
				super.updateItem(direction, empty);
				if(empty) {
					setText(null);
					setGraphic(null);
				} else {
					setText(direction.getTextDirection());
					this.icon.setImage(direction.getIcon().getImage());
					this.icon.setFitHeight(20);
					this.icon.setFitWidth(20);
					setGraphic(this.icon);
				}
			}
		});

		directionsListView.setMouseTransparent(true);
		directionsListView.setFocusTraversable(false);
	}

//	private void initializeDrawer() {
////		this.mapIconDrawer.setContent(mapScroll);
////		this.mapIconDrawer.setSidePane(floorsTraveledAnchorPane);
////		this.mapIconDrawer.setOverLayVisible(false);
////		this.mapIconDrawer.open();
//	}


	public void setStyleIDs() {
		startLblHBox.getStyleClass().add("hbox");
		destLblHBox.getStyleClass().add("hbox");
		directionsLblHBox.getStyleClass().add("hbox-go");
		topToolBar.getStyleClass().add("tool-bar");
		drawerParentPane.getStyleClass().add("drawer");
		startLbl.getStyleClass().add("label-path");
		destLbl.getStyleClass().add("label-path");
		sendToPhoneBtn.getStyleClass().add("jfx-button");
		directionsLbl.getStyleClass().add("label-directions");
		doneBtn.getStyleClass().add("jfx-button-blue");
		textDirections.setFont(Font.font("Roboto", 15));

	}

	private void setContentAnchorListeners() {
		// Redraw rooms when the background is released
		// TODO: Fix bug where clicking rooms un-draws them
		contentAnchor.setOnMouseReleased(event -> {
			iconController.resetAllRooms();
			this.displayRooms();
		});
	}


	/**
	 * Attempt to set up this scene in preparation to display a path between the given rooms
	 *
	 * If either room is null or the path does not exist, an empty Optional is returned.
	 *
	 * If the path does not exist, an alert is displayed.
	 *
	 * @param startRoom The room to start from
	 * @param endRoom The room to end from
	 * @return this if successful, or an empty Optional if an error occurs
	 */
	boolean preparePathSceneSuccess(Room startRoom, Room endRoom) {
		if ((startRoom == null) || (endRoom == null)) {
			return false;
		}
		startLbl.setText(startRoom.getName());
		destLbl.setText(endRoom.getName());

		Node startNode = startRoom.getLocation();
		MiniFloor startFloor = new MiniFloor(startNode.getFloor(), startNode.getBuildingName());

		List<Node> path= this.getPathOrAlert(startRoom, endRoom);
		if (path == null) {
			return false;
		}
		this.directionsListView.getItems().clear();
		directions = DirectionsGenerator.fromPath(path);
		this.directionsListView.setItems(FXCollections.observableList(directions));

		/* Draw the buttons for each floor on a multi-floor path. */
		// segment paths by floor and place them in a LinkedList
		List<Node> seg = new LinkedList<>();
		for(int i = 0; i < path.size()-1; i++){
			seg.add(path.get(i));
			if((path.get(i).getFloor() != path.get(i+1).getFloor()) ||
					!(path.get(i).getBuildingName().equals(path.get(i+1).getBuildingName()))){
				pathSegments.add(seg);
				seg = new LinkedList<>();
			}
		}
		seg.add(path.get(path.size()-1));
		pathSegments.addLast(seg); // pathSegment now has all segments

		this.changeFloor(FloorProxy.getFloor(startNode.getBuildingName(), startNode.getFloor()));
		paintPath(pathSegments.get(0));
		iconManager.getLinkedPathIcons(pathSegments);
		this.displayRooms();
		drawMiniMaps(path);

		return true;
	}

	/**
	 * Draw the minimaps for floor-switching
	 *
	 * @param path The path to draw floors for
	 */
	private void drawMiniMaps(List<Node> path) {
		List<MiniFloor> floors = new ArrayList<>();
		MiniFloor here = new MiniFloor(path.get(0).getFloor(), path.get(0).getBuildingName());
		// add starting floor
		floors.add(here);
		for (int i = 0; i < pathSegments.size(); i++) {
			if((pathSegments.get(i).size() > 1)) {
				Node n = pathSegments.get(i).get(0);
				here = new MiniFloor(n.getFloor(), n.getBuildingName());
				floors.add(here);
				List<Node> seg = pathSegments.get(i);
				this.createNewFloorButton(here, seg, floors.size());
			}
		}
		// take into account the fact that an elevator or staircase could be chosen as
		// the destination and would (possibly) have only one node shown in the
		// path list
		if(pathSegments.getLast().size() == 1){
			Node n = pathSegments.getLast().get(0);
			here = new MiniFloor(n.getFloor(), n.getBuildingName());
			floors.add(here);
			List<Node> seg = pathSegments.getLast();
			this.createNewFloorButton(here, seg, floors.size());
		}
	}

	private void createNewFloorButton(MiniFloor floor, List<Node> path, int buttonCount) {
		ImageView newFloorButton = new ImageView();
		Label newFloorLabel = new Label();

		int buttonWidth = 110;
		int buttonHeight = 70;
		int buttonSpread = 140;
		int buttonY = (int)floorsTraveledAnchorPane.getHeight()/2 + 15;
		int buttonCenterX = 430;

		int labelCenterX = 460;
		int labelY = (int)floorsTraveledAnchorPane.getHeight()/2 + 95; //change the +x value so the label is at the bottom


		newFloorLabel.setLayoutX(floorsTraveledAnchorPane.getLayoutX() + labelCenterX + (buttonSpread)*buttonCount);
		newFloorLabel.setLayoutY(labelY);
		newFloorLabel.setText(floor.building);

		newFloorButton.setLayoutX(floorsTraveledAnchorPane.getLayoutX() + buttonCenterX + (buttonSpread)*buttonCount);
		newFloorButton.setLayoutY(buttonY);
		newFloorButton.setFitWidth(buttonWidth);
		newFloorButton.setFitHeight(buttonHeight);
		FloorProxy map = FloorProxy.getFloor(floor.building, floor.number);

		newFloorButton.setImage(map.displayThumb());
		newFloorButton.setPickOnBounds(true);

		Rectangle backgroundRectangle = new Rectangle();
		backgroundRectangle.setWidth(buttonWidth*1.25);
		backgroundRectangle.setHeight(buttonHeight*1.25);
		backgroundRectangle.setX(floorsTraveledAnchorPane.getLayoutX() + buttonCenterX + (buttonSpread)*buttonCount-10);
		backgroundRectangle.setY(buttonY - 10);
		backgroundRectangle.setFill(Color.WHITE);
		backgroundRectangle.setStroke(Color.BLACK);
		backgroundRectangle.setStrokeWidth(5);

		newFloorButton.setOnMouseClicked(e-> {
			// change to the new floor, and draw the path for that floor
			this.changeFloor(FloorProxy.getFloor(floor.building, floor.number));
			this.paintPath(path);

			if(this.bgRectangle != null) this.bgRectangle.setVisible(false);
			backgroundRectangle.setVisible(true);
			this.bgRectangle = backgroundRectangle;
		});
		if(buttonCount-1 > 1) {
			backgroundRectangle.setVisible(false);
		} else {
			this.bgRectangle = backgroundRectangle;
			backgroundRectangle.setVisible(true);
		}
		floorsTraveledAnchorPane.getChildren().add(backgroundRectangle);
		floorsTraveledAnchorPane.getChildren().add(newFloorButton);
		floorsTraveledAnchorPane.getChildren().add(newFloorLabel);
	}

	// TODO: Draw by segments, not by floors
	private ArrayList<Node> getPathOnFloor(MiniFloor floor, List<Node> allPath) {
		ArrayList<Node> path = new ArrayList<>();
		for(Node n : allPath) {
			if (n.getFloor() == floor.number && n.getBuildingName().equalsIgnoreCase(floor.building)) {
				path.add(n);
			}
		}
		return path;
	}

	@FXML
	public void doneBtnClicked() throws IOException {
//		startRoom.getUserSideShape().setScaleX(1);
//		startRoom.getUserSideShape().setScaleY(1);
//		endRoom.getUserSideShape().setScaleX(1);
//		endRoom.getUserSideShape().setScaleY(1);

		iconController.resetAllRooms();
		Parent userPath = (BorderPane) FXMLLoader.load(this.getClass().getResource("/UserDestination.fxml"));
		this.mapScroll.getScene().setRoot(userPath);
	}

//	@FXML
//	public void backBtnClicked() throws IOException {
//		iconController.resetAllRooms();
//		Parent userPath = (BorderPane) FXMLLoader.load(this.getClass().getResource("/UserDestination.fxml"));
//		this.mapScroll.getScene().setRoot(userPath);
//	}

	@FXML
	public void sendSMSBtnClicked(){
//		Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
//		alert.setTitle("Information Dialog");
//		alert.setHeaderText("Feature Unavailable");
//		alert.setContentText("Sorry, SMS is currently unavailable.");
//		alert.showAndWait();

		FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/sms.fxml"));
		try {
			Scene smsScene = new Scene(loader.load());
			((SMSController)loader.getController()).setText(textDirections.getText());
			Stage smsStage = new Stage();
			smsStage.setTitle("Faulkner Hospital Navigator SMS Page");
			smsStage.getIcons().add(new Image("bwhIcon.png"));
			smsStage.initOwner(floorsTraveledAnchorPane.getScene().getWindow());
			smsStage.setScene(smsScene);
			smsStage.showAndWait();
		} catch (IOException e) {
			System.out.println("Error making SMS popup");
			throw new RuntimeException(e);
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
		Collection<Group> path = new HashSet<>();
		for (int i=0; i < directionNodes.size()-1; ++i) {
			Node here = directionNodes.get(i);
			Node there = directionNodes.get(i+1);
			if (here.getFloor() == this.directory.getFloorNum() && here.getFloor() == there.getFloor()) {
				Line line = new Line(here.getX(), here.getY(), there.getX(), there.getY());
				line.setStrokeWidth(PATH_WIDTH);
				line.setStroke(Color.MEDIUMVIOLETRED);
				if((i % 3) == 0) {
					path.add(new Arrow(line));
				} else {
					path.add(new Group(line));
				}
			}
		}
		this.linePane.getChildren().setAll(path);
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

	@Override
	protected void redisplayMapItems() {
		this.displayRooms();
	}

	private void displayRooms() {
		this.nodePane.getChildren().setAll(iconManager.getSavedIcons(directory.getRoomsOnFloor()));
	}


}
