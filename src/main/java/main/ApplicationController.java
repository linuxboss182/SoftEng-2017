package main;

import controllers.SMSController;
import controllers.icons.IconController;
import javafx.application.Application;
import entities.Directory;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class ApplicationController extends Application
{

	public static DatabaseController dbc;
	private static Directory directory;
	private static IconController iconController;

	public static Directory getDirectory() {
		return ApplicationController.directory; // returns the single copy
	}

	public static IconController getIconController() {
		return ApplicationController.iconController;
	}


	public static void main(String[] args) {
		SMSController sms = new SMSController();

		ApplicationController.dbc = new DatabaseController();

		try {
			ApplicationController.dbc.init();
		} catch (DatabaseException e) {
			System.out.println("ERROR IN DATABASE INITIALIZATION:\n" + e.getMessage());
			return;
		}

		ApplicationController.directory = ApplicationController.dbc.getDirectory();
		ApplicationController.iconController = new IconController(ApplicationController.directory);

		Application.launch(args);

		ApplicationController.dbc.close();
	}

	/** This is called by JavaFX and starts up the application UI user panel*/
	@Override
	public void start(Stage primaryStage) throws Exception {
		Parent root = (BorderPane) FXMLLoader.load(this.getClass().getResource("/UserDestination.fxml"));
		primaryStage.setTitle("Faulkner Hospital Navigator");
		Scene user = new Scene(root, 1174, 722);
		primaryStage.setScene(user);
		primaryStage.show();

	}
}

