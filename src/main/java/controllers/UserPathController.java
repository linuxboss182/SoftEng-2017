package controllers;

import entities.Node;
import entities.Room;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import controllers.UserMasterController;
import main.DirectionsGenerator;
import main.Pathfinder;

/**
 * Created by s7sal on 4/8/2017.
 */
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
		ret = Pathfinder.findPath(startRoom.getLocation(), endRoom.getLocation());
		changeFloor(floor);

		paintPath(new ArrayList<>(ret));
	}

	

}
