package adminpanel;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
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
	private Node selectedNode; // you select a node by double clicking
	private Circle selectedCircle; // This and the selectedNode should be set at the same time

	// Primary is left click and secondary is right click
	// these keep track of which button was pressed last on the mouse
	private boolean primaryPressed;
	private boolean secondaryPressed;

	private static final Color DEFAULT_CIRCLE_COLOR = Color.web("0x0000FF");
	private static final Color SELECTED_CIRCLE_COLOR = Color.BLACK;

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

		this.imageViewMap.setPickOnBounds(true);


		this.imageViewMap.setOnMouseClicked(e -> {
			//Create node on click
			this.clickNode = new Node(e.getX(), e.getY());
			//Paint something at that location
			this.paintOnLocation(this.clickNode);
			//update the text boxes
			this.setFields(this.clickNode);


			// reset selected circle and node
			this.selectedNode = null;
			if(this.selectedCircle != null)
				this.selectedCircle.setFill(this.DEFAULT_CIRCLE_COLOR);
			this.selectedCircle = null;
		});

		this.imageViewMap.setOnMouseDragged(e->{
			System.out.println("Mouse Dragged"+e.toString());

		});
	}

	@FXML
	private void addRoomBtnClicked() {
	}



	public void paintOnLocation(Node n) {
		Circle circ;
		circ = new Circle(n.getX(), n.getY(), 5,this.DEFAULT_CIRCLE_COLOR );

		this.contentPane.getChildren().add(circ);
		circ.setVisible(true);





	}

	public void displayNodes(ArrayList<entities.Node> alon) {

		for (int i = 0; i < alon.size(); i++) {
			Circle circ;
			double nodeX = alon.get(i).getX();
			double nodeY = alon.get(i).getY();

			circ = new Circle(nodeX, nodeY, 5, this.DEFAULT_CIRCLE_COLOR);
			this.contentPane.getChildren().add(circ);
			circ.setVisible(true);

			// needs to be final to work
			final int tempIndex = i;
			circ.setOnMouseClicked((MouseEvent e) ->{
				System.out.println(e.isPrimaryButtonDown());
				System.out.println(e.toString());
				EditorController.this.onCircleClick(e, alon.get(tempIndex));
			});

			circ.setOnMouseDragged(e->{
				EditorController.this.onCircleDrag(e, alon.get(tempIndex));
			});

			// Working as intended
			circ.setOnMousePressed(e->{
				this.primaryPressed = e.isPrimaryButtonDown();
				this.secondaryPressed = e.isSecondaryButtonDown();
			});

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

	public void onCircleClick(MouseEvent e, Node n) {
		// check if you double click

		// if you double click, then you are selecting a node
		if(e.getClickCount() == 2 && this.primaryPressed) {
			if(this.selectedCircle != null) this.selectedCircle.setFill(this.DEFAULT_CIRCLE_COLOR);

			this.selectedCircle = (Circle) e.getSource();
			this.selectedNode = n;
			this.selectedCircle.setFill(this.SELECTED_CIRCLE_COLOR);
		}
	}

	// This is going to allow us to drag a node!!!
	public void onCircleDrag(MouseEvent e, Node n) {
		if(this.selectedNode != null && this.selectedNode.equals(n) && this.primaryPressed) {
			this.selectedNode.moveTo(e.getX(), e.getY());
			this.selectedCircle = (Circle) e.getSource();
			this.selectedCircle.setCenterX(e.getX());
			this.selectedCircle.setCenterY(e.getY());
			this.setFields(this.selectedNode);
		}

	}
}