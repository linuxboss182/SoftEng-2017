package controllers.user;

import com.jfoenix.controls.JFXButton;
import controllers.extras.SMSController;
import controllers.shared.MapDisplayController;
import entities.FloorProxy;
import entities.Node;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

import entities.Room;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import main.ApplicationController;
import main.DirectionsGenerator;
import main.algorithms.PathNotFoundException;
import main.algorithms.Pathfinder;

// TODO: Put directions in a scroll box
// TODO: Generally improve text directions (see below)
/*
Remove recurring directions

If possible, use things like "follow the sidewalk" (probably not possible)

If possible, get turn direction upon exiting a building (requires changes elsewhere)
 */

// TODO: UserPathController should not inherit from UserMasterController

public class UserPathController
		extends MapDisplayController
		implements Initializable
{
	@FXML private JFXButton logAsAdmin;
	@FXML protected Pane linePane;
	@FXML private Pane nodePane;
	@FXML protected TextFlow directionsTextField;
	@FXML private BorderPane parentBorderPane;
	@FXML private SplitPane mapSplitPane;
	@FXML private ImageView logoImageView;

	@FXML private Button doneBtn;
	@FXML private AnchorPane floorsTraveledAnchorPane;

	private static final double PATH_WIDTH = 4.0;
	private double clickedX;
	private double clickedY;
	private Text textDirections = new Text();
	private Rectangle bgRectangle = null;
	private LinkedList<LinkedList<Node>> pathSegments = new LinkedList<>();


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

		mapScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
		mapScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

		this.directory = ApplicationController.getDirectory();
		iconController = ApplicationController.getIconController();

		this.changeFloor(this.directory.getFloor());
		this.imageViewMap.setPickOnBounds(true);

		// TODO: Move zoom stuff to MapDisplayController
		// TODO: Set zoom based on window size
		zoomSlider.setValue(0);
		setZoomSliding();

		this.displayRooms();
		iconController.resetAllRooms();

		setScrollZoom();

		contentAnchor.setOnMousePressed(event -> {
			clickedX = event.getX();
			clickedY = event.getY();
		});

		contentAnchor.setOnMouseDragged(event -> {
			if (contentAnchor.getTranslateX() + event.getX() - clickedX >= 0 && contentAnchor.getTranslateX() + event.getX() - clickedX + this.imageViewMap.getFitWidth() <= mapSplitPane.getWidth()) {
				contentAnchor.setTranslateX(contentAnchor.getTranslateX() + event.getX() - clickedX);
			}
			if(contentAnchor.getTranslateY() + event.getY() - clickedY >= 0 && contentAnchor.getTranslateY() + event.getY() - clickedY + this.imageViewMap.getFitHeight() <= mapSplitPane.getHeight()) {
				contentAnchor.setTranslateY(contentAnchor.getTranslateY() + event.getY() - clickedY);
			}
			event.consume();
		});


		// Redraw rooms when the background is released
		// TODO: Fix bug where clicking rooms un-draws them
		contentAnchor.setOnMouseReleased(event -> {
			iconController.resetAllRooms();
			this.displayRooms();
		});

		setHotkeys();
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

		Node startNode = startRoom.getLocation();
		MiniFloor startFloor = new MiniFloor(startNode.getFloor(), startNode.getBuildingName());
		this.changeFloor(FloorProxy.getFloor(startNode.getBuildingName(), startNode.getFloor()));

		List<Node> path= this.getPathOrAlert(startRoom, endRoom);
		if (path == null) {
			return false;
		}
		this.directionsTextField.getChildren().clear();
		textDirections.setText(DirectionsGenerator.fromPath(path));
		//Call text directions
		this.directionsTextField.getChildren().add(textDirections);

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
		paintPath(pathSegments.get(0));
		this.directionsTextField.getChildren().add(textDirections);
		// pathSegment now has all segments
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
				LinkedList<Node> seg = pathSegments.get(i);
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
			LinkedList<Node> seg = pathSegments.getLast();
			this.createNewFloorButton(here, seg, floors.size());
		}
	}

	private void createNewFloorButton(MiniFloor floor, List<Node> path, int buttonCount) {
		ImageView newFloorButton = new ImageView();

		int buttonWidth = 110;
		int buttonHeight = 70;
		int buttonSpread = 140;
		int buttonY = (int)floorsTraveledAnchorPane.getHeight()/2 + 15;
		int centerX = 0;


		newFloorButton.setLayoutX(floorsTraveledAnchorPane.getLayoutX() + centerX + (buttonSpread)*buttonCount);
		newFloorButton.setLayoutY(buttonY);
		newFloorButton.setFitWidth(buttonWidth);
		newFloorButton.setFitHeight(buttonHeight);
		FloorProxy map = FloorProxy.getFloor(floor.building, floor.number);

		newFloorButton.setImage(map.displayThumb());
		newFloorButton.setPickOnBounds(true);

		Rectangle backgroundRectangle = new Rectangle();
		backgroundRectangle.setWidth(buttonWidth*1.25);
		backgroundRectangle.setHeight(buttonHeight*1.25);
		backgroundRectangle.setX(floorsTraveledAnchorPane.getLayoutX() + centerX + (buttonSpread)*buttonCount-10);
		backgroundRectangle.setY(buttonY - 10);
		backgroundRectangle.setFill(Color.WHITE);
		backgroundRectangle.setStroke(Color.BLACK);
		backgroundRectangle.setStrokeWidth(5);

		newFloorButton.setOnMouseClicked(e-> {
			// change to the new floor, and draw the path for that floor
			this.changeFloor(FloorProxy.getFloor(floor.building, floor.number));
			this.paintPath(path);
			//Call text directions
			this.directionsTextField.getChildren().add(textDirections);
			if(this.bgRectangle != null) this.bgRectangle.setVisible(false);
			backgroundRectangle.setVisible(true);
			this.bgRectangle = backgroundRectangle;
		});
		backgroundRectangle.setVisible(false);
		floorsTraveledAnchorPane.getChildren().add(backgroundRectangle);
		floorsTraveledAnchorPane.getChildren().add(newFloorButton);
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
		this.floorsTraveledAnchorPane.getScene().setRoot(userPath);
	}

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
		this.directionsTextField.getChildren().clear();
		// This can be any collection type;
		Collection<Arrow> path = new HashSet<>();
		for (int i=0; i < directionNodes.size()-1; ++i) {
			Node here = directionNodes.get(i);
			Node there = directionNodes.get(i+1);
			if (here.getFloor() == this.directory.getFloorNum() && here.getFloor() == there.getFloor()) {
				Line line = new Line(here.getX(), here.getY(), there.getX(), there.getY());
				line.setStrokeWidth(PATH_WIDTH);
				line.setStroke(Color.MEDIUMVIOLETRED);
				path.add(new Arrow(line));
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
		Set<javafx.scene.Node> roomShapes = new HashSet<>();
		for (Room room : directory.getRoomsOnFloor(directory.getFloor())) {
			roomShapes.add(room.getUserSideShape());
		}
		this.nodePane.getChildren().setAll(roomShapes);
	}

	@FXML
	public void logAsAdminClicked()
			throws IOException, InvocationTargetException {
		// Unset navigation targets for after logout
		Parent loginPrompt = (BorderPane) FXMLLoader.load(this.getClass().getResource("/LoginPrompt.fxml"));
		floorsTraveledAnchorPane.getScene().setRoot(loginPrompt);
	}
}
