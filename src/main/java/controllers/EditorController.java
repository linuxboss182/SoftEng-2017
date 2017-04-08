package controllers;


import entities.Directory;
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
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import entities.Node;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.stage.Stage;
import main.ApplicationController;
import main.DatabaseException;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.StringJoiner;

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

	private AddProfessionalController addProController;


	final double SCALE_DELTA = 1.1;
	private double clickedX, clickedY;
	private boolean beingDragged;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		//Set the panes
		this.setPanes(linePane, nodePane);
		//Grab the database controller from main and use it to populate our directory
		this.directory = ApplicationController.getDirectory();

		//Add map
		this.map4 = new Image("/4_thefourthfloor.png");
		this.imageViewMap.setImage(this.map4);
		this.displayNodes(); // draws the nodes from the directory
		this.displayRooms();
		this.imageViewMap.setPickOnBounds(true);

		this.redrawLines();

		this.contentAnchor.setPickOnBounds(false);
		this.topPane.setPickOnBounds(false);
//		this.botPane.setPickOnBounds(false);
//		this.imageViewMap.setPickOnBounds(false);
		this.botPane.setOnMouseClicked(e -> {

			this.setFields(e.getX(), e.getY());

			//Create node on double click
			if(e.getClickCount() == 2) {
				this.addNode(e.getX(), e.getY());
			}

			if(this.selectedShape != null) {

				if(this.selectedNode instanceof Room) {
					if(((Room) this.selectedNode).getName().equalsIgnoreCase(this.KIOSK_NAME)) {
						this.selectedShape.setFill(this.KIOSK_COLOR);
					} else {
						this.selectedShape.setFill(this.DEFAULT_SHAPE_COLOR);
					}



				} else {
					this.selectedShape.setFill(this.DEFAULT_SHAPE_COLOR);
				}
			}

			this.selectedNode = null;
			this.selectedShape = null;
		});

		//populate box for professionals
		this.populateChoiceBox();
		this.proList = new ArrayList<>();
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
				contentAnchor.setScaleX(contentAnchor.getScaleX() * scaleFactor);
				contentAnchor.setScaleY(contentAnchor.getScaleY() * scaleFactor);
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

	@Override
	public void addRoomListener(Room r){
		r.getShape().setOnMouseClicked((MouseEvent e) ->{
			EditorController.this.onShapeClick(e, r);
			this.nameField.setText(r.getName());
			this.descriptField.setText((r.getDescription()));
		});

		r.getShape().setOnMouseDragged(e->{
			beingDragged = true;
			EditorController.this.onRectangleDrag(e, r);
		});

		// Working as intended
		r.getShape().setOnMousePressed(e->{
			this.primaryPressed = e.isPrimaryButtonDown();
			this.secondaryPressed = e.isSecondaryButtonDown();
		});


		r.getShape().setOnMouseReleased(e->{
			beingDragged = false;
			EditorController.this.onShapeReleased(e, r);
		});
	}

	public void selectChoiceBox() {

		this.proChoiceBox.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>()
		{
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

	@FXML
	public void addProfToRoom() {
		// TODO: Change use of instanceof to good coding standards
		if (this.selectedProf == null || this.selectedNode == null || !(this.selectedNode instanceof Room)) {
			return;
		} else {
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
			this.selectedProf.getLocations().forEach(room -> {
				if(room.equals(this.selectedNode)) {
					this.selectedProf.removeLocation(room);
				}
			});

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
		Scene addProScene = new Scene(loader.load());
		Stage addProStage = new Stage();
		addProStage.setScene(addProScene);

		addProStage.showAndWait();
	}

	@FXML
	public void deleteProfBtnClicked () {
		this.directory.removeProfessional(this.selectedProf);
		this.refreshBtnClicked();
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
	public void modifyRoomBtnClicked() {
		if(this.selectedNode == null) return;
		// TODO: Change use of instanceof to good coding standards
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
		circ.setStroke(this.DEFAULT_STROKE_COLOR);
		circ.setStrokeWidth(this.DEFAULT_STROKE_WIDTH);
		this.topPane.getChildren().add(circ);
		circ.setVisible(true);

		circ.setOnMouseClicked((MouseEvent e) ->{
			EditorController.this.onShapeClick(e, n);
		});

		circ.setOnMouseDragged(e->{
			beingDragged = true;
			EditorController.this.onCircleDrag(e, n);
		});

		// Working as intended
		circ.setOnMousePressed(e->{
			this.primaryPressed = e.isPrimaryButtonDown();
			this.secondaryPressed = e.isSecondaryButtonDown();
		});


		circ.setOnMouseReleased(e->{
			EditorController.this.onShapeReleased(e, n);
			beingDragged = false;
		});
	}



	public void redrawLines() {
		this.botPane.getChildren().clear();
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
				line.setFill(this.CONNECTION_LINE_COLOR);
				// TODO: Change use of instanceof to good coding standards
				if(connected instanceof Room) {
					line.setEndX(endX + this.RECTANGLE_WIDTH/2);
					line.setEndY(endY + this.RECTANGLE_HEIGHT/2);
				} else {
					line.setEndX(endX);
					line.setEndY(endY);
				}


				this.lines.add(line);

				this.botPane.getChildren().add(line);
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
				// TODO: Change use of instanceof to good coding standards
				if(connected instanceof Room) {
					line.setEndX(endX + this.RECTANGLE_WIDTH/2);
					line.setEndY(endY + this.RECTANGLE_HEIGHT/2);
				} else {
					line.setEndX(endX);
					line.setEndY(endY);
				}

				this.lines.add(line);

				this.botPane.getChildren().add(line);
				line.setVisible(true);
			}
		});
	}

	public void displayNodes() {
		this.directory.getNodes().forEach(node -> this.paintNodeOnLocation(node));
	}




	@FXML
	private void logoutBtnClicked() {
		//System.out.println();
		//System.out.println("addRoomBtn = " + addRoomBtn);
		try {
			Parent userUI = (BorderPane) FXMLLoader.load(this.getClass().getResource("/FinalUI.fxml"));
			this.botPane.getScene().setRoot(userUI);

		} catch(IOException e) {
			e.printStackTrace();
		}
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
		if(e.getClickCount() == 1 && this.primaryPressed) {
			if(this.selectedShape != null) {
				// TODO: Change use of instanceof to good coding standards
				if(this.selectedNode instanceof Room) {
					if(((Room) this.selectedNode).getName().equalsIgnoreCase(this.KIOSK_NAME)) {
						this.selectedShape.setFill(this.KIOSK_COLOR);
					} else {
						this.selectedShape.setFill(DEFAULT_SHAPE_COLOR);
					}

				} else {
					this.selectedShape.setFill(this.DEFAULT_SHAPE_COLOR);
				}
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
	public void onCircleDrag(MouseEvent e, Node n) {
		beingDragged = true;
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
