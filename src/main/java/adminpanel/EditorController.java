package adminpanel;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import javax.xml.soap.Text;
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
	@FXML
	private Pane contentPane;
	@FXML
	private TextField roomNumberField;
	@FXML
	private Button modifyRoomBtn;
	@FXML
	private Button cancelBtn;
	@FXML
	private Button deleteRoomBtn;


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
		//System.out.print("Map Clicked");
		imageViewMap.setPickOnBounds(true);


		imageViewMap.setOnMouseClicked(e -> {
			System.out.println("[" + e.getX() + ", " + e.getY() + "]");
			//Paint something at that location
			paintOnLocation(e.getX(), e.getY());

		});
	}



	public void paintOnLocation(double x, double y) {
		Circle circ;
		circ = new Circle(x,y,5, Color.web("0x0000FF") );

		contentPane.getChildren().add(circ);
		circ.setVisible(true);

	}


	@FXML
	private void logoutBtnClicked() {
	}
}
