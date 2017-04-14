package controllers;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Callback;
import main.ApplicationController;
import main.DatabaseException;

import java.io.IOException;
import java.net.URL;
import java.util.*;

import entities.Node;
import entities.Professional;
import entities.Room;

public class EditorController extends MapDisplayController implements Initializable
{
	private static final boolean DEBUGGING = false;

	@FXML
	private Button addRoomBtn;
	@FXML
	private Button logoutBtn;
	@FXML
	private TextField nameField;
	@FXML
	private TextArea descriptField;
	@FXML
	private TextField xCoordField;
	@FXML
	private TextField yCoordField;
	@FXML
	private ImageView imageViewMap;
	@FXML
	private Button modifyRoomBtn;
	@FXML
	private Button cancelBtn;
	@FXML
	private Button deleteRoomBtn;
	@FXML
	private Button confirmBtn;
//	@FXML
//	private ChoiceBox<Professional> proChoiceBox;
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
	@FXML
	public ChoiceBox floorChoiceBox;
	@FXML
	public TableView<Professional> roomProfTable;
	@FXML
	private TableColumn<Professional, String> roomCol;
	@FXML
	private TableColumn<Professional, String> profCol;

	@FXML
	private Text roomName;
	@FXML
	private Text yPos;
	@FXML
	private Label xPos;


	protected Node selectedNode; // you select a node by double clicking

	final double SCALE_DELTA = 1.1;
	final protected double zoomMin = 1/SCALE_DELTA;
	final protected double zoomMax = SCALE_DELTA*5;
	private double clickedX, clickedY; //Where we clicked on the anchorPane
	private boolean beingDragged; //Protects the imageView for being dragged

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		//Load
		this.setPanes(linePane, nodePane); //Set the panes
		directory = ApplicationController.getDirectory(); //Grab the database controller from main and use it to populate our directory
		iconController = ApplicationController.getIconController();
		this.loadMap();
		this.imageViewMap.setImage(this.map); //Load background
		this.imageViewMap.setPickOnBounds(true);
		if(floorChoiceBox != null) {
			initFloorChoiceBox();
		}

		// TODO: Move zoom initialization to separate function
		// I tested this value, and we want it to be defaulted here because the map does not start zoomed out all the way
		zoomSlider.setValue(2);
		zoomSlider.valueProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable,
			                    Number oldValue, Number newValue) {
				/**
				 * This one was a fun one.
				 * This math pretty much makes it so when the slider is at the far left, the map will be zoomed out all the way
				 * and when it's at the far right, it will be zoomed in all the way
				 * when it's at the left, zoomPercent is 0, so we want the full value of zoomMin to be the zoom coefficient
				 * when it's at the right, zoomPercent is 1, and we want the full value of zoomMax to be the zoom coefficient
				 * the equation is just that
				 */
				double zoomPercent = (zoomSlider.getValue()/100);
				double zoomCoefficient = zoomMin*(1 - zoomPercent) + zoomMax*(zoomPercent);
				contentAnchor.setScaleX(zoomCoefficient);
				contentAnchor.setScaleY(zoomCoefficient);
			}
		});

		this.kiosk = null;
		for (Room r : this.directory.getRooms()) {
			if (r.getName().equalsIgnoreCase("YOU ARE HERE")) {
				this.kiosk = r;
			}
		}

		this.redisplayGraph(); // redraw nodes and edges
		this.iconController.resetAllNodes();

		//Lets us click through items
		this.imageViewMap.setPickOnBounds(true);
		this.contentAnchor.setPickOnBounds(false);
		this.topPane.setPickOnBounds(false);

		this.installPaneListeners();

		// Add listeners to all nodes
		this.directory.getNodes().forEach(this::addNodeListeners);

		//Populate the tableview
		HashSet<Room> locations = new HashSet<>();
		for (Professional p: directory.getProfessionals()) {
			locations.addAll(p.getLocations());

		}
		populateTableView(directory.getProfessionals());

		//Listener for the tableview
		roomProfTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
			if (roomProfTable.getSelectionModel().getSelectedItem() != null) {
				// TODO: Allow professional selection from the TableView
				//selectedLocation = newValue;
			}
		});
	}

	/**
	 * Redraw all elements of the map and the professionals' elements
	 */
	// TODO: Use this more often
	private void redisplayAll() {
		this.redisplayGraph(); // nodes on this floor and lines between them
		this.populateTableView(directory.getProfessionals());
	}

	/**
	 * Redisplay the nodes on this floor and the lines
	 *
	 * If debugging, display all nodes
	 */
	private void redisplayGraph() {
//		if (EditorController.DEBUGGING) {
//			this.displayNodes(directory.getNodes());
//			this.redrawLines(directory.getNodes());
//		} else {
		this.displayNodes(directory.getNodesOnFloor(floor));
		this.redrawLines(directory.getNodesOnFloor(floor));
//		}
	}

	public void populateTableView (Collection<Professional> profs) {

//		roomCol.setCellValueFactory(cdf -> new SimpleStringProperty(cdf.getValue().toString()));

		roomCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Professional, String>, ObservableValue<String>>() {
			@Override
			public ObservableValue<String> call(TableColumn.CellDataFeatures<Professional, String> cdf) {
				return new SimpleStringProperty(cdf.getValue().getLocationNames());
			}
		});

		profCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Professional, String>, ObservableValue<String>>() {
			@Override
			public ObservableValue<String> call(TableColumn.CellDataFeatures<Professional, String> cdf) {
				return new SimpleStringProperty(cdf.getValue().getSurname()+", "+cdf.getValue().getGivenName()+" "+cdf.getValue().getTitle());
			}
		});

//		profCol.setCellValueFactory(new PropertyValueFactory<>("givenName"));

		roomProfTable.getSortOrder().add(profCol);
		roomProfTable.getSortOrder().add(roomCol);

		roomProfTable.getItems().setAll(profs);

		this.roomProfTable.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Professional>() {
			@Override
			public void changed(ObservableValue<? extends Professional> observable, Professional oldValue, Professional newValue) {
				selectedProf = newValue;
			}
		});
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

	private void changeFloor(int floor) {
		this.switchFloors(floor);
		this.imageViewMap.setImage(map);
		this.redisplayGraph();
	}

	@FXML
	public void addProfToRoom() {
		if (this.selectedProf == null || this.selectedNode == null) return;

		this.selectedNode.applyToRoom(room -> this.selectedProf.addLocation(room));

		this.populateTableView(directory.getProfessionals());
	}

	@FXML
	public void delProfFromRoom() {
		if (this.selectedNode == null || this.selectedProf == null) return;

		this.selectedNode.applyToRoom(room -> this.selectedProf.removeLocation(room));

		this.populateTableView(directory.getProfessionals());
	}

	@FXML
	public void addCustomProBtnPressed() throws IOException {
		AddProfessionalController addProController = new AddProfessionalController();
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(this.getClass().getResource("/AddProUI.fxml"));
		Scene addProScene = new Scene(loader.load());
		Stage addProStage = new Stage();
		addProStage.initOwner(contentAnchor.getScene().getWindow());
		addProStage.setScene(addProScene);
		addProStage.showAndWait();
		populateTableView(directory.getProfessionals());
	}

	@FXML
	public void deleteProfBtnClicked () {
		this.directory.removeProfessional(this.selectedProf);
		this.populateTableView(directory.getProfessionals());
	}


	@FXML
	public void confirmBtnPressed() {
		this.directory.getRooms().forEach(room ->
				System.out.println("Attempting to save room: "+room.getName()+" to database..."));

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
		if(this.yCoordField.getText().isEmpty() || this.xCoordField.getText().isEmpty()){
			if(this.yCoordField.getText().isEmpty()){
				yPos.setFill(Color.RED);
			}
			if(this.xCoordField.getText().isEmpty()){
				xPos.setTextFill(Color.RED);
			}
			return;
		}

		this.addNodeRoom(this.readX(), this.readY(), this.nameField.getText(), this.descriptField.getText());
	}

	@FXML
	public void modifyRoomBtnClicked() { //TODO
		if(this.selectedNode == null) return;

		this.updateSelectedRoom(this.readX(), this.readY(), this.nameField.getText(), this.descriptField.getText());
	}

	@FXML
	public void deleteRoomBtnClicked() {
		this.deleteSelectedNode();
	}

	/* **** Non-FXML functions **** */

	//Editor
	public void displayNodes(Collection<Node> nodes) {
		Set<Circle> nodeShapes = new HashSet<>();
		for (Node n : nodes) {
			nodeShapes.add(n.getShape());
		}
		this.topPane.getChildren().setAll(nodeShapes);

		// Does the same thing, but is hellish to read.
//		this.topPane.getChildren().setAll(this.directory.getNodes().stream().map(Node::getShape).collect(Collectors.toSet()));
	}

	/**
	 * Adds a listener to the choice box.
	 * Allows you to change floors
	 */
	public void initFloorChoiceBox(){
		this.populateFloorChoiceBox();
		this.floorChoiceBox.getSelectionModel().selectedIndexProperty().addListener(
				(observable, oldValue, newValue) -> {
					if(newValue.intValue() >= 0) changeFloor(newValue.intValue()+1);
				});
	}

	/**
	 * Initialize the floor's choice box with 1-7 (the floors)
	 * Ideally this shouldn't be hard coded
	 * TODO: Make this not hard coded into our program
	 */
	public void populateFloorChoiceBox() {
		// We are able to change what this list is of.
		this.floorChoiceBox.setItems(FXCollections.observableArrayList("Floor 1", "Floor 2", "Floor 3", "Floor 4", "Floor 5", "Floor 6", "Floor 7"));
		this.floorChoiceBox.setValue(this.floorChoiceBox.getItems().get(floor-1)); // default the selection to be whichever floor we start on
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
		node.getShape().setOnContextMenuRequested(e->{
			if(node.equals(this.selectedNode)) {
				ContextMenu optionsMenu = new ContextMenu();

				MenuItem exItem1 = new MenuItem("Connect to Node...");
				exItem1.setOnAction(e1 -> {
				});
				MenuItem exItem2 = new MenuItem("");
				exItem2.setOnAction(e2 -> {
				});
				optionsMenu.getItems().addAll(exItem1, exItem2);
				optionsMenu.show(node.getShape(), e.getScreenX(), e.getScreenY());
			}
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
	 * Also add a new node associated with the room.
	 */
	private void addNodeRoom(double x, double y, String name, String description) { //TODO
		// checking to see if x and y are negative or name field is empty. Changes text
		// next to each textField to red if it breaks the rules.
		// TODO: set fill back to black if the field if filled or follows the rules.
		if(x < 0 || y < 0 || name.isEmpty()) {
			if(x < 0){
				xPos.setTextFill(Color.RED);
			} else {
				xPos.setTextFill(Color.BLACK);
			} if(y < 0){
				yPos.setFill(Color.RED);
			} else {
				yPos.setFill(Color.BLACK);
			} if(name.isEmpty()){
				roomName.setFill(Color.RED);
			} else {
				roomName.setFill(Color.BLACK);
			}
			return;
		}
		xPos.setTextFill(Color.BLACK);
		yPos.setFill(Color.BLACK);
		roomName.setFill(Color.BLACK);

		if (this.selectedNode != null && this.selectedNode.getRoom() == null) {
			directory.addNewRoomToNode(this.selectedNode, name, description);
		} else {
			Node newNode = directory.addNewRoomNode(x, y, floor, name, description);
			this.addNodeListeners(newNode);
			this.displayNodes(directory.getNodesOnFloor(floor));
		}
	}

	/** Add a new node to the directory at the given coordinates */
	private void addNode(double x, double y) {
		if(x < 0 || y < 0) {
			return;
		}
		Node newNode = this.directory.addNewNode(x, y, floor);
		this.addNodeListeners(newNode);
	}

	private void  updateSelectedRoom(double x, double y, String name, String description) { //TODO
		this.selectedNode.applyToRoom(room -> {
			room.setName(name);
			room.setDescription(description);
			// Reset name
			// TODO: Don't rely on room shapes being a stacked rectangle and text
			((Text)room.getShape().getChildren().get(1)).setText(name);
		});
		this.updateSelectedNode(x, y);
		this.redrawLines(this.directory.getNodesOnFloor(floor));
		// TODO: Update the location of the node, whether or not it is a room (or not)
	}

	private void updateSelectedNode(double x, double y) { //TODO
		this.selectedNode.moveTo(x, y);
		this.selectedNode.getShape().setCenterX(this.selectedNode.getX());
		this.selectedNode.getShape().setCenterY(this.selectedNode.getY());
	}

	private void deleteSelectedNode() { // TODO: Separate this from a function that deletes both room and node
		if(this.selectedNode == null) return;

		this.directory.removeNodeAndRoom(this.selectedNode);
		this.selectedNode = null;

		this.redisplayAll();
	}

	/**
	 * Recreate and redisplay all lines on this floor
	 */
	public void redrawLines(Collection<Node> nodes) {
		Set<Line> lines = new HashSet<>();
		for (Node node : nodes) {
			for (Node neighbor : node.getNeighbors()) {
				if (node.getFloor() == neighbor.getFloor()) {
					lines.add(new Line(node.getX(), node.getY(), neighbor.getX(), neighbor.getY()));
				}
//				else if (EditorController.DEBUGGING) {
//					Line ln = new Line(node.getX(), node.getY(), neighbor.getX(), neighbor.getY());
//					ln.setStroke(Color.FUCHSIA);
//					lines.add(ln);
//				}
			}
		}
		this.botPane.getChildren().setAll(lines);
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

			this.deselectNode();

			this.displayNodes(this.directory.getNodesOnFloor(floor));
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
				potentialScaleX = (potentialScaleX < zoomMin ? zoomMin:potentialScaleX);
				potentialScaleY = (potentialScaleY < zoomMin ? zoomMin:potentialScaleY);
				potentialScaleX = (potentialScaleX > zoomMax ? zoomMax:potentialScaleX);
				potentialScaleY = (potentialScaleY > zoomMax ? zoomMax:potentialScaleY);
				contentAnchor.setScaleX(potentialScaleX);
				contentAnchor.setScaleY(potentialScaleY);
				// Update the slider
				zoomSlider.setValue(((potentialScaleX - zoomMin) / (zoomMax - zoomMin))*100);
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

			this.selectNode(n);
			this.updateFields();

		} else if(this.selectedNode != null && !this.selectedNode.equals(n) && this.secondaryPressed) {
			// ^ checks if there has been a node selected,
			// checks if the node selected is not the node we are clicking on
			// and checks if the button pressed is the right mouse button (secondary)

			// finally check if they are connected or not
			// if they are connected, remove the connection
			// if they are not connected, add a connection
			this.selectedNode.connectOrDisconnect(n);
			this.redrawLines(this.directory.getNodesOnFloor(floor));
		}
	}

	// This is going to allow us to drag a node!!!
	public void onShapeDrag(MouseEvent e, Node n) {
		this.beingDragged = true;
		if(this.selectedNode != null && this.selectedNode.equals(n)) {
			if(e.isPrimaryButtonDown()) {
				this.updateSelectedNode(e.getX(), e.getY());
				this.setFields(this.selectedNode.getX(), this.selectedNode.getY());
				this.redrawLines(this.directory.getNodesOnFloor(floor));
			} else if (this.secondaryPressed) {
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

	@FXML
	protected void increaseZoomButtonPressed() {
		double zoomPercent = (zoomSlider.getValue()/100);
		zoomPercent+=.2;
		zoomPercent = (zoomPercent > 1 ? 1 : zoomPercent);
		zoomSlider.setValue(zoomPercent*100);
		double zoomCoefficient = zoomMin*(1 - zoomPercent) + zoomMax*(zoomPercent);
		contentAnchor.setScaleX(zoomCoefficient);
		contentAnchor.setScaleY(zoomCoefficient);
	}

	@FXML
	protected void decreaseZoomButtonPressed() {
		double zoomPercent = (zoomSlider.getValue()/100);
		zoomPercent-=.2;
		zoomPercent = (zoomPercent < 0 ? 0 : zoomPercent);
		zoomSlider.setValue(zoomPercent*100);
		double zoomCoefficient = zoomMin*(1 - zoomPercent) + zoomMax*(zoomPercent);
		contentAnchor.setScaleX(zoomCoefficient);
		contentAnchor.setScaleY(zoomCoefficient);
	}

	private void selectNode (Node n){
		this.selectedNode = n;

		this.iconController.selectSingleNode(n);
		this.redisplayGraph();
	}

	private void deselectNode(){
		this.selectedNode = null;
		this.iconController.deselectAllNodes();
		this.redisplayGraph();
	}

	private void setXCoordField(double x) {
		this.xCoordField.setText(x+"");
	}

	private void setYCoordField(double y) {
		this.yCoordField.setText(y+"");
	}

	private void setFields(double x, double y) {
		this.setXCoordField(x);
		this.setYCoordField(y);
	}

	private void setNameField(String name) {
		this.nameField.setText(name);
	}

	private void setDescriptField(String desc) {
		this.descriptField.setText(desc);
	}

	private void setRoomFields(String name, String desc) {
		this.setNameField(name);
		this.setDescriptField(desc);
	}

	/** Updates the node/room fields based off of the selected node (or room)
	 *
	 */
	private void updateFields() {
		if(selectedNode == null) {
			return;
		}
		this.setFields(selectedNode.getX(), selectedNode.getY());

		//TODO: Use applyToRoom instead of checking containsRoom
		if(selectedNode.containsRoom()) {
			this.setRoomFields(selectedNode.getRoom().getName(), selectedNode.getRoom().getDescription());
		}
	}
}
