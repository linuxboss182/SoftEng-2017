package controllers.user;

import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTextField;
import entities.FloorProxy;
import entities.Room;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import main.ApplicationController;


import java.awt.Image;
import java.io.IOException;
import java.net.URL;
import java.text.Collator;
import java.text.Normalizer;
import java.util.ResourceBundle;
import java.util.Set;


public class PathDrawerController
		extends UserMasterController
		implements Initializable
{
	@FXML private ImageView startImageView;

	@FXML private JFXListView<Room> resultsListView;

	@FXML private ImageView destImageView;
	@FXML private ImageView logAsAdmin;
	@FXML private ImageView aboutBtn;
	@FXML private ImageView backImageView;
	@FXML protected VBox drawerVBox;
	@FXML private HBox destHBox;
	@FXML private HBox startHBox;
	@FXML private HBox bottomHBox;
	@FXML protected Label startLbl;
	@FXML protected Label destLbl;
	private Room selectedListRoom;
	@FXML public Pane linePane;



	@Override
	public void initialize(URL location, ResourceBundle resources) {
//		javafx.scene.image.Image startIcon;
//		startIcon = new javafx.scene.image.Image("/startIcon.png", true);
//		startImageView.setImage(startIcon);
		System.out.println("this.linePane = " + this.linePane);
		this.directory = ApplicationController.getDirectory();
		//startField.setText("Your Location");
		populateListView();
		resultsListView.setVisible(false);

		//set lock icon
		javafx.scene.image.Image login = new javafx.scene.image.Image("/lock.png");
		logAsAdmin.setImage(login);

		//set back icon
		javafx.scene.image.Image back = new javafx.scene.image.Image("/back.png");
		backImageView.setImage(back);

		//set start icon
		javafx.scene.image.Image start = new javafx.scene.image.Image("/aPin.png");
		startImageView.setImage(start);

		//set dest icon
		javafx.scene.image.Image dest = new javafx.scene.image.Image("/bPin.png");
		destImageView.setImage(dest);

		//set about page icon
		javafx.scene.image.Image about = new javafx.scene.image.Image("/about.png");
		aboutBtn.setImage(about);

		//Call listeners
		listViewListener();
		onLoginImageClicked();
		onAboutButtonClicked();
		onBackImageClicked();

		//Attempt to resize the listview
		resultsListView.setPrefHeight(drawerVBox.getPrefHeight() - startHBox.getPrefHeight() - destHBox.getPrefHeight() - bottomHBox.getPrefHeight());
		System.out.println(drawerVBox.getPrefHeight() - startHBox.getPrefHeight() - destHBox.getPrefHeight() - bottomHBox.getPrefHeight());

		//Set the start and end labels
		startLbl.setText(startRoom.getName());
		destLbl.setText(endRoom.getName());


		if(startRoom != null && endRoom != null) {
			drawDirections(startRoom, endRoom);
		}

	}

	protected void clickRoomAction(Room room) {

	}




	/**
	 * Populates the list of rooms
	 */
	public void populateListView() {

		this.resultsListView.setItems(this.listProperty);

		this.listProperty.set(FXCollections.observableArrayList(directory.filterRooms(r -> r.getLocation() != null)));

	}

	protected void listViewListener() {
		this.resultsListView.getSelectionModel().selectedItemProperty().addListener((ignored, oldValue, newValue) -> {
			if (isStart) {
				selectedListRoom = resultsListView.getSelectionModel().getSelectedItem();
				this.selectStartRoom(selectedListRoom);
				System.out.println("start clicked");
				startField.setText(newValue.getName());
				//resultsListView.getSelectionModel().clearSelection();

				//isDest = false;
			}

			if (isDest) {
				this.selectEndRoom(resultsListView.getSelectionModel().getSelectedItem());
				System.out.println("end clicked");
				startField.setText(newValue.getName());
				//resultsListView.getSelectionModel().clearSelection();

				//isStart = false;
			}

		});
	}


	public void onLoginImageClicked() {
		logAsAdmin.setOnMouseClicked(e-> {
			Scene loginScene = null;
			try {
				FXMLLoader loader = new FXMLLoader();
				loader.setLocation(this.getClass().getResource("/LoginPrompt.fxml"));
				loginScene = new Scene(loader.load());
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			ApplicationController.getStage().setScene(loginScene);



		});
	}

	public void onBackImageClicked() {
		backImageView.setOnMouseClicked(e-> {
			FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/NavDrawer.fxml"));
			PathDrawerController controller = loader.getController();

			drawerVBox.getChildren().clear();
			try {
				drawerVBox.getChildren().add(FXMLLoader.load(getClass().getResource("/NavDrawer.fxml")));
			} catch (IOException e1) {
				e1.printStackTrace();
			}


		});
	}

	public void onAboutButtonClicked() {
		aboutBtn.setOnMouseClicked(e-> {
			Scene aboutScene = null;
			try {
				FXMLLoader loader = new FXMLLoader();
				loader.setLocation(this.getClass().getResource("/aboutPage.fxml"));
				aboutScene = new Scene(loader.load());

				/////

			} catch (IOException e1) {
				e1.printStackTrace();
			}
			ApplicationController.getStage().setScene(aboutScene);



		});
	}

//		@FXML
//	public void logAsAdminClicked()
//			throws IOException, InvocationTargetException {
//		// Unset navigation targets for after logout
//		Parent loginPrompt = (BorderPane) FXMLLoader.load(this.getClass().getResource("/LoginPrompt.fxml"));
//		this.getScene().setRoot(loginPrompt);
//	}

//	public void aboutBtnClicked() throws IOException {
//		UserAboutPage aboutPageController = new UserAboutPage();
//		FXMLLoader loader = new FXMLLoader();
//		loader.setLocation(this.getClass().getResource("/aboutPage.fxml"));
//		Scene addAboutScene = new Scene(loader.load());
//		Stage addAboutStage = new Stage();
//		addAboutStage.initOwner(contentAnchor.getScene().getWindow());
//		addAboutStage.setScene(addAboutScene);
//		addAboutStage.showAndWait();
//	}
}
