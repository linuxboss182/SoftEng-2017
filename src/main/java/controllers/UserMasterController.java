package controllers;

import entities.Node;
import entities.Room;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import main.ApplicationController;
import main.DirectionsGenerator;
import main.Pathfinder;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;


public abstract class UserMasterController extends MapDisplayController
{
	@FXML
	public Button logAsAdmin;
	@FXML
	public ImageView imageViewMap;
	@FXML
	public AnchorPane contentAnchor = new AnchorPane();
	@FXML
	public ListView<Room> directoryView;
	@FXML
	public Button changeStartBtn;
	@FXML
	public Button getDirectionsBtn;
	@FXML
	public Pane linePane;
	@FXML
	public TextField searchBar;
	@FXML
	public Pane nodePane;
	@FXML
	public TextFlow directionsTextField;
	@FXML
	public GridPane sideGridPane;
	@FXML
	public ChoiceBox floorChoiceBox;

	final double SCALE_DELTA = 1.1;
	private double clickedX, clickedY;
	protected static Room startRoom;
	protected static Room endRoom;

	public void initialize() {
		//Set the panes
		this.setPanes(linePane, nodePane);
		//Grab the database controller from main and use it to populate our directory
		this.directory = ApplicationController.getDirectory();

		//Add map
		//this.map = new Image("/4_thefourthfloor.png");
		// use floor proxy class to load in map
		this.map = new FloorProxy(4).display();
		this.imageViewMap.setImage(this.map);
		this.imageViewMap.setPickOnBounds(true);

		if(floorChoiceBox != null)
			initFloorChoiceBox();
		this.kiosk = null;
		for (Room r : this.directory.getRooms()) {
			if (r.getName().equalsIgnoreCase("YOU ARE HERE")) {
				this.kiosk = r;
			}
		}
		this.displayRooms(directory.getRoomsOnFloor(floor));
		if(this.directoryView != null) {
			this.populateListView();
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
				contentAnchor.setTranslateX(contentAnchor.getTranslateX() + event.getX() - clickedX);
				contentAnchor.setTranslateY(contentAnchor.getTranslateY() + event.getY() - clickedY);
				event.consume();
			}
		});


	}

	/**
	 * Adds a listener to the choice box.
	 * Allows you to change floors
	 */
	public void initFloorChoiceBox(){
		this.populateFloorChoiceBox();
		this.floorChoiceBox.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				if(newValue.intValue() >= 0) {
					changeFloor(newValue.intValue()+1);
				}

			}
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

	@FXML
	public void logAsAdminClicked()
			throws IOException, InvocationTargetException {
		// TODO: Review
		Parent loginPrompt = (AnchorPane) FXMLLoader.load(this.getClass().getResource("/LoginPrompt.fxml"));
		this.contentAnchor.getScene().setRoot(loginPrompt);


	}

	public void displayRooms(Collection<Room> rooms) {
		Set<javafx.scene.Node> roomShapes = new HashSet<>();
		for (Room r : rooms) {
			roomShapes.add(r.getShape());
		}
		this.topPane.getChildren().setAll(roomShapes);
	}

	public void populateListView() {
		this.directoryView.itemsProperty().bind(this.listProperty);
		this.listProperty.set(FXCollections.observableArrayList(this.directory.getRooms()));

		this.directoryView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Room>() {
			@Override
			public void changed(ObservableValue<? extends Room> observable, Room oldValue, Room newValue) {
				List<Node> ret;
				if(kiosk != null) {
					ret = Pathfinder.findPath(kiosk.getLocation(), newValue.getLocation());
					paintPath(new ArrayList<>(ret));
				} else {

				}
			}
		});
		ArrayList<Room> tempRooms = new ArrayList<>(this.directory.getRooms());
		this.directoryView.setOnMousePressed(e->{
			switch (e.getButton()) {
				case PRIMARY:
					endRoom = directoryView.getSelectionModel().getSelectedItem();
					System.out.println(endRoom);
					break;
				case SECONDARY:
					startRoom = directoryView.getSelectionModel().getSelectedItem();
					System.out.println(startRoom);
					break;
				default:
					break;
			}
		});
		}

	@FXML
	public void getDirectionsClicked() throws IOException, InvocationTargetException {
		Parent userPath = (BorderPane) FXMLLoader.load(this.getClass().getResource("/UserPath.fxml"));
		this.imageViewMap.getScene().setRoot(userPath);
	}


	public void paintPath(List<Node> directionNodes) {
		this.directionsTextField.getChildren().clear();
		this.lines.forEach(line -> {
			this.botPane.getChildren().remove(line);
		});

		//add kiosk to start of list
		//directionNodes.add(0, this.kiosk);
		if(directionNodes.size() <= 0){
			return;
		}


		double destX = directionNodes.get(directionNodes.size() - 1).getX();
		double destY = directionNodes.get(directionNodes.size() - 1).getY();

		// This is commented out because it is outdated code
//		this.destNode = new Node(destX, destY);
//		main.Pathfinder.findPath(this.kiosk.getLocation(), this.destNode);

		for (int i = 0; i < directionNodes.size() - 1; i++) {
			double nodeX1 = directionNodes.get(i).getX();
			//	System.out.println("X1: " + nodeX1);
			double nodeY1 = directionNodes.get(i).getY();
			//	System.out.println("Y1: " + nodeY1);
			double nodeX2 = directionNodes.get(i+1).getX();
			//	System.out.println("X2: " + nodeX2);
			double nodeY2 = directionNodes.get(i+1).getY();
			//	System.out.println("Y2: " + nodeY2);
			Line line = new Line();
			this.lines.add(line);
			line.setStartX(nodeX1);
			line.setStartY(nodeY1);
			line.setEndX(nodeX2);
			line.setEndY(nodeY2);

			this.botPane.getChildren().add(line);
		}

		Text textDirections = new Text();
		textDirections.setText(DirectionsGenerator.fromPath(directionNodes));
		//Call text directions
		this.directionsTextField.getChildren().add(textDirections);

	}

	private void changeFloor(int floor) {
		this.switchFloors(floor);
		this.imageViewMap.setImage(map);
		this.displayRooms(directory.getRoomsOnFloor(floor));
	}

}
