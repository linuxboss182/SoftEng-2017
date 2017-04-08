package controllers;

import entities.Room;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Slider;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.scene.image.Image;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import entities.Node;
import main.ApplicationController;
import main.DirectionsGenerator;
import main.Pathfinder;
//import userpanel.Window;


public class UserController
	extends MapDisplayController
		implements Initializable
{
	@FXML
	private Button logAsAdmin;
	@FXML
	private ImageView imageViewMap;
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
	@FXML
	private Button refreshbtn;
	@FXML
	protected Pane linePane;
	@FXML
	protected Pane nodePane;

	final double SCALE_DELTA = 1.1;
	private double clickedX, clickedY;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		//Set the panes
		this.setPanes(linePane, nodePane);
		//Grab the database controller from main and use it to populate our directory
		this.directory = ApplicationController.getDirectory();

		//Add map
		this.map4 = new Image("/4_thefourthfloor.png");
		this.imageViewMap.setImage(this.map4);
		this.imageViewMap.setPickOnBounds(true);

		this.kiosk = null;
		for (Room r : this.directory.getRooms()) {
			if (r.getName().equalsIgnoreCase("YOU ARE HERE")) {
				this.kiosk = r;
			}
		}
		this.displayRooms();
		this.populateListView();


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
				contentAnchor.setTranslateX(contentAnchor.getTranslateX() + event.getX() - clickedX);
				contentAnchor.setTranslateY(contentAnchor.getTranslateY() + event.getY() - clickedY);
				event.consume();
			}
		});


	}





	@FXML
	private void refreshbtnClicked(){
		this.displayRooms();
		this.populateListView();
	}

	@FXML
	private void logAsAdminClicked()
			throws IOException, InvocationTargetException {
		// TODO: Review
		Parent loginPrompt = (AnchorPane) FXMLLoader.load(this.getClass().getResource("/LoginPrompt.fxml"));
		this.contentAnchor.getScene().setRoot(loginPrompt);


	}

	public void populateListView() {
		this.directoryList.itemsProperty().bind(this.listProperty);
		this.listProperty.set(FXCollections.observableArrayList(this.directory.getRooms()));


		this.directoryList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Room>() {
			@Override
			public void changed(ObservableValue<? extends Room> observable, Room oldValue, Room newValue) {
				List<Node> ret;
				ret = Pathfinder.findPath(kiosk, newValue);
				paintPath(new ArrayList<>(ret));
			}
		});

	}

	@FXML
	public void getDirectionsClicked() {

		this.paintPath(this.directionNodes);
	}

	@FXML
	public void doneBtnClicked() {
		this.directionsTextField.getChildren().removeAll();

	}

	// TODO: There was a bug with rectangles not showing up where they were supposed to.
	// Because of that bug, we did not find the bug with this code
	// Since rectangles are not stored based off of their center points,
	// // and we want to draw based on their center points,
	// // these lines do not draw onto rectangles properly
	// Currently, the lines are drawn to the top left of the rectangles
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

			this.botPane.getChildren().add(line);
		}

		Text textDirections = new Text();
		textDirections.setText(DirectionsGenerator.fromPath(directionNodes));
		//Call text directions
		this.directionsTextField.getChildren().add(textDirections);

	}

}
