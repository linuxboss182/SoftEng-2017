package controllers;


import entities.Professional;
import entities.Room;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.EventHandler;
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
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import entities.Node;
import javafx.scene.shape.Shape;
import javafx.stage.Stage;
import main.ApplicationController;
import main.DatabaseException;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.StringJoiner;
import java.util.stream.Collectors;

public class EditorController extends MapDisplayController implements Initializable
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
	@FXML
	private Button deleteProfBtn;
	@FXML
	protected Pane linePane;
	@FXML
	protected Pane nodePane;
	@FXML
	public AnchorPane contentAnchor = new AnchorPane();

	AddProfessionalController addProController = new AddProfessionalController();

	final double SCALE_DELTA = 1.1; //The rate to scale
	private double clickedX, clickedY; //Where we clicked on the anchorPane
	private boolean beingDragged; //Protects the imageView for being dragged

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		//Load
		this.setPanes(linePane, nodePane); //Set the panes
		this.directory = ApplicationController.getDirectory(); //Grab the database controller from main and use it to populate our directory
		this.loadMap();
		this.imageViewMap.setImage(this.map); //Load background

		//Init
		this.populateChoiceBox(); //populate box for professionals
		this.proList = new ArrayList<>(); //TODO: OBSOLETE, should be in directory
		for (Professional pro: this.directory.getProfessionals()) {
			this.proList.add(pro);
		}
		this.selectChoiceBox();
		this.kiosk = null;
		for (Room r : this.directory.getRooms()) {
			if (r.getName().equalsIgnoreCase("YOU ARE HERE")) {
				this.kiosk = r;
			}
		}

		this.displayNodes(this.directory.getNodesOnFloor(floor)); //draws the nodes from the directory
		this.redrawLines();  //deletes all the lines then draws them again from the directory

		//Lets us click through items
		this.imageViewMap.setPickOnBounds(true);
		this.contentAnchor.setPickOnBounds(false);
		this.topPane.setPickOnBounds(false);

		this.installPaneListeners();

		// Add listeners to all nodes
		this.directory.getNodes().forEach(this::addNodeListeners);
	}

	@FXML
	private void logoutBtnClicked() {
		try {
			Parent userUI = (BorderPane) FXMLLoader.load(this.getClass().getResource("/UserDestination.fxml"));
			this.botPane.getScene().setRoot(userUI);
		} catch(IOException e) {
			e.printStackTrace();
		}
	}

	@FXML
	public void addProfToRoom() {
		if (this.selectedProf == null || this.selectedNode == null) return;

		this.selectedNode.applyToRoom(room -> this.selectedProf.addLocation(room));
		// TODO: Use StringBuilder
		String roomList = "";
		for (Room r: this.selectedProf.getLocations()) {
			roomList += r.getName() + ", ";
		}
		this.roomTextLbl.setText(roomList);
	}

	@FXML
	public void delProfFromRoom() {
		if (this.selectedNode == null || this.selectedProf == null) return;

		this.selectedNode.applyToRoom(room -> this.selectedProf.removeLocation(room));

		// TODO: Use StringBuilder
		String roomList = "";
		for (Room r: this.selectedProf.getLocations()) {
			roomList += r.getName() + ", ";
		}
		this.roomTextLbl.setText(roomList);

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
		Scene addProScene = new Scene(loader.load());
		Stage addProStage = new Stage();
		addProStage.setScene(addProScene);

		addProStage.showAndWait();
	}

	@FXML
	public void deleteProfBtnClicked () {
		this.directory.removeProfessional(this.selectedProf);
	//	this.refreshBtnClicked();
	}


	@FXML
	public void confirmBtnPressed() {
		this.directory.getRooms().forEach(room -> {
			System.out.println("Attempting to save room: " + room.getName() + " to database...");
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
	public void modifyRoomBtnClicked() { //TODO
		if(this.selectedNode == null) return;

		if(this.selectedNode.containsRoom()) {
			this.updateSelectedRoom(this.readX(), this.readY(), this.nameField.getText(), this.descriptField.getText());
		} else {
			this.updateSelectedNode(this.readX(), this.readY());
		}
	}

	@FXML
	public void deleteRoomBtnClicked() {
		this.deleteSelectedNode();
	}

	/* **** Non-FXML functions **** */

	//Editor
	@Override
	public void displayNodes() {
		Set<Circle> nodeShapes = new HashSet<>();
		for (Node n : this.directory.getNodes()) {
			nodeShapes.add(n.getShape());
		}
		this.topPane.getChildren().setAll(nodeShapes);
//		this.topPane.getChildren().setAll(this.directory.getNodes().stream().map(Node::getShape).collect(Collectors.toSet()));

//		this.topPane.getChildren().removeAll();
//		this.directory.getNodes().forEach(node -> this.paintNode(node));
	}


	public void selectChoiceBox(){
		this.proChoiceBox.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				//proTextLbl.setText(proList.get(newValue.intValue() - 1).toString());

				if(proList.size() != 0 && newValue.intValue() >= 0) {
					EditorController.this.selectedProf = proList.get(newValue.intValue());
				}

				// Build a string listing the names of the professional's rooms
				StringJoiner roomList = new StringJoiner(", ");
				selectedProf.getLocations().forEach(room -> roomList.add(room.getName()));

				roomTextLbl.setText(roomList.toString());
			}
		});
	}

	public void populateChoiceBox() {
		this.proChoiceBox.setItems(FXCollections.observableArrayList(this.directory.getProfessionals()));
	}


	/**
	 * Add listeners to the given node
	 *
	 * @note No other function should add the base listeners to nodes.
	 */
	private void addNodeListeners(Node node) {
		node.getShape().setOnMouseClicked(event -> this.onShapeClick(event, node));
		node.getShape().setOnMouseDragged(event -> this.onShapeDrag(event, node));
		node.getShape().setOnMouseReleased(event -> this.onShapeReleased(event, node));
		node.getShape().setOnMousePressed((MouseEvent event) -> {
			this.primaryPressed = event.isPrimaryButtonDown();
			this.secondaryPressed = event.isSecondaryButtonDown();
		});
	}

	private double readX() {
		return Double.parseDouble(this.xCoordField.getText());
	}

	private double readY() {
		return Double.parseDouble(this.yCoordField.getText());
	}


	/**
	 * Add a new room with the given information to the directory.
	 * Also add a new node associated ith the room.
	 */
	private void addRoom(double x, double y, String name, String description) { //TODO
		Node newNode = this.directory.addNewRoomNode(x, y, name, description);
		this.paintNode(newNode);
		this.addNodeListeners(newNode);
	}

	/** Add a new node to the directory at the given coordinates */
	private void addNode(double x, double y) {
		Node newNode = this.directory.addNewNode(x, y);
		this.paintNode(newNode);
		this.addNodeListeners(newNode);
	}

	private void updateSelectedRoom(double x, double y, String name, String description) { //TODO
//		this.selectedNode.moveTo(x, y);
//		((Room) this.selectedNode).setName(name);
//		((Room) this.selectedNode).setDescription(description);
//		Rectangle selectedRectangle = (Rectangle) this.selectedShape;
//		selectedRectangle.setX(x);
//		selectedRectangle.setY(y);
		this.selectedNode.applyToRoom(room -> {
			room.setName(name);
			room.setDescription(description);
		});
		// TODO: Update the location of the node, whether or not it is a room
	}

	private void updateSelectedNode(double x, double y) { //TODO
		this.selectedNode.moveTo(x, y);

		Circle selectedCircle = (Circle) this.selectedShape;
		selectedCircle.setCenterX(x);
		selectedCircle.setCenterY(y);
	}

	private void deleteSelectedNode() { // TODO: Separate this from a function that deletes both room and node
		if(this.selectedNode == null) return;

		this.selectedNode.disconnectAll(); // maybe should be in Directory?
		this.directory.removeNodeAndRoom(this.selectedNode);
		this.selectedNode = null;
		// now garbage collector has to do its work

		this.contentPane.getChildren().remove(this.selectedShape);
		this.selectedShape = null;

		this.redrawLines();
	}

	public void redrawLines() {
		this.botPane.getChildren().clear();
		this.directory.getNodes().forEach(node -> {
				node.getNeighbors().forEach(Neighbor -> {
					this.paintLine(node,Neighbor);
				});
		});
	}


	public void setFields(double x, double y) {
		this.xCoordField.setText(x+"");
		this.yCoordField.setText(y+"");
	}



	///////////////////////
	/////EVENT HANDLERS////
	///////////////////////

	public void installPaneListeners(){
		botPane.setOnMouseClicked(e -> {
			this.setFields(e.getX(), e.getY());
			//Create node on double click
			if(e.getClickCount() == 2) {
				this.addNode(e.getX(), e.getY());
			}

			if(this.selectedShape != null) {
				this.selectedShape.setFill(this.DEFAULT_SHAPE_COLOR);
				selectedShape = null;
			}

			this.displayNodes(this.directory.getNodesOnFloor(floor));

			this.selectedNode = null;
			this.selectedShape = null;
		});

		contentAnchor.setOnScroll(new EventHandler<ScrollEvent>() {
			@Override public void handle(ScrollEvent event) {
				event.consume();
				if (event.getDeltaY() == 0) {
					return;
				}
				double scaleFactor =
						(event.getDeltaY() > 0)
								? SCALE_DELTA
								: 1/SCALE_DELTA;

				double potentialScaleX = contentAnchor.getScaleX() * scaleFactor;
				double potentialScaleY = contentAnchor.getScaleY() * scaleFactor;
				// Pretty much just limit the scaling minimum to be 1/SCALE_DELTA
				potentialScaleX = (potentialScaleX < 1/SCALE_DELTA ? 1/SCALE_DELTA:potentialScaleX);
				potentialScaleY = (potentialScaleY < 1/SCALE_DELTA ? 1/SCALE_DELTA:potentialScaleY);
				contentAnchor.setScaleX(potentialScaleX);
				contentAnchor.setScaleY(potentialScaleY);
			}
		});

		contentAnchor.setOnMousePressed(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent event) {
				clickedX = event.getX();
				clickedY = event.getY();
			}
		});

		contentAnchor.setOnMouseDragged(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent event) {
				if(!beingDragged) {
					contentAnchor.setTranslateX(contentAnchor.getTranslateX() + event.getX() - clickedX);
					contentAnchor.setTranslateY(contentAnchor.getTranslateY() + event.getY() - clickedY);
				}
				event.consume();
			}
		});
	}

	public void onShapeClick(MouseEvent e, Node n) {
		// update text fields
		this.setFields(n.getX(), n.getY());

		// check if you single click
		// so, then you are selecting a node
		if(e.getClickCount() == 1 && this.primaryPressed) {

			if(this.selectedShape != null) {
				this.selectedShape.setFill(this.DEFAULT_SHAPE_COLOR);
				selectedShape = null;
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
			this.selectedNode.connectOrDisconnect(n);
			this.redrawLines();
		}
	}

	// This is going to allow us to drag a node!!!
	public void onShapeDrag(MouseEvent e, Node n) {
		this.beingDragged = true;
		if(this.selectedNode != null && this.selectedNode.equals(n)) {

			if(e.isPrimaryButtonDown()) {
				this.selectedShape = (Shape) e.getSource();
				this.updateSelectedNode(e.getX(), e.getY());
				this.setFields(this.selectedNode.getX(), this.selectedNode.getY());
				this.redrawLines();
			} else if(this.secondaryPressed) {
				// right click drag on the selected node
				// do nothing for now
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

		this.beingDragged = false;
	}

}
