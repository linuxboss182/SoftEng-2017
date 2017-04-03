package adminpanel;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import entities.Node;

import javax.xml.soap.Text;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
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
	private TextField descriptionField;
	@FXML
	private TextField xCoordField;
	@FXML
	private TextField yCoordField;
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
	Node clickNode;
	ArrayList<Node> alon = new ArrayList<Node>();
	Node n;
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		//Simulate populating the nodes list from database with random nodes
		for (int i = 0; i < 10; i++) {

			Random r = new Random();
			double randX = 0.0 + r.nextDouble() * 750.0;
			double randY = 0.0 + r.nextDouble() * 450.0;
			Node newNode = new Node(randX, randY);
			this.alon.add(newNode);
		}

		//Add map
		this.map4 = new Image("/4_thefourthfloor.png");
		this.imageViewMap.setImage(this.map4);
		this.displayNodes(this.alon);
	}

	@FXML
	private void addRoomBtnClicked() {
	}

	@FXML
	private void mapClicked() {
		//System.out.print("Map Clicked");
		this.imageViewMap.setPickOnBounds(true);


		this.imageViewMap.setOnMouseClicked(e -> {
			System.out.println("[" + e.getX() + ", " + e.getY() + "]");
			//Create node on click
			this.clickNode = new Node(e.getX(), e.getY());
			//Paint something at that location
			this.paintOnLocation(this.clickNode);
			//update the text boxes
			this.setFields(this.clickNode);

		});
	}



	public void paintOnLocation(Node n) {
		Circle circ;
		circ = new Circle(n.getX(), n.getY(), 5, Color.web("0x0000FF") );

		this.contentPane.getChildren().add(circ);
		circ.setVisible(true);





	}

	public void displayNodes(ArrayList<entities.Node> alon) {

		for (int i = 0; i < alon.size(); i++) {
			Circle circ;
			double nodeX = alon.get(i).getX();
			double nodeY = alon.get(i).getY();
			;
			circ = new Circle(nodeX, nodeY, 5, Color.web("0x0000FF"));
			this.contentPane.getChildren().add(circ);
			circ.setVisible(true);
		}


	}


	@FXML
	private void logoutBtnClicked() {

	}

	public void setFields(Node n) {
		String xVal = Double.toString(n.getX());
		String yVal = Double.toString(n.getY());
		this.xCoordField.setText(xVal);
		this.yCoordField.setText(yVal);
	}

	public void onCircleClick(ActionEvent e, Node n) {


	}
}
