package adminpanel;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Window
		extends Application
{
	@Override
	public void start(Stage primaryStage) throws Exception{
		Parent root = FXMLLoader.load(this.getClass().getResource("/AdminUI.fxml"));
		primaryStage.setScene(new Scene(root, 1174, 722));
		primaryStage.show();
	}

}
