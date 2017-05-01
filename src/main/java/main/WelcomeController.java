package main;

import controllers.user.UserState;
import entities.Directory;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;

import java.util.TimerTask;


public class WelcomeController
{
	@FXML private GridPane welcome;

	Directory directory = ApplicationController.getDirectory();

	protected Scene getScene() {
		// The parentBorderPane should always exist, so use it to get the scene
		return this.welcome.getScene();
	}

	public UserState getState() {
		return new UserState(this.getScene().getRoot(), this.getScene());
	}
	@FXML
	public void onClick(){
		this.directory.getCaretaker().addState(this.getState());
		TimeoutTimer.getTimeoutTimer().registerTask(() -> setState(directory.getCaretaker().getState()));
		try {
			Parent UserMaster = (BorderPane) FXMLLoader.load(this.getClass().getResource("/UserDestination.fxml"));
			this.welcome.getScene().setRoot(UserMaster);
		} catch (Exception e){e.printStackTrace();}
	}

	public void setState(UserState state) {
		welcome.getScene().setRoot(state.getRoot());
		this.directory.logOut();
	}
}
