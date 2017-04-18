package controllers.user;

import controllers.shared.FloorProxy;
import controllers.shared.MapDisplayController;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.scene.text.TextFlow;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.Collator;
import java.text.Normalizer;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import entities.Node;
import entities.Room;
import main.ApplicationController;

import static com.sun.java.accessibility.util.AWTEventMonitor.addComponentListener;


public abstract class UserMasterController
		extends MapDisplayController
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
	@FXML
	private ToolBar bottomToolbar;
	@FXML
	private BorderPane parentBorderPane;

	final double SCALE_DELTA = 1.1;
	final protected double zoomMin = 1/SCALE_DELTA;
	final protected double zoomMax = SCALE_DELTA*5;

	private double clickedX, clickedY;
	protected static Room startRoom;
	protected static Room endRoom;
	protected static boolean choosingStart = false;
	protected static boolean choosingEnd = true; // Default this to true because that's the screen we start on


	/* ABSTRACT METHODS */
	/**
	 * Function called when a room is left clicked on the map
	 * @param room
	 */
	protected abstract void clickRoomAction(Room room);


	/* NON-ABSTRACT METHODS */

	/**
	 * Get the scene this is working on
	 */
	protected Scene getScene() {
		// The contentAnchor should alays exist, so use it to get the scene
		return this.parentBorderPane.getScene();
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
		this.map = FloorProxy.getMaps().get(floor - 1).display();
		this.imageViewMap.setImage(this.map);
		this.imageViewMap.setPickOnBounds(true);

		// Set buttons to default
		this.enableOrDisableNavigationButtons();

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

		//Call listeners for window resizing
		windowResized();


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
	 * @param searchString The new string in the search bar
	 */
	public void filterRoomsByName(String searchString) {
		if((this.searchBar == null) || (searchString == null) || (searchString.length() == 0)) {
			this.populateListView();
		} else {
			// The Collator allows case-insensitie comparison
			Collator coll = Collator.getInstance();
			coll.setStrength(Collator.PRIMARY);
			// coll.setDecomposition(Collator.FULL_DECOMPOSITION); <- done by Normalizer

			// Normalize accents, remove leading spaces, remove duplicate spaces elsewhere
			String normed = Normalizer.normalize(searchString, Normalizer.Form.NFD).toLowerCase()
					.replaceAll("^\\s*", "").replaceAll("\\s+", " ");

			Set<Room> roomSet = directory.filterRooms(room ->
					(room.getLocation() != null) && // false if room has no location
					Normalizer.normalize(room.getName(), Normalizer.Form.NFD).toLowerCase()
					          .contains(normed)); // check with unicode normalization

			this.directoryView.setItems(FXCollections.observableArrayList(roomSet));
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
		// Unset navigation targets for after logout
		startRoom = null;
		endRoom = null;
		Parent loginPrompt = (AnchorPane) FXMLLoader.load(this.getClass().getResource("/LoginPrompt.fxml"));
		this.getScene().setRoot(loginPrompt);


	}

	public void displayRooms() {
		Set<javafx.scene.Node> roomShapes = new HashSet<>();
		for (Room room : directory.getRoomsOnFloor(floor)) {
			roomShapes.add(room.getShape());
			/* This is code to make a context menu appear when you right click on the shape for a room
			 * setonContextMenuRequested pretty much checks the right click- meaning right clicking is how you request a context menu
			 * that is reallllllllly helpful for a lot of stuff
			 */
			room.getShape().setOnMouseClicked((MouseEvent e) -> {
				if (e.getButton() == MouseButton.PRIMARY) this.clickRoomAction(room);
			});
			room.getShape().setOnContextMenuRequested(e -> {

				ContextMenu optionsMenu = new ContextMenu();

				MenuItem startRoomItem = new MenuItem("Set as starting location");
				startRoomItem.setOnAction(e1 -> selectStartRoom(room));
				MenuItem endRoomItem = new MenuItem("Set as destination");
				endRoomItem.setOnAction(e2-> selectEndRoom(room));
				optionsMenu.getItems().addAll(startRoomItem, endRoomItem);
				optionsMenu.show(room.getShape(), e.getScreenX(), e.getScreenY());
			});
		}
		this.topPane.getChildren().setAll(roomShapes);
	}

	public void populateListView() {
		this.directoryView.setItems(this.listProperty);
		this.listProperty.set(FXCollections.observableArrayList(directory.filterRooms(r -> r.getLocation() != null)));

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

	/**
	 * Enable or disable the "get directions" and "set starting location" buttons
	 * 
	 * If both start and end locations are set, enable the "get directions" button
	 * 
	 * If The end room is set, enable the "set starting location" button
	 */
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




	@FXML
	public void getDirectionsClicked() throws IOException, InvocationTargetException {
		Parent userPath = (BorderPane) FXMLLoader.load(this.getClass().getResource("/UserPath.fxml"));
		this.getScene().setRoot(userPath);
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

	public void scaleElements() {
		this.bottomToolbar.prefWidthProperty().bind(this.parentBorderPane.widthProperty());
		this.contentAnchor.prefWidthProperty().bind(this.parentBorderPane.widthProperty());
		System.out.println("half of window: " + parentBorderPane.getWidth() / 2);
		this.getDirectionsBtn.relocate((parentBorderPane.getWidth()/ 2), (bottomToolbar.getHeight()/2));
		//this.getDirectionsBtn.relocate((500.0), (bottomToolbar.getHeight()/2));
	}

	public void windowResized() {

		this.parentBorderPane.widthProperty().addListener(new ChangeListener<Number>() {
			@Override public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneWidth, Number newSceneWidth) {
				System.out.println("Width: " + newSceneWidth);
				scaleElements();
			}
		});
		this.parentBorderPane.heightProperty().addListener(new ChangeListener<Number>() {
			@Override public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneHeight, Number newSceneHeight) {
				System.out.println("Height: " + newSceneHeight);
				scaleElements();
			}
		});
	}

}
