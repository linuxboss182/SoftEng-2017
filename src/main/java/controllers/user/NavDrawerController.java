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
import javafx.scene.image.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import main.ApplicationController;

import java.awt.*;
import java.awt.Image;
import java.io.IOException;
import java.net.URL;
import java.text.Collator;
import java.text.Normalizer;
import java.util.ResourceBundle;
import java.util.Set;


public class NavDrawerController
		extends UserMasterController
		implements Initializable
{
	@FXML private ImageView startImageView;

	@FXML private JFXListView<Room> resultsListView;

	@FXML private ImageView destImageView;
	@FXML private ImageView logAsAdmin;
	@FXML private ImageView aboutBtn;
	@FXML protected VBox drawerVBox;
	@FXML
	private HBox destHBox;
	@FXML
	private HBox startHBox;
	@FXML
	private HBox bottomHBox;





	@Override
	public void initialize(URL location, ResourceBundle resources) {
//		javafx.scene.image.Image startIcon;
//		startIcon = new javafx.scene.image.Image("/startIcon.png", true);
//		startImageView.setImage(startIcon);
		this.directory = ApplicationController.getDirectory();
		//startField.setText("Your Location");
		populateListView();
		resultsListView.setVisible(false);

		//set lock icon
		javafx.scene.image.Image login = new javafx.scene.image.Image("/lock.png");
		logAsAdmin.setImage(login);

		//set start icon
		javafx.scene.image.Image start = new javafx.scene.image.Image("/aPin.png");
		startImageView.setImage(start);

		//set dest icon
		javafx.scene.image.Image dest = new javafx.scene.image.Image("/bPin.png");
		destImageView.setImage(dest);

		//set about page icon
		javafx.scene.image.Image about = new javafx.scene.image.Image("/about.png");
		aboutBtn.setImage(about);

		startFieldListener();
		destFieldListener();
		listViewListener();
		startFieldFocusedListener();
		destinationFieldFocusedListener();
		onLoginImageClicked();
		onAboutButtonClicked();

		resultsListView.setPrefHeight(drawerVBox.getPrefHeight() - startHBox.getPrefHeight() - destHBox.getPrefHeight() - bottomHBox.getPrefHeight());
		System.out.println(drawerVBox.getPrefHeight() - startHBox.getPrefHeight() - destHBox.getPrefHeight() - bottomHBox.getPrefHeight());



	}

	protected void clickRoomAction(Room room) {

	}

	public void startFieldFocusedListener() {
		startField.focusedProperty().addListener(new ChangeListener<Boolean>()
		{
			@Override
			public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldPropertyValue, Boolean newPropertyValue) {
				if (newPropertyValue) {
					isStart = true;
				}
				else {
					isStart = false;
				}
			}
		});
	}

	public void destinationFieldFocusedListener() {
		destinationField.focusedProperty().addListener(new ChangeListener<Boolean>()
		{
			@Override
			public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldPropertyValue, Boolean newPropertyValue) {
				if (newPropertyValue) {
					isDest = true;
				}
				else {
					isDest = false;
				}
			}
		});
	}

	public void startFieldListener() {

		// Enable search; if this becomes more than one line, make it a function
		this.startField.textProperty().addListener((ignored, ignoredOld, contents) -> {
			isStart = true;
			this.filterRoomsByName(contents);
			resultsListView.setVisible(true);
		});
	}

	public void destFieldListener() {
		// Enable search; if this becomes more than one line, make it a function
		this.destinationField.textProperty().addListener((ignored, ignoredOld, contents) -> {
			isDest = true;
			this.filterRoomsByName(contents);
			resultsListView.setVisible(true);
		});
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
				this.selectStartRoom(resultsListView.getSelectionModel().getSelectedItem());
				System.out.println("start clicked");

				//isDest = false;
			}

			if (isDest) {
				this.selectEndRoom(resultsListView.getSelectionModel().getSelectedItem());
				System.out.println("end clicked");

				//isStart = false;
			}

		});
	}

	/**
	 * Filter the room list for the search bar
	 *
	 * @param contentString The new string in the search bar
	 */
	public void filterRoomsByName(String contentString) {
		if((this.startField == null) || (contentString == null) || (contentString.length() == 0) ||
				(this.destinationField == null)) {
			this.populateListView();
			resultsListView.setVisible(false);
		} else {
			// The Collator allows case-insensitie comparison
			Collator coll = Collator.getInstance();
			coll.setStrength(Collator.PRIMARY);
			// coll.setDecomposition(Collator.FULL_DECOMPOSITION); <- done by Normalizer

			// Normalize accents, remove leading spaces, remove duplicate spaces elsewhere
			String normeStart = Normalizer.normalize(contentString, Normalizer.Form.NFD).toLowerCase()
					.replaceAll("^\\s*", "").replaceAll("\\s+", " ");


			Set<Room> roomSet = directory.filterRooms(room ->
					(room.getLocation() != null) && // false if room has no location
							(Normalizer.normalize(room.getName(), Normalizer.Form.NFD).toLowerCase()
									.contains(normeStart))); // check with unicode normalization

			this.resultsListView.setItems(FXCollections.observableArrayList(roomSet));
		}
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
