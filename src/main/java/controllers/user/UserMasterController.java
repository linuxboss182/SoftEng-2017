package controllers.user;

import controllers.shared.FloorProxy;
import controllers.shared.MapDisplayController;
import entities.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.scene.text.TextFlow;
import main.ApplicationController;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public abstract class UserMasterController extends MapDisplayController
{
	@FXML
	private Button logAsAdmin;
	@FXML
	private ImageView imageViewMap;
	@FXML
	private AnchorPane contentAnchor = new AnchorPane();
	@FXML
	private ListView<Room> directoryView;
	@FXML
	private Button getDirectionsBtn;
	@FXML
	private Button changeStartBtn;
	@FXML
	private Pane linePane;
	@FXML
	protected TextField searchBar;
	@FXML
	private Pane nodePane;
	@FXML
	protected TextFlow directionsTextField;
	@FXML
	private GridPane sideGridPane;
	@FXML
	private ChoiceBox floorChoiceBox;

	final double SCALE_DELTA = 1.1;
	final protected double zoomMin = 1/SCALE_DELTA;
	final protected double zoomMax = SCALE_DELTA*5;

	private double clickedX, clickedY;
	protected static Room startRoom;
	protected static Room endRoom;
	protected static boolean choosingStart = false;
	protected static boolean choosingEnd = true; // Default this to true because that's the screen we start on

	/**
	 * Get the scene this is working on
	 */
	protected Scene getScene() {
		// The contentAnchor should alays exist, so use it to get the scene
		return this.contentAnchor.getScene();
	}

	public void initialize() {
		//Set the panes
		this.setPanes(linePane, nodePane);
		//Grab the database controller from main and use it to populate our directory
		this.directory = ApplicationController.getDirectory();
		iconController = ApplicationController.getIconController();

		//Add map
		//this.map = new Image("/4_thefourthfloor.png");
		// use floor proxy class to load in map
		this.map = new FloorProxy(floor).display();
		this.imageViewMap.setImage(this.map);
		this.imageViewMap.setPickOnBounds(true);

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

		if(floorChoiceBox != null) {
			initFloorChoiceBox();
		}

		this.displayRooms();
		iconController.resetAllRooms();
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
				// Pretty much just limit the scaling minimum and maximum
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
	 * Filter the room list for the search bar
	 *
	 * @param oldValue The previous string in the search bar
	 * @param newValue The new string in the search bar
	 */
	// TODO: DOCUMENT THIS URGENTLY; (it can be made simpler)
	public void filterRoomList(String oldValue, String newValue) {
		ObservableList<Room> filteredList = FXCollections.observableArrayList();
		if((searchBar == null) || (newValue == null) || (newValue.length() < oldValue.length())) {
			populateListView();
		}
		else {
			newValue = newValue.toUpperCase();
			for(Room room : directoryView.getItems()) {
				Room filterText = room;
				if(filterText.getName().toUpperCase().contains(newValue)) {
					filteredList.add(filterText);
				}
			}

			directoryView.setItems(filteredList);
		}
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
		this.getScene().setRoot(loginPrompt);


	}

	public void displayRooms() {
		Set<javafx.scene.Node> roomShapes = new HashSet<>();
		for (Room r : directory.getRoomsOnFloor(floor)) {
			roomShapes.add(r.getShape());
			/* This is code to make a context menu appear when you right click on the shape for a room
			 * setonContextMenuRequested pretty much checks the right click- meaning right clicking is how you request a context menu
			 * that is reallllllllly helpful for a lot of stuff
			 */
			r.getShape().setOnContextMenuRequested(e->{

				ContextMenu optionsMenu = new ContextMenu();

				MenuItem startRoomItem = new MenuItem("Set as starting location");
				startRoomItem.setOnAction(e1 -> selectStartRoom(r));
				MenuItem endRoomItem = new MenuItem("Set as destination");
				endRoomItem.setOnAction(e2-> selectEndRoom(r));
				optionsMenu.getItems().addAll(startRoomItem, endRoomItem);
				optionsMenu.show(r.getShape(), e.getScreenX(), e.getScreenY());
			});
		}
		this.topPane.getChildren().setAll(roomShapes);
	}

	public void populateListView() {
		this.directoryView.setItems(this.listProperty);
		this.listProperty.set(FXCollections.observableArrayList(this.directory.getRooms()));

		this.directoryView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Room>() {
			@Override
			public void changed(ObservableValue<? extends Room> observable, Room oldValue, Room newValue) {
				// Commented this out because we are not going to want to get directions as soon as they click on the list view
//				List<Node> ret;
//				if(kiosk != null) {
//					ret = Pathfinder.findPath(kiosk.getLocation(), newValue.getLocation());
//					paintPath(new ArrayList<>(ret));
//				} else {
//
//				}
				// These variables are set in the controllers when the scene is switched...
				if(choosingEnd) {

					selectEndRoom(directoryView.getSelectionModel().getSelectedItem());
				} else if(choosingStart) {

					selectStartRoom(directoryView.getSelectionModel().getSelectedItem());

				}
			}
		});
	}
	
	
	protected void enableOrDisableNavigationButtons() {
		if (this.getDirectionsBtn != null) {
			if (endRoom == null || startRoom == null) {
				this.getDirectionsBtn.setDisable(true);
			} else {
				this.getDirectionsBtn.setDisable(false);
			}
		}
		if (this.changeStartBtn != null) {
			this.changeStartBtn.setDisable((endRoom != null) ? false : true);
		}
	}




		// Commented out because we have a separate tab for choosing the start and the end, and left/right clicking is somewhat confusing for this sort of menu
//		ArrayList<Room> tempRooms = new ArrayList<>(this.directory.getRooms());
//		this.directoryView.setOnMousePressed(e->{
//			switch (e.getButton()) {
//				case PRIMARY:
//					endRoom = directoryView.getSelectionModel().getSelectedItem();
//					System.out.println(endRoom);
//					break;
//				case SECONDARY:
//					startRoom = directoryView.getSelectionModel().getSelectedItem();
//					System.out.println(startRoom);
//					break;
//				default:
//					break;
//			}
//		});
		}
	public void setDisable() {
		changeStartBtn.setDisable(false);
		getDirectionsBtn.setDisable(false);
	}

	@FXML
	public void getDirectionsClicked() throws IOException, InvocationTargetException {
		Parent userPath = (BorderPane) FXMLLoader.load(this.getClass().getResource("/UserPath.fxml"));
		this.getScene().setRoot(userPath);
	}

	/**
	 * Draw a simple path between the nodes in the given list
	 *
	 * @param directionNodes A list of the nodes in the path, in order
	 */
	public void paintPath(List<Node> directionNodes) {
		this.directionsTextField.getChildren().clear();

		//add kiosk to start of list
		//directionNodes.add(0, this.kiosk);
		if(directionNodes.size() <= 0) {
			// TODO: Give an error message when no path is found
			return;
		}

		// This can be any collection type;
		Collection<Line> path = new HashSet<>();
		for (int i=0; i < directionNodes.size()-1; ++i) {
			Node here = directionNodes.get(i);
			Node there = directionNodes.get(i + 1);
			if (here.getFloor() == floor && here.getFloor() == there.getFloor()) {
				Line line = new Line(here.getX(), here.getY(), there.getX(), there.getY());
				path.add(line);
			}
		}
		this.botPane.getChildren().setAll(path);


	}

	protected void changeFloor(int floor) {
		this.switchFloors(floor);
		this.imageViewMap.setImage(map);
		this.displayRooms();
	}


	/**
	 * Below are helper methods to select and deselect the starting rooms for a path
	 */

	protected void selectStartRoom(Room r) {
		if(r == null) return;
		startRoom = r;
		this.enableOrDisableNavigationButtons();
//		this.enableDirectionsBtn();
		iconController.selectStartRoom(r);
		this.displayRooms();
	}

	protected void selectEndRoom(Room r) {
		if(r == null) return;
		endRoom = r;
		this.enableOrDisableNavigationButtons();
//		this.enableDirectionsBtn();
//		this.enableChangeStartBtn();
		iconController.selectEndRoom(r);
		this.displayRooms();
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

}