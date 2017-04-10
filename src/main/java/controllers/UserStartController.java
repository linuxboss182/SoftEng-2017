package controllers;

import entities.Room;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.TextFlow;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ResourceBundle;


public class UserStartController extends UserMasterController implements Initializable
{


	@FXML
	private Button changeStartBtn;


	final double SCALE_DELTA = 1.1;
	private double clickedX, clickedY;


	@Override
	public void initialize(URL location, ResourceBundle resources) {
		initialize();

	}

	@FXML
	public void backBtnClicked() throws IOException, InvocationTargetException {
		Parent userDest = (BorderPane) FXMLLoader.load(this.getClass().getResource("/UserDestination.fxml"));
		this.sideGridPane.getScene().setRoot(userDest);
		this.choosingEnd = true;
		this.choosingStart = false;
	}



}
