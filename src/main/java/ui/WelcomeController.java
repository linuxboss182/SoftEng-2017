package ui;

import main.ApplicationController;
import memento.UserState;
import entities.Directory;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;


public class WelcomeController implements Initializable
{
	@FXML private Pane welcome;

	Directory directory = ApplicationController.getDirectory();

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		ImageView imageView = new ImageView(new Image("/faulkner_welcome.png"));
		Pane bottomColor = new Pane();

		this.welcome.getChildren().add(bottomColor);
		this.welcome.getChildren().add(imageView);


		bottomColor.setBackground(new Background(new BackgroundFill(Color.DARKGRAY, new CornerRadii(0), new Insets(0))));
		double ratio = 2.4;

		imageView.setPreserveRatio(true);
		Platform.runLater( ()-> {
			this.welcome.getScene().setCursor(Cursor.HAND);
			imageView.setFitHeight(750);
			imageView.setFitWidth(750);
			imageView.setLayoutX(welcome.getWidth()/2 - imageView.getFitWidth()/2);
			imageView.setLayoutY(welcome.getHeight()/2 - imageView.getFitHeight()/2);
			bottomColor.setPrefWidth(this.welcome.getWidth());
			bottomColor.setPrefHeight(this.welcome.getHeight()/ratio);
			this.welcome.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, new CornerRadii(0), new Insets(0))));

			this.welcome.boundsInLocalProperty().addListener((observable, oldValue, newValue) -> {

				imageView.setLayoutX(welcome.getWidth()/2 - imageView.getFitWidth()/2);
				imageView.setLayoutY(welcome.getHeight()/2 - imageView.getFitHeight()/2);
				bottomColor.setPrefWidth(this.welcome.getWidth());
				bottomColor.setPrefHeight(this.welcome.getHeight()/ratio);
			});
		});
	}

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
		try {
			this.welcome.getScene().setCursor(Cursor.DEFAULT);
			Parent UserMaster = (BorderPane) FXMLLoader.load(this.getClass().getResource("/UserDestination.fxml"));
			this.welcome.getScene().setRoot(UserMaster);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


}
