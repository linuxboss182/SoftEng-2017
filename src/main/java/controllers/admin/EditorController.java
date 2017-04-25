package controllers.admin;

import com.jfoenix.controls.JFXButton;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.*;

import main.ApplicationController;
import entities.Node;
import entities.Professional;
import entities.Room;
import controllers.filereader.FileParser;
import controllers.shared.MapDisplayController;
import main.algorithms.Pathfinder;
import main.algorithms.Algorithm;
import main.database.DatabaseWrapper;
import entities.FloorProxy;

public class EditorController
		extends MapDisplayController
		implements Initializable
{
	// TODO: Add the other buttons, and pull listeners out of the FXMLs
	@FXML private JFXButton addBtn;
	@FXML private Button logoutBtn;
	@FXML private TextField nameField;
	@FXML private TextArea descriptField;
	@FXML private TextField xCoordField;
	@FXML private TextField yCoordField;
	@FXML private Button confirmBtn;
	@FXML private Button addCustomProBtn;
	@FXML private Button deleteProfBtn;
	@FXML protected Pane linePane;
	@FXML protected Pane nodePane;
	@FXML public ComboBox<FloorProxy> floorComboBox;
	@FXML public TableView<Professional> roomProfTable;
	@FXML private TableColumn<Professional, String> roomCol;
	@FXML private TableColumn<Professional, String> profCol;
	@FXML private Text roomName;
	@FXML private Text yPos;
	@FXML private Label xPos;
	@FXML private ComboBox<Algorithm> algorithmComboBox;
	@FXML private Button helpBtn;
	@FXML private SplitPane mapSplitPane;

	/**
	 * Class implemented for use in multiple selection
	 *
	 * In addition to standard operatons, can test if only one element is present, and
	 * get the element if there is only one.
	 */
	private class SingularHashSet<E> extends HashSet<E>
	{
		public boolean isSingular() {
			return this.size() == 1;
		}
		public E getSoleElement() {
			Iterator<E> iterator = this.iterator();
			if (! iterator.hasNext()) {
				throw new NoSuchElementException("too few elements for SingularHashSet::getSoleElement");
			}
			E element = iterator.next();
			if (iterator.hasNext()) {
				throw new NoSuchElementException("too many elements for SingularHashSet::getSoleElement");
			} else {
				return element;
			}
		}
	}

	protected SingularHashSet<Node> selectedNodes = new SingularHashSet<>();
	protected double selectionStartX;
	protected double selectionStartY;
	protected double selectionEndX;
	protected double selectionEndY;

	protected boolean toggleShowRooms = false; // this is to enable/disable label editing


	private double clickedX, clickedY; //Where we clicked on the anchorPane

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		//Load
		directory = ApplicationController.getDirectory(); //Grab the database controller from main and use it to populate our directory
		iconController = ApplicationController.getIconController();

		this.changeFloor(this.directory.getFloor());

		this.imageViewMap.setPickOnBounds(true);
		if(floorComboBox != null) {
			initfloorComboBox();
		}

		// TODO: Set zoom based on window size
		zoomSlider.setValue(0);
		setZoomSliding();

		this.redisplayGraph(); // redraw nodes and edges
		this.iconController.resetAllNodes();

		//Lets us click through items
		this.imageViewMap.setPickOnBounds(true);
		this.contentAnchor.setPickOnBounds(false);
		this.nodePane.setPickOnBounds(false);

		this.installPaneListeners();
		this.setUpAlgorithmChoiceBox();

		// Add listeners to all nodes
		this.directory.getNodes().forEach(this::addNodeListeners);

		this.populateTableView();

		// TODO: Use control+plus/minus for zooming
		setHotkeys();
	}


	// TODO: rename descriptively
	public void populateTableView() {
		Collection<Professional> profs = directory.getProfessionals();

//		roomCol.setCellValueFactory(cdf -> new SimpleStringProperty(cdf.getValue().toString()));

		roomCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Professional, String>, ObservableValue<String>>() {
			@Override
			public ObservableValue<String> call(TableColumn.CellDataFeatures<Professional, String> cdf) {
				StringJoiner roomList = new StringJoiner(", ");
				for (Room r : cdf.getValue().getLocations()) {
					roomList.add(r.getName());
				}
				return new SimpleStringProperty(roomList.toString());
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
		if (! directory.roomsAreConnected()) {
			Alert warn = new Alert(Alert.AlertType.CONFIRMATION, "Not all rooms are connected: some paths will not exist.");
			// true if and only if the button pressed in the alert did not say "OK"
			if (! warn.showAndWait().map(result -> "OK".equals(result.getText())).orElse(false)) {
				return;
			}
		}

		try {
			Parent userUI = (BorderPane) FXMLLoader.load(this.getClass().getResource("/UserDestination.fxml"));
			this.linePane.getScene().setRoot(userUI);
		} catch(IOException e) {
			e.printStackTrace();
		}
	}

	@FXML
	public void addProfToRoom() {
		if (this.selectedProf == null || this.selectedNodes.isEmpty()) return;

		this.selectedNodes.forEach(n-> n.applyToRoom(room -> directory.addRoomToProfessional(room, this.selectedProf)));

		this.populateTableView();
	}

	@FXML
	public void delProfFromRoom() {
		if (this.selectedNodes.isEmpty() || this.selectedProf == null) return;

		this.selectedNodes.forEach(n-> n.applyToRoom(room -> directory.removeRoomFromProfessional(room, this.selectedProf)));

		this.populateTableView();
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
		this.populateTableView();
	}

	@FXML
	public void deleteProfBtnClicked () {
		this.directory.removeProfessional(this.selectedProf);
		this.populateTableView();
	}


	@FXML
	public void confirmBtnPressed() {
		DatabaseWrapper.getInstance().saveDirectory(this.directory);
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
		double x = this.readX();
		double y = this.readY();
		String name = this.nameField.getText();
		String description = this.descriptField.getText();

		// check to see if x and y are negative or name field is empty. Changes text
		// next to each textField to red if it breaks the rules.
		// This first condition requires that there has only been one node selected
		if(x < 0 || y < 0 || name.isEmpty()) {
			if(x < 0){
				xPos.setTextFill(Color.RED);
			} else {
				xPos.setTextFill(Color.BLACK);
			} if(y < 0){
				yPos.setFill(Color.RED);
			} else {
				yPos.setFill(Color.BLACK);
			} if(name.isEmpty()) {
				roomName.setFill(Color.RED);
			} else {
				roomName.setFill(Color.BLACK);
			}
			return;
		}
		xPos.setTextFill(Color.BLACK);
		yPos.setFill(Color.BLACK);
		roomName.setFill(Color.BLACK);

		if (this.selectedNodes.isSingular() && (this.selectedNodes.getSoleElement().getRoom() == null)) {
			directory.addNewRoomToNode(this.selectedNodes.getSoleElement(), name, description);
		} else {
			this.addNodeRoom(x, y, name, description);
		}
		this.redisplayAll();
	}

	@FXML
	public void modifyRoomBtnClicked() {
		if(! this.selectedNodes.isSingular()) return;

		this.updateSelectedRoom(this.readX(), this.readY(), this.nameField.getText(), this.descriptField.getText());
	}

	@FXML
	public void deleteRoomBtnClicked() {
		this.deleteSelectedNodes();
	}


	/* **** Non-FXML functions **** */

	@Override
	protected void redisplayMapItems() {
		this.redisplayGraph();
	}

	/**
	 * Redraw all elements of the map and the professionals' elements
	 */
	private void redisplayAll() {
		this.redisplayGraph(); // nodes on this floor and lines between them
		this.populateTableView();
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
		this.displayNodes(directory.getNodesOnFloor(this.directory.getFloor()));
		this.redrawLines();
//		}
	}

	/**
	 * This function is currently for testing purposes only
	 * Using it will modifier listeners that the user can access; be careful
	 */
	public void displayRoomsOnFloor() {
		Set<javafx.scene.Node> roomShapes = new HashSet<>();
		for (Room r : directory.getRoomsOnFloor(floor)) {
			roomShapes.add(r.getUserSideShape());
			r.getUserSideShape().setOnMouseClicked(event -> {});
			r.getUserSideShape().setOnContextMenuRequested(event -> {});
			Label label = r.getUserSideShape().getLabel();
			label.setOnMouseDragged(event -> {
				event.consume();
				label.relocate(event.getX(), event.getY());
			});
		}
		this.nodePane.getChildren().setAll(roomShapes);
	}

	//Editor
	// TODO: Automatically use the one floor instead of passing in a collection
	public void displayNodes(Collection<Node> nodes) {
		Set<Circle> nodeShapes = new HashSet<>();
		for (Node n : nodes) {
			nodeShapes.add(n.getShape());
		}
		this.nodePane.getChildren().setAll(nodeShapes);

		// Does the same thing, but is hellish to read.
//		this.nodePane.getChildren().setAll(this.directory.getNodes().stream().map(Node::getUserSideShape).collect(Collectors.toSet()));
	}

	/**
	 * Recreate and redisplay all lines on this floor
	 */
	public void redrawLines() {
		Set<Line> lines = new HashSet<>();
		for (Node node : directory.getNodesOnFloor(directory.getFloor())) {
			for (Node neighbor : node.getNeighbors()) {
				if ((node.getFloor() == neighbor.getFloor()) &&
						node.getBuildingName().equalsIgnoreCase(neighbor.getBuildingName())) {
					lines.add(new Line(node.getX(), node.getY(), neighbor.getX(), neighbor.getY()));
				}
//				else if (EditorController.DEBUGGING) {
//					Line ln = new Line(node.getX(), node.getY(), neighbor.getX(), neighbor.getY());
//					ln.setStroke(Color.FUCHSIA);
//					lines.add(ln);
//				}
			}
		}
		this.linePane.getChildren().setAll(lines);
	}

	/**
	 * Adds a listener to the choice box.
	 * Allows you to change floors
	 */
	public void initfloorComboBox() {
		this.floorComboBox.setItems(FXCollections.observableArrayList(FloorProxy.getFloors()));
		this.floorComboBox.getSelectionModel().selectedItemProperty().addListener(
				(ignored, ignoredOld, choice) -> this.changeFloor(choice));

		this.floorComboBox.setValue(this.floorComboBox.getItems().get(this.directory.getFloorNum() - 1)); // default the selection to be whichever floor we start on
	}

	/**
	 * Add listeners to the given node
	 *
	 * @note No other function should add the base listeners to nodes.
	 */
	private void addNodeListeners(Node node) {
		node.getShape().setOnMouseClicked(event -> this.clickNodeListener(event, node));
		node.getShape().setOnMouseDragged(event -> this.dragNodeListener(event, node));
		node.getShape().setOnMouseReleased(event -> this.releaseNodeListener(event, node));
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
	private void addNodeRoom(double x, double y, String name, String description) {
		// TODO: Review this assumption
		Node newNode = directory.addNewRoomNode(x, y, directory.getFloor(), name, description);
		this.addNodeListeners(newNode);
		this.redisplayGraph();
		this.selectedNodes.forEach(n -> {
			this.directory.connectOrDisconnectNodes(n, newNode);
		});
	}

	/** Add a new node to the directory at the given coordinates */
	private Node addNode(double x, double y) {
		if(x < 0 || y < 0) {
			return null;
		}
		Node newNode = this.directory.addNewNode(x, y, this.directory.getFloor());
		this.addNodeListeners(newNode);
		return newNode;
	}

	/**
	 * This should only be called by the modify room button
	 *
	 * it runs with the assumption that it has been guaranteed a room is in the selected Nodes list at index 0
	 *
	 * DO NOT USE IT IF YOU HAVE NOT SATISFIED THIS REQUIREMENT
	 */
	private void updateSelectedRoom(double x, double y, String name, String description) {
		this.selectedNodes.getSoleElement().applyToRoom(room -> {
			directory.updateRoom(room, name, description);
			// TODO: Handle this in updateRoom or a method called there (VERY BAD)
			((Label)room.getUserSideShape().getChildren().get(1)).setText(name);
		});
		this.updateSelectedNode(x, y);
		this.redrawLines();
		// TODO: Update the location of the node, whether or not it is a room (or not)
		// That should just require a Node.moveTo (but make sure that's not being made private first)
	}

	/**
	 * This should only be called in theory by the method called by the modify room button.
	 *
	 * It runs with the assumption that it has been guaranteed that a node is in the selectedNodes list at index 0
	 *
	 * DO NOT USE IT IF YOU HAVE NOT SATISFIED THIS REQUIREMENT
	 */
	private void updateSelectedNode(double x, double y) {
		this.selectedNodes.getSoleElement().moveTo(x, y);
		this.selectedNodes.getSoleElement().getShape().setCenterX(x);
		this.selectedNodes.getSoleElement().getShape().setCenterY(y);
	}

	private void updateSelectedNodes(double x, double y) {
		this.selectedNodes.forEach(n -> {
			double newX = n.getX() - this.clickedX + x;
			double newY = n.getY() - this.clickedY + y;
			n.moveTo(newX, newY);
			n.getShape().setCenterX(newX);
			n.getShape().setCenterY(newY);
		});
		this.clickedX = x;
		this.clickedY = y;
	}

	/**
	 * Deletes the node from EVERYTHING
	 * @param n
	 */
	private void deleteNode(Node n) {
		this.directory.removeNodeAndRoom(n);
	}

	/**
	 * Delete all of the selected Nodes
	 */
	private void deleteSelectedNodes() {
		this.selectedNodes.forEach(this::deleteNode);
		this.selectedNodes.clear();
		this.redisplayAll();
	}

	/**
	 * Deletes the nodes in the selection pool that are out of bounds (less than 0 x and y)
	 */
	private void deleteOutOfBoundNodes() {
		Set<Node> deleted = new HashSet<>();
		this.selectedNodes.forEach(node -> {
			if ((node.getX() < 0) || (node.getY() < 0)) {
				this.deleteNode(node);
				deleted.add(node);
			}
		});
		this.selectedNodes.removeAll(deleted);
		this.redisplayAll();
	}


	///////////////////////
	/////EVENT HANDLERS////
	///////////////////////

	public void installPaneListeners() {
		linePane.setOnMouseClicked(e -> {
			e.consume();
			this.setFields(e.getX(), e.getY());

			//Create node on double click
			if(e.getClickCount() == 2) {
				Node newNode = this.addNode(e.getX(), e.getY());
				if (newNode == null) {
					return;
				}
				if (e.isShiftDown()) {
					// shift double click:
					// if one node is selected, connect to new, then select only new
					// if multiple are selected, connect to all and add new to selection
					if (this.selectedNodes.isSingular()) {
						this.directory.connectNodes(newNode, this.selectedNodes.getSoleElement());
						this.selectSingleNode(newNode); // chain add if one is selected
					} else {
						this.selectedNodes.forEach(n -> this.directory.connectNodes(n, newNode));
						this.selectNode(newNode);
					}
				} else {
					this.selectSingleNode(newNode);
				}
			} else if (! e.isShiftDown()) {
				this.deselectNodes();
			}
			this.redisplayGraph();
		});

		// TODO: Move to MapDisplayController
		setScrollZoom();

		contentAnchor.setOnMousePressed(e -> {
			e.consume();
			clickedX = e.getX();
			clickedY = e.getY();
			if(e.isShiftDown()) {
				this.selectionStartX = e.getX();
				this.selectionStartY = e.getY();
			}
		});

		contentAnchor.setOnMouseDragged(e-> {
			e.consume();
			if(e.isShiftDown()) {
				Rectangle r = new Rectangle();
				if(e.getX() > selectionStartX) {
					r.setX(selectionStartX);
					r.setWidth(e.getX() - selectionStartX);
				} else {
					r.setX(e.getX());
					r.setWidth(selectionStartX - e.getX());
				}
				if(e.getY() > selectionStartY) {
					r.setY(selectionStartY);
					r.setHeight(e.getY() - selectionStartY);
				} else {
					r.setY(e.getY());
					r.setHeight(selectionStartY - e.getY());
				}
				r.setFill(Color.SKYBLUE);
				r.setStroke(Color.BLUE);
				r.setOpacity(0.5);
				this.redisplayAll();
				this.linePane.getChildren().add(r);
			} else if(! this.toggleShowRooms) {
				// Limits the dragging for x and y coordinates. (panning I mean)
				if (e.getSceneX() >= mapSplitPane.localToScene(mapSplitPane.getBoundsInLocal()).getMinX() && e.getSceneX() <=  mapScroll.localToScene(mapScroll.getBoundsInLocal()).getMaxX()) {
					contentAnchor.setTranslateX(contentAnchor.getTranslateX() + e.getX() - clickedX);
				}
				if(e.getSceneY() >= mapSplitPane.localToScene(mapSplitPane.getBoundsInLocal()).getMinY() && e.getSceneY() <=  mapScroll.localToScene(mapScroll.getBoundsInLocal()).getMaxY()) {
					contentAnchor.setTranslateY(contentAnchor.getTranslateY() + e.getY() - clickedY);
				}
			}
		});

		contentAnchor.setOnMouseReleased(e->{
			e.consume();
			if (e.isShiftDown()) { // this is so that you are allowed to release shift after pressing it at the start of the drag
				this.selectionEndX = e.getX();
				this.selectionEndY = e.getY();
				this.redisplayAll(); // this is to clear the rectangle off of the pane

				// These are the bounds of the selection
				double topLeftX;
				double topLeftY;
				double botRightX;
				double botRightY;
				if(this.selectionStartX < this.selectionEndX) {
					topLeftX = this.selectionStartX;
					botRightX = this.selectionEndX;
				} else {
					topLeftX = this.selectionEndX;
					botRightX = this.selectionStartX;
				}
				if(this.selectionStartY < this.selectionEndY) {
					topLeftY = this.selectionStartY;
					botRightY = this.selectionEndY;
				} else {
					topLeftY = this.selectionEndY;
					botRightY = this.selectionStartY;
				}
				// Loop through and select/deselect all nodes in the bounds
				this.directory.getNodesOnFloor(this.directory.getFloor()).forEach(n -> {
					if(n.getX() > topLeftX && n.getX() < botRightX && n.getY() > topLeftY && n.getY() < botRightY) {
						this.selectNode(n);
					}
				});
			}
			if(this.toggleShowRooms) {
				this.displayAdminSideRooms();
			}
			this.redisplayGraph();
		});
	}

	public void clickNodeListener(MouseEvent e, Node node) {
		e.consume();

		// update text fields
		this.setFields(node.getX(), node.getY());

		// single left click without drag to select nodes
		if((e.getClickCount() == 1) && (e.getButton() == MouseButton.PRIMARY) && e.isStillSincePress()) {
			this.setFields(node.getX(), node.getY());
			node.applyToRoom(room -> this.setRoomFields(room.getName(), room.getDescription()));
			if (! e.isShiftDown()) {
				this.deselectNodes(); // no-shift click will deselect all others
			}
			// control click to select neighbors instead of target node
			if (e.isControlDown()) {
				node.getNeighbors().forEach(this::selectNode);
			}

			this.selectOrDeselectNode(node);
			this.redisplayGraph();

		} else if (!this.selectedNodes.isEmpty() && (e.getButton() == MouseButton.SECONDARY)) {
			// Connect all of the nodes selected to the one that you have clicked on
			this.selectedNodes.forEach(n -> this.directory.connectOrDisconnectNodes(node, n));
			this.redrawLines();
		}
	}

	// This is going to allow us to drag a node!!!
	public void dragNodeListener(MouseEvent e, Node n) {
		e.consume();
		if (this.selectedNodes.contains(n)) {
			if (e.isPrimaryButtonDown()) {
				this.updateSelectedNodes(e.getX(), e.getY());
				this.setFields(n.getX(), n.getY());
				this.redrawLines();

			} else if (e.getButton() == MouseButton.SECONDARY) {
				// right click drag on the selected node
				// do nothing for now
			}
			this.redisplayGraph();
		}
	}

	public void releaseNodeListener(MouseEvent e, Node n) {
		e.consume();
		this.releasedX = e.getX();
		this.releasedY = e.getY();

		// Delete any nodes that were dragged out of bounds
		this.deleteOutOfBoundNodes();
	}

	/**
	 * Adds or removes the node from the selection pool
	 * @param n - The Node being selected or deselected
	 */
	private void selectOrDeselectNode(Node n) {
		if(this.selectedNodes.contains(n)) {
			this.deselectNode(n);
		} else {
			this.selectNode(n);
		}
	}

	private void selectAllNodesOnFloor() {
		this.directory.getNodesOnFloor(floor).forEach(node -> {
			if (!this.selectedNodes.contains(node)) {
				this.selectedNodes.add(node);
				this.iconController.selectAnotherNode(node);
			}
		});
	}

	private void selectSingleNode(Node n) {
		this.selectedNodes.clear();
		this.selectedNodes.add(n);
		this.iconController.selectSingleNode(n);
	}

	private void selectNode(Node n) {
		this.selectedNodes.add(n);
		this.iconController.selectAnotherNode(n);
	}

	private void deselectNode(Node n) {
		this.selectedNodes.remove(n);
		this.iconController.resetSingleNode(n);
	}

	private void deselectNodes() {
		this.iconController.deselectAllNodes();
		this.selectedNodes.clear();
	}

	// This method is commented out because it is outdated and was only used when there was singular node selection
	// In the current implementation it is not needed
//	private void deselectNode(){
//		this.selectedNode = null;
//		this.iconController.deselectAllNodes();
//		this.redisplayGraph();
//	}

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

	/**
	 * Upload professonals from a file
	 */
	@FXML
	private void loadProfessionalsFile() {
		Alert ask = new Alert(Alert.AlertType.CONFIRMATION, "If the selected file "
				+ "contains people who are already in the application, they will be duplicated.");

		// true if and only if the button pressed in the alert said "OK"
		if (ask.showAndWait().map(result -> "OK".equals(result.getText())).orElse(false)) {
			FileChooser fc = new FileChooser();
			File f = fc.showOpenDialog(this.contentAnchor.getScene().getWindow());
			if (f != null) {
				try {
					FileParser.parseProfessionals(f, directory);
				} catch (FileNotFoundException e) {
					Alert a = new Alert(Alert.AlertType.ERROR, "Unable to read file");
					a.showAndWait();
					return;
				}
				this.populateTableView();
			}
		}
	}

	/**
	 * Get a choice box that sets the active algorithm
	 */
	private void setUpAlgorithmChoiceBox() {
		this.algorithmComboBox.setItems(FXCollections.observableArrayList(Pathfinder.getAlgorithmList()));
		this.algorithmComboBox.getSelectionModel().selectedItemProperty().addListener(
				(ignored, ignoredOld, choice) -> Pathfinder.setStrategy(choice));
		// this.algorithmComboBox.setConverter(Algorithm.ALGORITHM_STRING_CONVERTER);
		this.algorithmComboBox.getSelectionModel().select(Pathfinder.getStrategy());
	}

	/*
	To set the kiosk, bind this line to a "set kiosk" button:
	if (selectedNode != null) selectedNode.applyToRoom(room -> directory.setKiosk(room));
	 */

	@FXML
	public void setToggleShowRooms() {
		this.toggleShowRooms = !toggleShowRooms;
		if(toggleShowRooms) {
			// for now, disable dragging
			this.imageViewMap.setDisable(true);
			this.linePane.setDisable(true);
			this.linePane.getChildren().clear();
			this.nodePane.getChildren().clear();
			this.displayAdminSideRooms();

		} else {
			// re-enable dragging
			this.imageViewMap.setDisable(false);
			this.linePane.setDisable(false);
			this.redisplayAll();
		}
	}

	/**
	 * Show the rooms with editable labels to the admin
	 */
	public void displayAdminSideRooms() {
		Set<javafx.scene.Node> roomShapes = new HashSet<>();
		for (Room room : directory.getRoomsOnFloor(floor)) {
			roomShapes.add(room.getAdminSideShape());
			/* This is code to make a context menu appear when you right click on the shape for a room
			 * setonContextMenuRequested pretty much checks the right click- meaning right clicking is how you request a context menu
			 * that is reallllllllly helpful for a lot of stuff
			 */
		}
		this.nodePane.getChildren().setAll(roomShapes);
	}

	/**
	 * Make the room of the currently-selected node into the kiosk
	 */
	@FXML
	public void selectKioskClicked() {
		if (selectedNodes.isSingular()) selectedNodes.getSoleElement().applyToRoom(room -> directory.setKiosk(room));
	}
	@FXML
	private void helpBtnClicked() throws IOException {
		AdminHelpController helpController = new AdminHelpController();
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(this.getClass().getResource("/AdminHelp.fxml"));
		Scene helpScene = new Scene(loader.load());
		Stage helpStage = new Stage();
		helpStage.initOwner(contentAnchor.getScene().getWindow());
		helpStage.setScene(helpScene);
		helpStage.showAndWait();
	}
}
