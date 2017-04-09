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
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import main.ApplicationController;
import main.DirectionsGenerator;
import main.Pathfinder;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by s7sal on 4/8/2017.
 */
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


	final double SCALE_DELTA = 1.1;
	private double clickedX, clickedY;


	public void initialize() {
		//Set the panes
		this.setPanes(linePane, nodePane);
		//Grab the database controller from main and use it to populate our directory
		this.directory = ApplicationController.getDirectory();

		//Add map
		this.map = new Image("/4_thefourthfloor.png");
		this.imageViewMap.setImage(this.map);
		this.imageViewMap.setPickOnBounds(true);

		this.kiosk = null;
		for (Room r : this.directory.getRooms()) {
			if (r.getName().equalsIgnoreCase("YOU ARE HERE")) {
				this.kiosk = r;
			}
		}
		this.displayRooms(directory.getRoomsOnFloor(floor));
		//this.populateListView();


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
				if(potentialScaleX < 1/SCALE_DELTA) {
					potentialScaleX = 1/SCALE_DELTA;
				}
				if(potentialScaleY < 1/SCALE_DELTA) {
					potentialScaleY = 1/SCALE_DELTA;
				}
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

	@FXML
	public void logAsAdminClicked()
			throws IOException, InvocationTargetException {
		// TODO: Review
		Parent loginPrompt = (AnchorPane) FXMLLoader.load(this.getClass().getResource("/LoginPrompt.fxml"));
		this.contentAnchor.getScene().setRoot(loginPrompt);


	}

	public void populateListView() {
		this.directoryView.itemsProperty().bind(this.listProperty);
		this.listProperty.set(FXCollections.observableArrayList(this.directory.getRooms()));


		this.directoryView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Room>() {
			@Override
			public void changed(ObservableValue<? extends Room> observable, Room oldValue, Room newValue) {
				List<Node> ret;
				ret = Pathfinder.findPath(kiosk.getLocation(), newValue.getLocation());
				paintPath(new ArrayList<>(ret));
			}
		});

		}

	@FXML
	public void getDirectionsClicked() throws IOException, InvocationTargetException {
		Parent userPath = (BorderPane) FXMLLoader.load(this.getClass().getResource("/UserPath.fxml"));
		this.imageViewMap.getScene().setRoot(userPath);
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
		main.Pathfinder.findPath(this.kiosk.getLocation(), this.destNode);

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
