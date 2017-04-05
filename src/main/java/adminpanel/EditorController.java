package adminpanel;


import entities.Directory;
import entities.Professional;
import entities.Room;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import entities.Node;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.stage.Stage;
import main.ApplicationController;
import main.DatabaseController;
import main.DatabaseException;

import javax.xml.soap.Text;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
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
	private TextField descriptField;
	@FXML
	private TextField xCoordField;
	@FXML
	private TextField yCoordField;
	@FXML
	private ImageView imageViewMap;
	@FXML
	private Pane contentPane;
	@FXML
	private Button modifyRoomBtn;
	@FXML
	private Button cancelBtn;
	@FXML
	private Button deleteRoomBtn;
	@FXML
	private Button confirmBtn;
	@FXML
	private ChoiceBox<Professional> proChoiceBox;
	@FXML
	private Label roomTextLbl;
	@FXML
	private Button addCustomProBtn;

	private AddProfessionalController addProController;

	// TODO: Add click+drag to select a rectangle area of nodes/a node

	private Image map4;
	private ArrayList<Line> lines = new ArrayList<Line>();
	private Directory directory;
	private Room kiosk;

	// TODO: We want to have this use a directory instead of a list of nodes or a list of rooms

	private Node selectedNode; // you select a node by double clicking
	private Shape selectedShape; // This and the selectedNode should be set at the same time

	// Primary is left click and secondary is right click
	// these keep track of which button was pressed last on the mouse
	private boolean primaryPressed;
	private boolean secondaryPressed;

	private double releasedX;
	private double releasedY;

	private static final Color DEFAULT_SHAPE_COLOR = Color.web("0x0000FF");
	private static final Color SELECTED_SHAPE_COLOR = Color.BLACK;
	private static final Color CONNECTION_LINE_COLOR = Color.BLACK;
	private static final Color KIOSK_COLOR = Color.RED;

	private static final double RECTANGLE_WIDTH = 7;
	private static final double RECTANGLE_HEIGHT = 7;
	private static final double CIRCLE_RADIUS = 5;
	private static final String KIOSK_NAME = "You Are Here";
	private Professional selectedProf;
	private String roomList;
	private ArrayList<Professional> proList;



	@Override
	public void initialize(URL location, ResourceBundle resources) {
		//Grab the database controller from main and use it to populate our directory
		this.directory = ApplicationController.getDirectory();

//		//make kiosk
//		this.kiosk = new Room(353.5, 122.5);
//		this.kiosk.setName(KIOSK_NAME);
//		this.directory.addRoom(this.kiosk);

//		this.kiosk = null;
//		for (Room r : this.directory.getRooms()) {
//			if (r.getName().equalsIgnoreCase("YOU ARE HERE")) {
//				this.kiosk = r;
//			}
//		}
//		if (this.kiosk == null) {
//			this.kiosk = new Room(353.5, 122.5, "You are here", "this is the kiosk");
//		}

		//Add map
		this.map4 = new Image("/4_thefourthfloor.png");
		this.imageViewMap.setImage(this.map4);
		this.displayNodes(); // draws the nodes from the directory
		this.displayRooms();
		this.imageViewMap.setPickOnBounds(true);

		this.redrawLines();

		this.imageViewMap.setOnMouseClicked(e -> {
			this.setFields(e.getX(), e.getY());
			//Create node on double click
			if(e.getClickCount() == 2) {
				this.addNode(e.getX(), e.getY());
			}
			//Paint something at that location
			//update the text boxes

			// reset selected circle and node
			this.selectedNode = null;
			if(this.selectedShape != null)
				this.selectedShape.setFill(this.DEFAULT_SHAPE_COLOR);
			this.selectedShape = null;
		});
		//populate box for professionals
		this.populateChoiceBox();
		this.proList = new ArrayList<>();
		for (Professional pro: this.directory.getProfessionals()) {
			this.proList.add(pro);

		}
		this.selectChoiceBox();




	}

	public void selectChoiceBox() {

		this.proChoiceBox.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>()
		{
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				//proTextLbl.setText(proList.get(newValue.intValue() - 1).toString());

				System.out.println(newValue.intValue());
				System.out.println(oldValue.intValue());
				System.out.println(proList.size());
				if(proList.size() != 0 && newValue.intValue() >= 0) {
					EditorController.this.selectedProf = proList.get(newValue.intValue());
				}
				roomTextLbl.setText(selectedProf.getLocations().toString());
			}
		});
	}

	@FXML
	public void addProfToRoom() {
		if (this.selectedNode == null || !(this.selectedNode instanceof Room)) {
			return;
		} else {
			System.out.println(this.selectedProf);
			System.out.println(this.selectedNode);
			this.selectedProf.addLocation((Room)this.selectedNode);
			this.roomList = "";
			for (Room r: this.selectedProf.getLocations())
				this.roomList += r.getName() + ", ";
			this.roomTextLbl.setText(this.roomList);
		}

	}
	@FXML
	public void delProfFromRoom() {
		if (this.selectedNode == null) {
			return;
		} else {
			System.out.println(this.selectedProf.getLocations().size());
			this.selectedProf.getLocations().forEach(room -> {
				if(room.equals(this.selectedNode)) {
					this.selectedProf.removeLocation(room);
				}
			});
			System.out.println(this.selectedProf.getLocations().size());

			this.roomList = "";
			for (Room r: this.selectedProf.getLocations())
				this.roomList += r.getName() + ", ";
			this.roomTextLbl.setText(this.roomList);
		}

	}

	public void populateChoiceBox() {

		this.proChoiceBox.setItems(FXCollections.observableArrayList(this.directory.getProfessionals()));
	}

	@FXML
	public void refreshBtnClicked() {
		this.populateChoiceBox();

		for (Professional pro: this.directory.getProfessionals()) {
			this.proList.add(pro);

		}
	}

	@FXML
	public void addCustomProBtnPressed() throws IOException {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(this.getClass().getResource("/AddProUI.fxml"));
		this.addProController = loader.getController();
		//this.addProController.setEditorController(this);
		System.out.print("Onto the AddPro");
		Scene addProScene = new Scene(loader.load());
		Stage addProStage = new Stage();
		addProStage.setScene(addProScene);

		addProStage.showAndWait();
	}


	@FXML
	public void confirmBtnPressed() {
		this.directory.getRooms().forEach(room -> {
			System.out.println(room.getName());
		});

		try {


			ApplicationController.dbc.destructiveSaveDirectory(this.directory);
		} catch (DatabaseException e) {
			System.err.println("\n\nDATABASE DAMAGED\n\n");
			e.printStackTrace();
			System.err.println("\n\nDATABASE DAMAGED\n\n");
		}
	}

	@FXML
	public void addRoomBtnClicked() {
		this.addRoom(this.readX(), this.readY(), this.nameField.getText(), this.descriptField.getText());
	}

	@FXML
	public void modifyRoomBtnClicked() {
		if(this.selectedNode == null) return;
		if(this.selectedNode instanceof Room) {
			this.updateSelectedRoom(this.readX(), this.readY(), this.nameField.getText(), this.descriptField.getText());

		} else {
			this.updateSelectedNode(this.readX(), this.readY());

		}
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
		this.paintNodeOnLocation(newNode);
	}

	private void addRoom(double x, double y, String name, String description) {
		Room newRoom = new Room(x-this.RECTANGLE_WIDTH/2, y-this.RECTANGLE_HEIGHT/2, name, description);
		this.directory.addRoom(newRoom);
		this.paintRoomOnLocation(newRoom);
	}

	private void updateSelectedNode(double x, double y) {
		this.selectedNode.moveTo(x, y);
		// TODO: This might be bad coding practice, but it helps me generalize other code

		Circle selectedCircle = (Circle) this.selectedShape;
		selectedCircle.setCenterX(x);
		selectedCircle.setCenterY(y);
	}

	private void updateSelectedRoom(double x, double y, String name, String description) {
		this.selectedNode.moveTo(x, y);
		((Room) this.selectedNode).setName(name);
		((Room) this.selectedNode).setDescription(description);
		Rectangle selectedRectangle = (Rectangle) this.selectedShape;
		selectedRectangle.setX(x);
		selectedRectangle.setY(y);
	}

	private void deleteSelectedNode() {
		if(this.selectedNode == null) return;


		this.selectedNode.disconnectAll();
		this.directory.removeNodeOrRoom(this.selectedNode);
		this.selectedNode = null;
		// now garbage collector has to do its work

		this.contentPane.getChildren().remove(this.selectedShape);
		this.selectedShape = null;

		this.redrawLines();

	}

	public void paintNodeOnLocation(Node n) {
		Circle circ;
		circ = new Circle(n.getX(), n.getY(), this.CIRCLE_RADIUS,this.DEFAULT_SHAPE_COLOR);

		this.contentPane.getChildren().add(circ);
		circ.setVisible(true);

		circ.setOnMouseClicked((MouseEvent e) ->{
			EditorController.this.onShapeClick(e, n);
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
			EditorController.this.onShapeReleased(e, n);
		});
	}

	public void paintRoomOnLocation(Room r) {
		Rectangle rect;
		rect = new Rectangle(r.getX(), r.getY(), this.RECTANGLE_WIDTH, this.RECTANGLE_HEIGHT);
//		if (r.getName().equals(KIOSK_NAME)) {
//			rect.setFill(KIOSK_COLOR);
//		} else {
			rect.setFill(this.DEFAULT_SHAPE_COLOR);
//		}

		this.contentPane.getChildren().add(rect);
		rect.setVisible(true);

		rect.setOnMouseClicked((MouseEvent e) ->{
			EditorController.this.onShapeClick(e, r);
			this.nameField.setText(r.getName());
			this.descriptField.setText((r.getDescription()));
		});

		rect.setOnMouseDragged(e->{
			EditorController.this.onRectangleDrag(e, r);
		});

		// Working as intended
		rect.setOnMousePressed(e->{
			this.primaryPressed = e.isPrimaryButtonDown();
			this.secondaryPressed = e.isSecondaryButtonDown();
		});


		rect.setOnMouseReleased(e->{
			EditorController.this.onShapeReleased(e, r);
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

		this.directory.getNodes().forEach(node -> {
			Node[] adjacents = node.getNeighbors().toArray(new Node[node.getNeighbors().size()]);
			for(int connection = 0; connection < adjacents.length; connection++) {
				Node connected = adjacents[connection];
				double startX = node.getX();
				double startY = node.getY();
				double endX = connected.getX();
				double endY = connected.getY();
				Line line = new Line();
				line.setStartX(startX);
				line.setStartY(startY);
				if(connected instanceof Room) {
					line.setEndX(endX + this.RECTANGLE_WIDTH/2);
					line.setEndY(endY + this.RECTANGLE_HEIGHT/2);
				} else {
					line.setEndX(endX);
					line.setEndY(endY);
				}


				this.lines.add(line);

				this.contentPane.getChildren().add(line);
				line.setVisible(true);
			}
		});

		this.directory.getRooms().forEach(node -> {
			Node[] adjacents = node.getNeighbors().toArray(new Node[node.getNeighbors().size()]);
			for(int connection = 0; connection < adjacents.length; connection++) {
				Node connected = adjacents[connection];
				double startX = node.getX();
				double startY = node.getY();
				double endX = connected.getX();
				double endY = connected.getY();
				Line line = new Line();
				line.setStartX(startX + this.RECTANGLE_WIDTH/2);
				line.setStartY(startY + this.RECTANGLE_HEIGHT/2);
				if(connected instanceof Room) {
					line.setEndX(endX + this.RECTANGLE_WIDTH/2);
					line.setEndY(endY + this.RECTANGLE_HEIGHT/2);
				} else {
					line.setEndX(endX);
					line.setEndY(endY);
				}

				this.lines.add(line);

				this.contentPane.getChildren().add(line);
				line.setVisible(true);
			}
		});
	}

	public void displayNodes() {
		this.directory.getNodes().forEach(node -> this.paintNodeOnLocation(node));
	}

	public void displayRooms() {
		this.directory.getRooms().forEach(room -> this.paintRoomOnLocation(room));
	}


	@FXML
	private void logoutBtnClicked() {

	}

	public void setFields(double x, double y) {
		this.xCoordField.setText(x+"");
		this.yCoordField.setText(y+"");
	}

	public void onShapeClick(MouseEvent e, Node n) {

		// update text fields
		this.setFields(n.getX(), n.getY());

		// check if you single click
		// so, then you are selecting a node
		System.out.println("Clicked on a rectangle " + (this.selectedNode != null && !this.selectedNode.equals(n) && this.secondaryPressed) + " " + this.primaryPressed);
		if(e.getClickCount() == 1 && this.primaryPressed) {
			if(this.selectedShape != null) {
				this.selectedShape.setFill(this.DEFAULT_SHAPE_COLOR);
			}

			this.selectedShape = (Shape) e.getSource();
			this.selectedNode = n;
			this.selectedShape.setFill(this.SELECTED_SHAPE_COLOR);
		} else if(this.selectedNode != null && !this.selectedNode.equals(n) && this.secondaryPressed) {
			// ^ checks if there has been a node selected,
			// checks if the node selected is not the node we are clicking on
			// and checks if the button pressed is the right mouse button (secondary)

			// finally check if they are connected or not
			// if they are connected, remove the connection
			// if they are not connected, add a connection
			System.out.println("Clicked on a rectangle " + this.selectedNode.getNeighbors().contains(n));
			this.selectedNode.connectOrDisconnect(n);
			this.redrawLines();
		}
	}

	// This is going to allow us to drag a node!!!
	public void onCircleDrag(MouseEvent e, Node n) {
		if(this.selectedNode != null && this.selectedNode.equals(n)) {
			if(this.primaryPressed) {
				this.selectedShape = (Shape) e.getSource();
				this.updateSelectedNode(e.getX(), e.getY());
				this.setFields(this.selectedNode.getX(), this.selectedNode.getY());
				this.redrawLines();
			} else if(this.secondaryPressed) {
				// right click drag on the selected node
			}

		}

	}

	public void onShapeReleased(MouseEvent e, Node n) {
		this.releasedX = e.getX();
		this.releasedY = e.getY();

		// if the releasedX or Y is negative we want to remove the node

		if(this.releasedX < 0 || this.releasedY < 0) {
			this.deleteSelectedNode();
		}
	}


	public void onRectangleDrag(MouseEvent e, Room r) {
		if(this.selectedNode != null && this.selectedNode.equals(r)) {
			if(this.primaryPressed) {
				this.selectedShape = (Rectangle) e.getSource();
				this.updateSelectedRoom(e.getX()-this.RECTANGLE_WIDTH/2, e.getY()-this.RECTANGLE_HEIGHT/2, r.getName(), r.getDescription());
				this.setFields(this.selectedNode.getX(), this.selectedNode.getY());
				this.redrawLines();
			} else if(this.secondaryPressed) {
				// right click drag on the selected node
			}

		}

	}




}
