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

	final double SCALE_DELTA = 1.1; //The rate to scale
	private double clickedX, clickedY; //Where we clicked on the anchorPane
	private boolean beingDragged; //Protects the imageView for being dragged

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		this.setPanes(linePane, nodePane); //Set the panes

		this.directory = ApplicationController.getDirectory(); //Grab the database controller from main and use it to populate our directory

		//TODO: get this from the directory
		this.map4 = new Image("/4_thefourthfloor.png");
		this.imageViewMap.setImage(this.map4);

		this.displayNodes(); //draws the nodes from the directory
		this.redrawLines();  //deletes all the lines then draws them again from the directory

		//Lets us click through items
		this.imageViewMap.setPickOnBounds(true);
		this.contentAnchor.setPickOnBounds(false);
		this.topPane.setPickOnBounds(false);

	}

	@FXML
	private void logoutBtnClicked() {
		try {
			Parent userUI = (BorderPane) FXMLLoader.load(this.getClass().getResource("/FinalUI.fxml"));
			this.botPane.getScene().setRoot(userUI);
		} catch(IOException e) {
			e.printStackTrace();
		}
	}

	@FXML
	public void addProfToRoom() {
		// TODO: Change use of instanceof to good coding standards
//		if (this.selectedProf == null || this.selectedNode == null || !(this.selectedNode instanceof Room)) {
//			return;
//		} else {
//			this.selectedProf.addLocation((Room)this.selectedNode);
//			this.roomList = "";
//			for (Room r: this.selectedProf.getLocations())
//				this.roomList += r.getName() + ", ";
//			this.roomTextLbl.setText(this.roomList);
//		}
	}

	@FXML
	public void delProfFromRoom() {
//		if (this.selectedNode == null) {
//			return;
//		} else {
//			this.selectedProf.getLocations().forEach(room -> {
//				if(room.equals(this.selectedNode)) {
//					this.selectedProf.removeLocation(room);
//				}
//			});
//
//			this.roomList = "";
//			for (Room r: this.selectedProf.getLocations())
//				this.roomList += r.getName() + ", ";
//			this.roomTextLbl.setText(this.roomList);
//		}
	}

	@FXML
	public void refreshBtnClicked() {
		//TODO
//		this.populateChoiceBox();
//
//		for (Professional pro: this.directory.getProfessionals()) {
//			this.proList.add(pro);
//
//		}
	}

	@FXML
	public void addCustomProBtnPressed() throws IOException {
		//TODO
//		FXMLLoader loader = new FXMLLoader();
//		loader.setLocation(this.getClass().getResource("/AddProUI.fxml"));
//		this.addProController = loader.getController();
//		//this.addProController.setEditorController(this);
//		Scene addProScene = new Scene(loader.load());
//		Stage addProStage = new Stage();
//		addProStage.setScene(addProScene);
//
//		addProStage.showAndWait();
	}

	@FXML
	public void deleteProfBtnClicked () {
		//TODO
//		this.directory.removeProfessional(this.selectedProf);
//	//	this.refreshBtnClicked();
	}


	@FXML
	public void confirmBtnPressed() {
		//TODO
//		this.directory.getRooms().forEach(room -> {
//			System.out.println("Attempting to save room: " + room.getName() + " to database...");
//		});
//
//		try {
//
//
//			ApplicationController.dbc.destructiveSaveDirectory(this.directory);
//		} catch (DatabaseException e) {
//			System.err.println("\n\nDATABASE DAMAGED\n\n");
//			e.printStackTrace();
//			System.err.println("\n\nDATABASE DAMAGED\n\n");
//		}
	}

	@FXML
	public void addRoomBtnClicked() {
		//TODO
//		this.addRoom(this.readX(), this.readY(), this.nameField.getText(), this.descriptField.getText());
	}

	@FXML
	public void modifyRoomBtnClicked() {
		if(this.selectedNode == null) return;
		//TODO: Change use of instanceof to good coding standards
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


	public void selectChoiceBox(){
		//TODO
//		this.proChoiceBox.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
//			@Override
//			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
//				//proTextLbl.setText(proList.get(newValue.intValue() - 1).toString());
//
//				if(proList.size() != 0 && newValue.intValue() >= 0) {
//					EditorController.this.selectedProf = proList.get(newValue.intValue());
//				}
//
//				// Build a string listing the names of the professional's rooms
//				StringJoiner roomList = new StringJoiner(", ");
//				selectedProf.getLocations().forEach(room -> roomList.add(room.getName()));
//
//				roomTextLbl.setText(roomList.toString());
//			}
//		});
	}

	public void populateChoiceBox() {
		this.proChoiceBox.setItems(FXCollections.observableArrayList(this.directory.getProfessionals()));
	}


	private void addRoom(double x, double y, String name, String description) { //TODO
		//		Room newRoom = new Room(x-this.RECTANGLE_WIDTH/2, y-this.RECTANGLE_HEIGHT/2, name, description);
		//		this.directory.addRoom(newRoom);
		//		this.paintRoomOnLocation(newRoom);
	}

	private double readX() {
		return Double.parseDouble(this.xCoordField.getText());
	}

	private double readY() {
		return Double.parseDouble(this.yCoordField.getText());
	}

	private void addNode(double x, double y) {
		Node newNode = this.directory.newNode(x,y);
		this.paintNode(newNode);
	}

	private void updateSelectedNode(double x, double y) { //TODO
//		this.selectedNode.moveTo(x, y);
//
//		Circle selectedCircle = (Circle) this.selectedShape;
//		selectedCircle.setCenterX(x);
//		selectedCircle.setCenterY(y);
	}

	private void updateSelectedRoom(double x, double y, String name, String description) { //TODO
//		this.selectedNode.moveTo(x, y);
//		((Room) this.selectedNode).setName(name);
//		((Room) this.selectedNode).setDescription(description);
//		Rectangle selectedRectangle = (Rectangle) this.selectedShape;
//		selectedRectangle.setX(x);
//		selectedRectangle.setY(y);
	}

	private void deleteSelectedNode() { //TODO
//		if(this.selectedNode == null) return;
//
//
//		this.selectedNode.disconnectAll();
//		this.directory.removeNodeOrRoom(this.selectedNode);
//		this.selectedNode = null;
//		// now garbage collector has to do its work
//
//		this.contentPane.getChildren().remove(this.selectedShape);
//		this.selectedShape = null;
//
//		this.redrawLines();

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

}
