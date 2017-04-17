package controllers.user;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ResourceBundle;

import entities.Room;


public class UserDestinationController
		extends UserMasterController
		implements Initializable
{
	protected Room kiosk = null;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		//Call initialize from super class
		this.initialize();

		// TODO: Save the kiosk in a more sane manner, and load it faster
		if (this.kiosk == null) {;
			for (Room r : directory.getRooms()) {
				if (r.getName().equalsIgnoreCase("YOU ARE HERE")) {
					this.kiosk = r;
				}
			}
		}
		if (startRoom == null) startRoom = this.kiosk;

		// This has to be done after setting the kiosk
		this.enableOrDisableNavigationButtons();
//		this.disableDirectionsBtn();
//		this.disableChangeStartBtn();

		//Listener for search bar
		this.searchBar.textProperty().addListener((ignored, ignoredOld, contents) -> this.filterRoomsByName(contents));

	}

	@FXML
	public void changeStartClicked() throws IOException, InvocationTargetException {
		Parent userStart = (BorderPane) FXMLLoader.load(this.getClass().getResource("/UserStart.fxml"));
		this.getScene().setRoot(userStart);
		choosingEnd = false;
		choosingStart = true;
	}

	/**
	 * When selecting a destination, left clicking a room should select it
	 */
	@Override
	protected void clickRoomAction(Room room) {
		this.selectEndRoom(room);
	}
}
