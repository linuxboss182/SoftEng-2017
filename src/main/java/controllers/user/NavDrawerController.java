package controllers.user;

import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTextField;
import entities.Room;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.*;
import javafx.scene.layout.VBox;
import main.ApplicationController;

import java.awt.*;
import java.awt.Image;
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

	@FXML private JFXHamburger settingsHamburgerBtn;

	@FXML private JFXListView<Room> resultsListView;

	@FXML private ImageView destImageView;

	@FXML private JFXTextField startField;

	@FXML private VBox drawerVBox;

	@FXML private JFXTextField destinationField;



	@Override
	public void initialize(URL location, ResourceBundle resources) {
//		javafx.scene.image.Image startIcon;
//		startIcon = new javafx.scene.image.Image("/startIcon.png", true);
//		startImageView.setImage(startIcon);
		this.directory = ApplicationController.getDirectory();
		startField.setText("Your Location");
		populateListView();
	}

//	protected void clickRoomAction(Room room) {
//
//	}

	/**
	 * Populates the list of rooms
	 */
	public void populateListView() {

		this.resultsListView.setItems(this.listProperty);

		this.listProperty.set(FXCollections.observableArrayList(directory.filterRooms(r -> r.getLocation() != null)));
		System.out.println("begin");

		this.resultsListView.getSelectionModel().selectedItemProperty().addListener(
				(ignored, oldValue, newValue) -> this.selectRoomAction(resultsListView.getSelectionModel().getSelectedItem()));
	}

	/**
	 * Filter the room list for the search bar
	 *
	 * @param startString The new string in the start bar
	 * @param destString The new string in the dest bar
	 */
	public void filterRoomsByName(String startString, String destString) {
		if((this.startField == null) || (startString == null) || (startString.length() == 0) ||
				(this.destinationField == null) || (destString == null) || (destString.length() == 0)) {
			this.populateListView();
		} else {
			// The Collator allows case-insensitie comparison
			Collator coll = Collator.getInstance();
			coll.setStrength(Collator.PRIMARY);
			// coll.setDecomposition(Collator.FULL_DECOMPOSITION); <- done by Normalizer

			// Normalize accents, remove leading spaces, remove duplicate spaces elsewhere
			String normeStart = Normalizer.normalize(startString, Normalizer.Form.NFD).toLowerCase()
					.replaceAll("^\\s*", "").replaceAll("\\s+", " ");

			// Normalize accents, remove leading spaces, remove duplicate spaces elsewhere
			String normeDest = Normalizer.normalize(destString, Normalizer.Form.NFD).toLowerCase()
					.replaceAll("^\\s*", "").replaceAll("\\s+", " ");

			Set<Room> roomSet = directory.filterRooms(room ->
					(room.getLocation() != null) && // false if room has no location
							(Normalizer.normalize(room.getName(), Normalizer.Form.NFD).toLowerCase()
									.contains(normeStart)) &&
							(Normalizer.normalize(room.getName(), Normalizer.Form.NFD).toLowerCase()
									.contains(normeDest))); // check with unicode normalization

			this.resultsListView.setItems(FXCollections.observableArrayList(roomSet));
		}
	}
}
