package adminpanel;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.net.URL;
import java.util.ResourceBundle;

public class EditorController implements Initializable
{
	@FXML
	private Button addRoomBtn;
	@FXML
	private Button logoutBtn;
	@FXML
	private TextField nameField;
	@FXML
	private TextField descriptField;
	@FXML
	private TextField XcoordField;
	@FXML
	private TextField YcoordField;
	@FXML
	private ImageView imageViewMap;

	Image map4;
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		//Add map
		map4 = new Image("/4_thefourthfloor.png");
		imageViewMap.setImage(map4);
	}

	@FXML
	private void addRoomBtnClicked() {
	}

	@FXML
	private void mapClicked() {
	}

	@FXML
	private void logoutBtnClicked() {
	}
}
