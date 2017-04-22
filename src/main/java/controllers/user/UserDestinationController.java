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

// TODO: REMOVE THIS CLASS ENTIRELY

public class UserDestinationController
		extends UserMasterController
		implements Initializable
{
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		this.initialize(); // THIS IS THE ONLY LINE OF CODE THAT THIS CLASS SHOULD HAVE

//
//		// This has to be done after setting the kiosk
//		this.enableOrDisableNavigationButtons();
////		this.disableDirectionsBtn();
////		this.disableChangeStartBtn();
//
//		//Listener for search bar
//		this.searchBar.textProperty().addListener((ignored, ignoredOld, contents) -> this.filterRoomsByName(contents));

	}

	/**
	 * When selecting a destination, left clicking a room should select it
	 */
//	@Override
//	protected void clickRoomAction(Room room) {
//		this.selectEndRoom(room);
//	}
}
