package userpanel;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.scene.image.Image;

import java.awt.*;

public class Controller
{
	@FXML
	private Button logAsAdmin;
	@FXML
	private ImageView imageViewMap;
	@FXML
	public VBox contentVBox;
	@FXML
	public HBox contentHBox;

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
			paintOnLocation();


		});
	}
	public VBox getContentVBox() {
		return contentVBox;
	}

	//private String imageFile;
	public void paintOnLocation() {
		Image map4 = new Image("file:resources/4_thefourthfloor.png");
		ImageView imageViewMap = new ImageView();
		imageViewMap.setImage(map4);
		Circle circ = new Circle(50);
		circ.setTranslateX(120);
		circ.setTranslateY(10);
		circ.setCenterX(50);
		circ.setCenterY(50);
		circ.setFill(new ImagePattern(map4, 0.2, 0.2, 0.4, 0.4, true));

		contentHBox.getChildren().add(circ);

	}


}
