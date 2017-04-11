package controllers;

import entities.Room;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.TextFlow;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by s7sal on 4/8/2017.
 */
public class UserDestinationController extends UserMasterController implements Initializable
{


	@Override
	public void initialize(URL location, ResourceBundle resources) {
		//Call initialize from super class
		initialize();
		changeStartBtn.setDisable(true);
		getDirectionsBtn.setDisable(true);

		//Listener for search bar
		searchBar.textProperty().addListener(new ChangeListener() {
			public void changed(ObservableValue observable, Object oldValue, Object newValue) {
				filterRoomList((String) oldValue, (String) newValue);
			}
		});

	}

	@FXML
	public void changeStartClicked() throws IOException, InvocationTargetException {
		Parent userStart = (BorderPane) FXMLLoader.load(this.getClass().getResource("/UserStart.fxml"));
		this.changeStartBtn.getScene().setRoot(userStart);
		this.choosingEnd = false;
		this.choosingStart = true;
	}




}
