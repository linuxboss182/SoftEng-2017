package controllers.user;

import controllers.SMSController;
import entities.FloorProxy;
import entities.Node;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import entities.Room;
import javafx.stage.Stage;
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

public class UserPathController
		extends UserMasterController
		implements Initializable
{

	private static final double PATH_WIDTH = 4.0;
	final double SCALE_DELTA = 1.1;
	private double clickedX, clickedY;
	@FXML private Button doneBtn;
	@FXML private AnchorPane floorsTraveledAnchorPane;
	private Text textDirections = new Text();
	private Rectangle bgRectangle = null;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		this.initialize();
		List<Node> ret;

		// Check if either start or destination is null (this should be impossible)
		// TODO: Get path _before_ openng UserPathController
		if (startRoom == null || endRoom == null) {
			try {
				this.doneBtnClicked();
			} catch (IOException e) {
				e.printStackTrace();
				System.err.println("Error loading user UserDestination.fxml");
			}
			return;
		}

		try {
			ret = Pathfinder.findPath(startRoom.getLocation(), endRoom.getLocation());
		} catch (PathNotFoundException e) {
			Alert alert = new Alert(Alert.AlertType.INFORMATION);
			alert.setTitle("No Path Found");
			alert.setHeaderText(null);
			alert.setContentText("There is no existing path to your destination. \n" +
					"Please check your start and end location and try again");
			alert.showAndWait();
			// TODO: Move the pathfinding (and the error handling) to UserMasterController
			return;
		}
		if (ret.isEmpty()) {
			// This is actually an impossible place to get to if the algorithms are correct
		}

		startRoom.getUserSideShape().setScaleX(1.5);
		startRoom.getUserSideShape().setScaleY(1.5);

		endRoom.getUserSideShape().setScaleX(1.5);
		endRoom.getUserSideShape().setScaleY(1.5);

		// change displayed floor to match the floor that the start node is on
		Node startNode = startRoom.getLocation();
		if (startNode == null) {
			// Handle this better (though it should be impossible)
			throw new RuntimeException("Start room was null; FIXME");
		}
		MiniFloor startFloor = new MiniFloor(startNode.getFloor(), startNode.getBuildingName());

		this.changeFloor(FloorProxy.getFloor(startNode.getBuildingName(), startNode.getFloor()));

		this.paintPath(this.getPathOnFloor(startFloor, ret));
		this.directionsTextField.getChildren().clear();

		textDirections.setText(DirectionsGenerator.fromPath(ret));
		//Call text directions
		this.directionsTextField.getChildren().add(textDirections);


		/* Draw the buttons for each floor on a multi-floor path. */

		List<MiniFloor> floors = new ArrayList<>();

		MiniFloor last = new MiniFloor(0, "");
		MiniFloor here = new MiniFloor(ret.get(0).getFloor(), ret.get(0).getBuildingName());
		MiniFloor next = new MiniFloor(ret.get(ret.size()-1).getFloor(), ret.get(ret.size()-1).getBuildingName());
		// add starting floor
		floors.add(here);
		this.createNewFloorButton(here, this.getPathOnFloor(here, ret), floors.size());
		//prints all the floors on the path in order
// 		System.out.println(ret.stream().map(Node::getFloorNum).collect(Collectors.toList()).toString());

		System.out.println(ret);
		System.out.println(ret.stream().map(n -> n.getBuildingName()).collect(Collectors.toList()));
		for (int i = 1; i < ret.size()-1; ++i) {
			System.out.println(here.building + " " + here.number);
			last = here;
			here = new MiniFloor(ret.get(i).getFloor(), ret.get(i).getBuildingName());
			next = new MiniFloor(ret.get(i+1).getFloor(), ret.get(i+1).getBuildingName());
//			System.out.println(last+" "+here+" "+next);
			// Check when there is a floor A -> floor B -> floor B transition and save floor B
			if ((last.number != here.number && next.number == here.number) || ! last.building.equalsIgnoreCase(here.building)) {
				floors.add(here);
				this.createNewFloorButton(here, this.getPathOnFloor(here, ret), floors.size());
			}
		}
		// Check that the last node's floor (which will always be 'next') is in the list
		last = floors.get(floors.size()-1);
		if (! last.isSameFloor(next)) {
			floors.add(next);
			this.createNewFloorButton(next, this.getPathOnFloor(next, ret), floors.size());
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
		public boolean isSameFloor(MiniFloor other) {
			return (other != null) && (this.number == other.number) &&
					this.building.equalsIgnoreCase(other.building);
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

	private ArrayList<Node> getPathOnFloor(MiniFloor floor, List<Node> allPath) {
		ArrayList<Node> path = new ArrayList<>();
		for(Node n : allPath) {
			if (n.getFloor() == floor.number && n.getBuildingName().equalsIgnoreCase(floor.building)) path.add(n);
		}
		return path;
	}

	@FXML
	public void doneBtnClicked() throws IOException {
		startRoom.getUserSideShape().setScaleX(1);
		startRoom.getUserSideShape().setScaleY(1);
		endRoom.getUserSideShape().setScaleX(1);
		endRoom.getUserSideShape().setScaleY(1);

		iconController.resetAllRooms();
		choosingStart = false;
		choosingEnd = true;
		startRoom = null;
		endRoom = null;
		Parent userPath = (BorderPane) FXMLLoader.load(this.getClass().getResource("/UserDestination.fxml"));
		this.getScene().setRoot(userPath);
	}

	@FXML
	public void sendSMSBtnClicked(){
//		Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
//		alert.setTitle("Information Dialog");
//		alert.setHeaderText("Feature Unavailable");
//		alert.setContentText("Sorry, SMS is currently unavailable.");
//		alert.showAndWait();

		 FXMLLoader loader = new FXMLLoader();
		 loader.setLocation(this.getClass().getResource("/sms.fxml"));
		 try {
		 	Scene smsScene = new Scene(loader.load());
		 	((SMSController)loader.getController()).setText(textDirections.getText());
		 	Stage smsStage = new Stage();
		 	smsStage.initOwner(floorsTraveledAnchorPane.getScene().getWindow());
		 	smsStage.setScene(smsScene);
		 	smsStage.showAndWait();
		 } catch (Exception e){
		 	System.out.println("Error making SMS popup");
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
		Collection<Line> path = new HashSet<>();
		for (int i=0; i < directionNodes.size()-1; ++i) {
			Node here = directionNodes.get(i);
			Node there = directionNodes.get(i + 1);
			if (here.getFloor() == this.directory.getFloorNum() && here.getFloor() == there.getFloor()) {
				Line line = new Line(here.getX(), here.getY(), there.getX(), there.getY());
				line.setStrokeWidth(PATH_WIDTH);
				line.setStroke(Color.MEDIUMVIOLETRED);
				path.add(line);
			}
		}
		this.linePane.getChildren().setAll(path);
	}

	/**
	 * When a path has been drawn, clicking a room doesn't do anythng
	 */
	// TODO: Make clicking a room after getting directions highlight the room in the directions, maybe?
	@Override
	protected void clickRoomAction(Room room) {
		return;
	}
}
