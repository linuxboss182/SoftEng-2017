package userpanel;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.awt.*;

public class Controller
{
	@FXML
	private Button logAsAdmin;
	@FXML
	private ImageView imageViewMap;

	@FXML
	private void logAsAdminClicked() throws Exception{
//		Parent root = (AnchorPane)FXMLLoader.load(getClass().getResource("/AdminUI.fxml"));
//		adminScene.setTitle("Faulkner Hospital Navigator");
//		primaryStage.setScene(new Scene(root, 1174, 722));
//		primaryStage.setScene(adminStage);
//		adminScene.show();

		Parent root;
		root = FXMLLoader.load(getClass().getResource("/AdminUI.fxml"));
		Stage stage = new Stage();
		stage.setTitle("My New Stage Title");
		stage.setScene(new Scene(root, 1174, 722));
		stage.show();
	}

	@FXML
	private void mapClicked() {
		//System.out.print("Map Clicked");
		imageViewMap.setPickOnBounds(true);

		imageViewMap.setOnMouseClicked(e -> {
			System.out.println("["+e.getX()+", "+e.getY()+"]");
			//Paint something at that location

		});
	}
}
