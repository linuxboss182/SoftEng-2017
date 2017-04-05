package userpanel;

import adminpanel.EditorController;
import entities.Directory;
import entities.Room;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Slider;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;
import entities.Node;
import main.ApplicationController;
import main.DatabaseController;
import main.Pathfinder;


public class Controller extends Window implements Initializable
{
	@FXML
	private Button logAsAdmin;
	@FXML
	private ImageView imageViewMap;
	@FXML
	public VBox contentVBox;
	@FXML
	public HBox contentHBox;
	@FXML
	public AnchorPane contentAnchor = new AnchorPane();
	@FXML
	private Slider floorSlider;
	@FXML
	private RadioButton elevatorRadio;
	@FXML
	private RadioButton stairRadio;
	@FXML
	private Button doneBtn;
	@FXML
	private ListView<Room> directoryList;
	@FXML
	private TextFlow directionsTextField;


	private Image map4;
	private Node clickNode;
	private ArrayList<Node> directionNodes = new ArrayList<Node>();
	private ArrayList<Node> alon = new ArrayList<>();
	private ArrayList<Room> roomList = new ArrayList<>();
	protected ListProperty<Room> listProperty = new SimpleListProperty<>();
	private Directory directory;
	private Room kiosk;
	private Node destNode;

	private static final Color DEFAULT_SHAPE_COLOR = Color.web("0x0000FF");
	private static final Color SELECTED_SHAPE_COLOR = Color.BLACK;
	private static final Color CONNECTION_LINE_COLOR = Color.BLACK;
	private static final Color KIOSK_COLOR = Color.RED;

	private static final double RECTANGLE_WIDTH = 7;
	private static final double RECTANGLE_HEIGHT = 7;
	private static final double CIRCLE_RADIUS = 5;
	private static final String KIOSK_NAME = "You Are Here";



	@Override
	public void initialize(URL location, ResourceBundle resources) {
		//Grab the database controller from main and use it to populate our directory
		this.directory = ApplicationController.getDirectory();


		//Add map
		this.map4 = new Image("/4_thefourthfloor.png");
		this.imageViewMap.setImage(this.map4);
		//this.imageViewMap.fitWidthProperty().bind(this.primaryStage.widthProperty());


		//set elevator to clicked
		//this.elevatorRadio.focusedProperty(true);
		//display nodes on map
		//this.displayNodes(this.alon);

		this.imageViewMap.setPickOnBounds(true);


		this.imageViewMap.setOnMouseClicked(e -> {
			System.out.println("[" + e.getX() + ", " + e.getY() + "]");
			//Paint something at that location
			this.clickNode = new Node(e.getX(), e.getY());
			this.directionNodes.add(this.clickNode);
			this.displayNodes();
			this.paintOnLocation(e.getX(), e.getY());
		});

		//populate listview
		// Room r1 = new Room("Room 101", "Hi");
		// Room r2 = new Room("Room 102", "Hi");
		// Room r3 = new Room("Room 103", "Hi");
		// this.roomList.add(r1);
		// this.roomList.add(r2);
		// this.roomList.add(r3);
		// this.populateListView(this.roomList);
		//make kiosk
//		this.kiosk = new Room(353.5, 122.5);
//		this.kiosk.setName(KIOSK_NAME);
//		this.directory.addRoom(this.kiosk);
//		this.paintRoomOnLocation(this.kiosk);

		this.kiosk = new Room(353.5, 122.5);

		this.displayNodes();
		this.populateListView();



	}

	public void paintRoomOnLocation(Room r) {
		Rectangle rect;
		rect = new Rectangle(r.getX(), r.getY(), this.RECTANGLE_WIDTH, this.RECTANGLE_HEIGHT);
		if (r.getName().equals(KIOSK_NAME)) {
			rect.setFill(KIOSK_COLOR);
		} else {
			rect.setFill(this.DEFAULT_SHAPE_COLOR);
		}

		this.contentAnchor.getChildren().add(rect);
		rect.setVisible(true);

	}

	@FXML
	private void logAsAdminClicked()
			throws IOException, InvocationTargetException {
		Parent loginPrompt = (AnchorPane) FXMLLoader.load(this.getClass().getResource("/LoginPrompt.fxml"));
		System.out.print("here");
		Scene loginPromptScene = new Scene(loginPrompt);
		Stage loginPromptStage = new Stage();
		loginPromptStage.setScene(loginPromptScene);


		loginPromptStage.showAndWait();


	}

	public void populateListView() {
		//this.directoryList = new ListView();
//		this.directory.addRoom(new Room(50,50,"test", "test"));
//		System.out.println(this.directory.getRooms());

		this.directoryList.itemsProperty().bind(this.listProperty);
		this.listProperty.set(FXCollections.observableArrayList(this.directory.getRooms()));


		this.directory.getRooms().forEach(room -> {

			room.connect(this.kiosk);

		//		this.directoryList.itemsProperty().bind(FXCollections.observableArrayList(this.directory.getRooms()));

				//this.directoryList.itemsProperty().bind(FXCollections.observableArrayList(this.directory.getRooms()));
				//	this.listProperty.set(FXCollections.observableArrayList(this.directory.getRooms().forEach(room -> {})));

		//			this.listProperty.set(FXCollections.observableArrayList(room.getName()));

		});



//		listProperty.setOnMouseClicked((MouseEvent e) -> {
//			EditorController.this.onShapeClick(e, n);
//		});

		this.directoryList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Room>() {
			@Override
			public void changed(ObservableValue<? extends Room> observable, Room oldValue, Room newValue) {
				// Your action here
				//	System.out.println("Selected item: " + newValue.getName());
				List<Node> ret;

				ret = Pathfinder.findPath(kiosk, newValue);

				paintPath(new ArrayList<>(ret));

			}
		});

	}


	public void paintOnLocation(double x, double y) {
		Circle circ;
		circ = new Circle(x, y, 5, Color.web("0x0000FF"));

		this.contentAnchor.getChildren().add(circ);

		circ.setVisible(true);
	}


	public void displayNodes() {
		this.directory.getNodes().forEach(node -> {
			Circle circ;
			double nodeX = node.getX();
			double nodeY = node.getY();
			circ = new Circle(nodeX, nodeY, 5, Color.web("0x0000FF"));
			this.contentAnchor.getChildren().add(circ);
			circ.setVisible(true);
				}
		);

	}

	@FXML
	public void getDirectionsClicked() {

		this.paintPath(this.directionNodes);
		Text t1 = new Text("hey");
		Text t2 = new Text("heyyyyy");
		this.directionsTextField.getChildren().add(t1);
		this.directionsTextField.getChildren().add(t2);


	}

	@FXML
	public void doneBtnClicked() {

	}

	private ArrayList<Line> lines = new ArrayList<Line>();

	public void paintPath(ArrayList<entities.Node> directionNodes) {
		this.lines.forEach(line -> {
			this.contentAnchor.getChildren().remove(line);
		});

		//add kiosk to start of list
		directionNodes.add(0, this.kiosk);

		double destX = directionNodes.get(directionNodes.size() - 1).getX();
		double destY = directionNodes.get(directionNodes.size() - 1).getY();

		this.destNode = new Node(destX, destY);
		main.Pathfinder.findPath(this.kiosk, this.destNode);

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

			this.contentAnchor.getChildren().add(line);
		}
	}

	public void clearPath(ArrayList<entities.Node> directionNodes) {
		directionNodes.clear();
		for (int i = 0; i < directionNodes.size(); i++) {

		}

	}
}
