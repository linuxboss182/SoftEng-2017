package controllers.user;

import controllers.shared.FloorProxy;
import entities.Node;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.ResourceBundle;

import entities.Room;
import main.DirectionsGenerator;
import main.Pathfinder;

public class UserPathController
		extends UserMasterController
		implements Initializable
{

	private static final double PATH_WIDTH = 2.0;
	final double SCALE_DELTA = 1.1;
	private double clickedX, clickedY;
	@FXML
	private Button doneBtn;
	@FXML
	private AnchorPane floorsTraveledAnchorPane;
	Text textDirections = new Text();
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		initialize();
		List<Node> ret;

		// Check if either start or destination is null
		// TODO: create exception class?
		// TODO: make pop-up for UI when this happens
		if (startRoom == null || endRoom == null) {
			try {
				this.doneBtnClicked();
			} catch (IOException e) {
				e.printStackTrace();
				System.err.println("Error loading user UserDestination.fxml");
			}
			return;
		}
		System.out.println("UserPathController.initialize");
		System.out.println("startRoom = " + startRoom);
		ret = Pathfinder.findPath(startRoom.getLocation(), endRoom.getLocation());
		// change displayed floor to match the floor that the start node is on
		int startFloor = startRoom.getLocation().getFloor();
		changeFloor(startFloor);
		paintPath(getPathOnFloor(startFloor, ret));
		this.directionsTextField.getChildren().clear();

		textDirections.setText(DirectionsGenerator.fromPath(ret));
		//Call text directions
		this.directionsTextField.getChildren().add(textDirections);


		/* Draw the buttons for each floor on a multi-floor path. */

		List<Integer> floors = new ArrayList<>();

		// Set initial values (set next to last in case there are only 1 or 2 steps)
		int last = 0;
		int here = ret.get(0).getFloor();
		int next = ret.get(ret.size()-1).getFloor();
		// add starting floor
		floors.add(here);
		this.createNewFloorButton(here, this.getPathOnFloor(here, ret), floors.size());
		//prints all the floors on the path in order
// 		System.out.println(ret.stream().map(Node::getFloor).collect(Collectors.toList()).toString());

		for (int i = 1; i < ret.size()-1; ++i) {
			last = ret.get(i-1).getFloor();
			here = ret.get(i  ).getFloor();
			next = ret.get(i+1).getFloor();
			System.out.println(last+" "+here+" "+next);
			// Check when there is a floor A -> floor B -> floor B transition and save floor B
			if (last != here && next == here) {
				floors.add(here);
				this.createNewFloorButton(here, this.getPathOnFloor(here, ret), floors.size());
			}
		}
		// Check that the last node's floor (which will always be 'next') is in the list
		if (floors.get(floors.size()-1) != next) {
			floors.add(next);
			this.createNewFloorButton(next, this.getPathOnFloor(next, ret), floors.size());
		}
	}

	private void createNewFloorButton(int floor, List<Node> path, int buttonCount) {
		ImageView newFloorButton = new ImageView();

		int buttonWidth = 80;
		int buttonHeight = 50;
		int buttonSpread = 100;
		int buttonY = 95;
		int centerX = 250;


		newFloorButton.setLayoutX(floorsTraveledAnchorPane.getLayoutX() + centerX + (buttonSpread)*buttonCount);
		newFloorButton.setLayoutY(buttonY);
		newFloorButton.setFitWidth(buttonWidth);
		newFloorButton.setFitHeight(buttonHeight);
		FloorProxy map = new FloorProxy(floor);

		newFloorButton.setImage(map.displayThumb());
		newFloorButton.setPickOnBounds(true);

		newFloorButton.setOnMouseClicked(e-> {
			// change to the new floor, and draw the path for that floor
			changeFloor(floor);
			paintPath(path);
			//Call text directions
			this.directionsTextField.getChildren().add(textDirections);
		});
		floorsTraveledAnchorPane.getChildren().add(newFloorButton);
	}

	private ArrayList<Node> getPathOnFloor(int floor, List<Node> allPath) {
		ArrayList<Node> path = new ArrayList<>();
		for(Node n : allPath) {
			if (n.getFloor() == floor) path.add(n);
		}
		return path;
	}

	@FXML
	public void doneBtnClicked() throws IOException {
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
		Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
		alert.setTitle("Information Dialog");
		alert.setHeaderText("Feature Unavailable");
		alert.setContentText("Sorry, SMS is currently unavailable.");
		alert.showAndWait();

		// FXMLLoader loader = new FXMLLoader();
		// loader.setLocation(this.getClass().getResource("/sms.fxml"));
		// try {
		// 	Scene smsScene = new Scene(loader.load());
		// 	((SMSController)loader.getController()).setText(textDirections.getText());
		// 	Stage smsStage = new Stage();
		// 	smsStage.initOwner(contentAnchor.getScene().getWindow());
		// 	smsStage.setScene(smsScene);
		// 	smsStage.showAndWait();
		// } catch (Exception e){
		// 	System.out.println("Error making SMS popup");
		// }
	}


	/**
	 * Draw a simple path between the nodes in the given list
	 *
	 * @param directionNodes A list of the nodes in the path, in order
	 */
	public void paintPath(List<Node> directionNodes) {
		this.directionsTextField.getChildren().clear();

		//add kiosk to start of list
		//directionNodes.add(0, this.kiosk);
		if(directionNodes.size() <= 0) {
			// TODO: Give an error message when no path is found
			return;
		}

		// This can be any collection type;
		Collection<Line> path = new HashSet<>();
		for (int i=0; i < directionNodes.size()-1; ++i) {
			Node here = directionNodes.get(i);
			Node there = directionNodes.get(i + 1);
			if (here.getFloor() == floor && here.getFloor() == there.getFloor()) {
				Line line = new Line(here.getX(), here.getY(), there.getX(), there.getY());
				line.setStrokeWidth(PATH_WIDTH);
				path.add(line);
			}
		}
		this.botPane.getChildren().setAll(path);
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
