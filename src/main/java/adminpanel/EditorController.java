package adminpanel;


import entities.Directory;
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
import javafx.scene.shape.Line;
import main.DatabaseController;

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

	// TODO: Add click+drag to select a rectangle area of nodes/a node

	Image map4;
	Node clickNode;
	ArrayList<Line> lines = new ArrayList<Line>();
	Directory directory;

	// TODO: We want to have this use a directory instead of a list of nodes or a list of rooms

	private Node selectedNode; // you select a node by double clicking
	private Circle selectedCircle; // This and the selectedNode should be set at the same time

	// Primary is left click and secondary is right click
	// these keep track of which button was pressed last on the mouse
	private boolean primaryPressed;
	private boolean secondaryPressed;

	private double releasedX;
	private double releasedY;

	private static final Color DEFAULT_CIRCLE_COLOR = Color.web("0x0000FF");
	private static final Color SELECTED_CIRCLE_COLOR = Color.BLACK;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		//Grab the database controller from main and use it to populate our directory
		this.directory = main.ApplicationController.dbc.getDirectory();

		//Add map
		this.map4 = new Image("/4_thefourthfloor.png");
		this.imageViewMap.setImage(this.map4);
		this.displayNodes(new ArrayList<Node>(this.directory.getNodes()));
		this.imageViewMap.setPickOnBounds(true);


		this.imageViewMap.setOnMouseClicked(e -> {
			//Create node on double click
			if(e.getClickCount() == 2) {
				this.addNode(e.getX(), e.getY());
			}
			//Paint something at that location
			//update the text boxes


			// reset selected circle and node
			this.selectedNode = null;
			if(this.selectedCircle != null)
				this.selectedCircle.setFill(this.DEFAULT_CIRCLE_COLOR);
			this.selectedCircle = null;
		});

		// this could be helpful for selecting a large area
//		this.imageViewMap.setOnMouseDragged(e->{
//
//
//		});
	}

	@FXML
	public void addRoomBtnClicked() {
		this.addNode(this.readX(), this.readY());
	}

	@FXML
	public void modifyRoomBtnClicked() {
		this.updateSelectedNode(this.readX(), this.readY());
	}

	@FXML
	public void deleteRoomBtnClicked() {
		this.deleteSelectedNode();
	}

	private double readX() {
		return Double.parseDouble(this.xCoordField.getText());
	}

	private double readY() {
		return Double.parseDouble(this.yCoordField.getText());
	}

	private void addNode(double x, double y) {
		Node newNode = new Node(x, y);
		this.directory.addNode(newNode);
		this.paintOnLocation(newNode);
	}

	private void updateSelectedNode(double x, double y) {
		this.selectedNode.moveTo(x, y);
		this.selectedCircle.setCenterX(x);
		this.selectedCircle.setCenterY(y);

	}

	private void deleteSelectedNode() {
		this.selectedNode.disconnectAll();
		this.directory.removeNode(this.selectedNode);
		this.selectedNode = null;
		// now garbage collector has to do its work

		this.contentPane.getChildren().remove(this.selectedCircle);
		this.selectedCircle = null;

		this.redrawLines();
	}

	public void paintOnLocation(Node n) {
		Circle circ;
		circ = new Circle(n.getX(), n.getY(), 5,this.DEFAULT_CIRCLE_COLOR );

		this.contentPane.getChildren().add(circ);
		circ.setVisible(true);

		circ.setOnMouseClicked((MouseEvent e) ->{
			EditorController.this.onCircleClick(e, n);
		});

		circ.setOnMouseDragged(e->{
			EditorController.this.onCircleDrag(e, n);
		});

		// Working as intended
		circ.setOnMousePressed(e->{
			this.primaryPressed = e.isPrimaryButtonDown();
			this.secondaryPressed = e.isSecondaryButtonDown();
		});


		circ.setOnMouseReleased(e->{
			EditorController.this.onCircleReleased(e, n);
		});


	}

	public void redrawLines() {
		// clear arraylist
		for(int i = 0; i < this.lines.size(); i++) {
			this.contentPane.getChildren().remove(this.lines.get(i));
		}
		this.lines.clear();
		// repopulate arraylist
		// then draw the lines
		this.fillDrawLines();
	}

	private void fillDrawLines() {
		ArrayList<Node> tempArrayListOfNodes = new ArrayList<>(this.directory.getNodes());
		for(int node = 0; node < tempArrayListOfNodes.size(); node++) {
			Node current = tempArrayListOfNodes.get(node);
			Node[] adjacents = current.getNeighbors().toArray(new Node[current.getNeighbors().size()]);
			for(int connection = 0; connection < adjacents.length; connection++) {
				Node connected = adjacents[connection];
				double startX = current.getX();
				double startY = current.getY();
				double endX = connected.getX();
				double endY = connected.getY();
				Line line = new Line();
				line.setStartX(startX);
				line.setStartY(startY);
				line.setEndX(endX);
				line.setEndY(endY);

				this.lines.add(line);

				this.contentPane.getChildren().add(line);
				line.setVisible(true);
			}
		}
	}

	public void displayNodes(ArrayList<entities.Node> alon) {
		for (int i = 0; i < alon.size(); i++) {
			this.paintOnLocation((alon.get(i)));
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

		// update text fields
		this.setFields(n);

		// check if you single click
		// so, then you are selecting a node
		if(e.getClickCount() == 1 && this.primaryPressed) {
			if(this.selectedCircle != null) this.selectedCircle.setFill(this.DEFAULT_CIRCLE_COLOR);

			this.selectedCircle = (Circle) e.getSource();
			this.selectedNode = n;
			this.selectedCircle.setFill(this.SELECTED_CIRCLE_COLOR);
		} else if(this.selectedNode != null && !this.selectedNode.equals(n) && this.secondaryPressed) {
			// ^ checks if there has been a node selected,
			// checks if the node selected is not the node we are clicking on
			// and checks if the button pressed is the right mouse button (secondary)

			// finally check if they are connected or not
			// if they are connected, remove the connection
			// if they are not connected, add a connection
			if(this.selectedNode.areConnected(n)) {
				this.selectedNode.disconnect(n);
				this.redrawLines();
			} else {
				this.selectedNode.connect(n);
				this.redrawLines();
			}
		}
	}

	// This is going to allow us to drag a node!!!
	public void onCircleDrag(MouseEvent e, Node n) {
		if(this.selectedNode != null && this.selectedNode.equals(n)) {
			if(this.primaryPressed) {
				this.selectedCircle = (Circle) e.getSource();
				this.updateSelectedNode(e.getX(), e.getY());
				this.setFields(this.selectedNode);
				this.redrawLines();
			} else if(this.secondaryPressed) {
				// right click drag on the selected node
			}

		}

	}

	public void onCircleReleased(MouseEvent e, Node n) {
		this.releasedX = e.getX();
		this.releasedY = e.getY();

		// if the releasedX or Y is negative we want to remove the node

		if(this.releasedX < 0 || this.releasedY < 0) {
			this.deleteSelectedNode();
		}
	}
}
