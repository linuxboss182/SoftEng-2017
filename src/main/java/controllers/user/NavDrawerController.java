package controllers.user;

import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTextField;
import entities.Room;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

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

	}

	protected void clickRoomAction(Room room) {

	}

	/**
	 * Populates the list of rooms
	 */
	public void populateListView() {
		this.resultsListView.setItems(this.listProperty);
		this.listProperty.set(FXCollections.observableArrayList(directory.filterRooms(r -> r.getLocation() != null)));

//		this.resultsListView.getSelectionModel().selectedItemProperty().addListener(
//				(ignored, oldValue, newValue) -> this.selectRoomAction(resultsListView.getSelectionModel().getSelectedItem()));
	}

	/**
	 * Filter the room list for the search bar
	 *
	 * @param searchString The new string in the search bar
	 */
	public void filterRoomsByName(String searchString) {
		if((this.searchBar == null) || (searchString == null) || (searchString.length() == 0)) {
			this.populateListView();
		} else {
			// The Collator allows case-insensitie comparison
			Collator coll = Collator.getInstance();
			coll.setStrength(Collator.PRIMARY);
			// coll.setDecomposition(Collator.FULL_DECOMPOSITION); <- done by Normalizer

			// Normalize accents, remove leading spaces, remove duplicate spaces elsewhere
			String normed = Normalizer.normalize(searchString, Normalizer.Form.NFD).toLowerCase()
					.replaceAll("^\\s*", "").replaceAll("\\s+", " ");

			Set<Room> roomSet = directory.filterRooms(room ->
					(room.getLocation() != null) && // false if room has no location
							Normalizer.normalize(room.getName(), Normalizer.Form.NFD).toLowerCase()
									.contains(normed)); // check with unicode normalization

			this.resultsListView.setItems(FXCollections.observableArrayList(roomSet));
		}
	}
}
