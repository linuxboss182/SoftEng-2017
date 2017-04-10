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

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		initialize();
		List<Node> ret;
		try{
			ret = Pathfinder.findPath(startRoom.getLocation(), endRoom.getLocation());
			// change displyaed floor to match the floor that the start node is on
			int startFloor = startRoom.getLocation().getFloor();
			changeFloor(startFloor);

			paintPath(new ArrayList<>(ret));
		} catch (NullPointerException n){
			System.out.println("start or dest node is null, need to re-choose start and dest.");
		}
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
