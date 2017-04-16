package controllers.user;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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


public class UserDestinationController extends UserMasterController implements Initializable
{
	protected Room kiosk = null;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		//Call initialize from super class
		initialize();
		changeStartBtn.setDisable(true);
		getDirectionsBtn.setDisable(true);

		// TODO: Save the kiosk in a more sane manner, and load it faster
		if (kiosk == null) {;
			for (Room r : directory.getRooms()) {
				if (r.getName().equalsIgnoreCase("YOU ARE HERE")) {
					kiosk = r;
				}
			}
		}
		if (startRoom == null) startRoom = kiosk;

		//Listener for search bar
		searchBar.textProperty().addListener(new ChangeListener() {
			public void changed(ObservableValue observable, Object oldValue, Object newValue) {
				filterRoomList((String) oldValue, (String) newValue);
			}
		});

	}

	@FXML
	public void changeStartClicked() throws IOException, InvocationTargetException {
		Parent userStart = (BorderPane) FXMLLoader.load(this.getClass().getResource("/UserStart.fxml"));
		this.changeStartBtn.getScene().setRoot(userStart);
		choosingEnd = false;
		choosingStart = true;
	}

}
