package main;

//import controllers.SMSController;
import controllers.icons.IconController;
import javafx.application.Application;
import entities.Directory;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import main.database.DatabaseWrapper;
import main.database.DatabaseException;

public class ApplicationController extends Application
{

	private static Directory directory;
	private static IconController iconController;
	private static Stage stage;

	public static Stage getStage() {
		return ApplicationController.stage;
	}

	public static Directory getDirectory() {
		return ApplicationController.directory; // returns the single copy
	}

	public static void setDirectory(Directory newDirectory) {
		ApplicationController.directory = newDirectory;
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
//		TimeoutTimer.getTimeoutTimer().cancelTimer();
	}

	/** This is called by JavaFX and starts up the application UI user panel*/
	@Override
	public void start(Stage primaryStage) throws Exception {
		ApplicationController.stage = primaryStage;
		Parent root = (GridPane) FXMLLoader.load(this.getClass().getResource("/Welcome.fxml"));
		primaryStage.setTitle("Faulkner Hospital Navigator");
		primaryStage.getIcons().add(new Image("bwhIcon.png"));
		Scene user = new Scene(root, 1300, 800);
		primaryStage.setMinWidth(1180);
		primaryStage.setMinHeight(722);
		primaryStage.setScene(user);
		primaryStage.show();

	}
}

