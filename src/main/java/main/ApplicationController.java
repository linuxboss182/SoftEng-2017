package main;

//import controllers.SMSController;
import controllers.icons.IconController;
import javafx.application.Application;
import entities.Directory;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import main.database.DatabaseWrapper;
import main.database.DatabaseException;

import java.io.IOException;

public class ApplicationController extends Application
{

	private static Directory directory;
	private static IconController iconController;
	private static Scene scene;
	public static Scene loginScene;
	private static Stage stage;


	public static Scene getScene() {
		return ApplicationController.scene;
	}

	public static Stage getStage() {
		return ApplicationController.stage;
	}

	public static Scene getLoginScene() {
		return ApplicationController.loginScene;
	}

	public static void setScene(Scene scene) {
		ApplicationController.scene = scene;
	}

	public static Directory getDirectory() {
		return ApplicationController.directory; // returns the single copy
	}

	public static IconController getIconController() {
		return ApplicationController.iconController;
	}


	public static void main(String[] args) {
		try {
			DatabaseWrapper.getInstance().init();
		} catch (DatabaseException e) {
			System.out.println("ERROR IN DATABASE INITIALIZATION:\n" + e.getMessage());
			return;
		}

		ApplicationController.directory = DatabaseWrapper.getInstance().getDirectory();
		ApplicationController.iconController = new IconController(ApplicationController.directory);



		Application.launch(args);

		DatabaseWrapper.getInstance().close();


	}


	/** This is called by JavaFX and starts up the application UI user panel*/
	@Override
	public void start(Stage primaryStage) throws Exception {

		ApplicationController.stage = primaryStage;
		Parent root = (BorderPane) FXMLLoader.load(this.getClass().getResource("/UserDestination.fxml"));
		primaryStage.setTitle("Faulkner Hospital Navigator");
		Scene user = new Scene(root, 1174, 722);
		primaryStage.setScene(user);
		primaryStage.show();

	}
}

