package main;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;


public class WelcomeController
{
	@FXML
	private GridPane welcome;

	@FXML
	public void onClick(){
		try {
			Parent UserMaster = (BorderPane) FXMLLoader.load(this.getClass().getResource("/UserDestination.fxml"));
			this.welcome.getScene().setRoot(UserMaster);
		} catch (Exception e){}
	}
}
