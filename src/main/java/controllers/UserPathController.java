package controllers;

import entities.Node;
import entities.Room;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import controllers.UserMasterController;
import main.DirectionsGenerator;
import main.Pathfinder;

public class UserPathController extends UserMasterController implements Initializable
{


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

		try{
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

			/** The following code/ comments are for drawing the path and or buttons for getting directions between floors.
			 *
			 * OK...
			 * So basically we just scroll through the path and find the floors that the path travels between, count them, and display them in the order that they are traveled in.
			 * Count floors
			 * Display buttons in order of path
			 * Change to the floor of the starting room
			 * draw the path on that floor
			 */
			ArrayList<Integer> floors = new ArrayList<>();
			for(int i = 0; i < ret.size(); i++) {
				// add buttons for the floors traveled on

				int floor = ret.get(i).getFloor();
				if(!floors.contains(floor)) {
					System.out.println("Adding a floor button");
					floors.add(floor);
					createNewFloorButton(floor, getPathOnFloor(floor, ret), floors.size());
				}
			}
		} catch (NullPointerException n){
			// TODO: create exception class?
			// TODO: make pop-up for UI when this happens
			System.out.println("start or dest node is null, need to re-choose start and dest.");
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
			if(n.getFloor() == floor) path.add(n);
		}
		return path;
	}

	@FXML
	public void doneBtnClicked() throws IOException {
		deselectStartRoom();
		deselectEndRoom();
		choosingStart = false;
		choosingEnd = true;
		Parent userPath = (BorderPane) FXMLLoader.load(this.getClass().getResource("/UserDestination.fxml"));
		this.imageViewMap.getScene().setRoot(userPath);
	}

}
