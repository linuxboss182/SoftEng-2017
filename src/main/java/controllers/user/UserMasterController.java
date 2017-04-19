package controllers.user;

import com.jfoenix.controls.JFXButton;
import controllers.admin.AddProfessionalController;
import controllers.shared.FloorProxy;
import controllers.shared.MapDisplayController;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.*;
import javafx.scene.text.TextFlow;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.Collator;
import java.text.Normalizer;
import java.util.HashSet;
import java.util.Set;

import entities.Room;
import javafx.stage.Stage;
import main.ApplicationController;

import controllers.shared.FloorImage;
import controllers.shared.FloorProxy;
import controllers.shared.MapDisplayController;


public abstract class UserMasterController
		extends MapDisplayController
{
	@FXML
	private JFXButton logAsAdmin;
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
	private ComboBox<FloorProxy> floorChoiceBox;
	@FXML
	private ComboBox buildingChoiceBox;
	@FXML
	private ToolBar bottomToolbar;
	@FXML
	private BorderPane parentBorderPane;
	@FXML
	private SplitPane mapSplitPane;
	@FXML
	private GridPane destGridPane;
	@FXML
	private GridPane bottomGridPane;
	@FXML
	private Button aboutBtn;
	@FXML
	private ImageView logoImageView;
	@FXML
	private ScrollPane mapScroll = new ScrollPane();

	final double SCALE_DELTA = 1.1;
	final protected double zoomMin = 1;
	final protected double zoomMax = 6;

	private double clickedX, clickedY;
	protected double SCALE_TOTAL = 1;
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
		mapScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
		mapScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

		//Set the panes
		this.setPanes(linePane, nodePane);
		//Grab the database controller from main and use it to populate our directory
		this.directory = ApplicationController.getDirectory();
		iconController = ApplicationController.getIconController();

		//Add map
		//this.map = new Image("/4_thefourthfloor.png");
		// use floor proxy class to load in map
		this.changeFloor(getFloor());
		this.imageViewMap.setPickOnBounds(true);


		//Load logo
//		Image logo;
//		logo = new Image("/bwhLogo.png");
//		logoImageView.setImage(logo);


		// Set buttons to default
		this.enableOrDisableNavigationButtons();

		// I tested this value, and we want it to be defaulted here because the map does not start zoomed out all the way
		zoomSlider.setValue(0);
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
				mapScroll.setScaleX(zoomCoefficient);
				mapScroll.setScaleY(zoomCoefficient);
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

				if (scaleFactor * SCALE_TOTAL >= 1 && scaleFactor * SCALE_TOTAL <= 6) {
					Bounds viewPort = mapScroll.getViewportBounds();
					Bounds contentSize = contentAnchor.getBoundsInParent();

					double centerPosX = (contentSize.getWidth() - viewPort.getWidth()) * mapScroll.getHvalue() + viewPort.getWidth() / 2;

					double centerPosY = (contentSize.getHeight() - viewPort.getHeight()) * mapScroll.getVvalue() + viewPort.getHeight() / 2;

					mapScroll.setScaleX(mapScroll.getScaleX() * scaleFactor);
					mapScroll.setScaleY(mapScroll.getScaleY() * scaleFactor);
					SCALE_TOTAL *= scaleFactor;

					double newCenterX = centerPosX * scaleFactor;
					double newCenterY = centerPosY * scaleFactor;

					mapScroll.setHvalue((newCenterX - viewPort.getWidth() / 2) / (contentSize.getWidth() * scaleFactor - viewPort.getWidth()));
					mapScroll.setVvalue((newCenterY - viewPort.getHeight() / 2) / (contentSize.getHeight() * scaleFactor - viewPort.getHeight()));
				}

				if (scaleFactor * SCALE_TOTAL <= 1) {
//					SCALE_TOTAL = 1/scaleFactor;
					zoomSlider.setValue(0);

				}else if(scaleFactor * SCALE_TOTAL >= 5.5599173134922495) {
//					SCALE_TOTAL = 6 / scaleFactor;
					zoomSlider.setValue(100);

				}else {
					zoomSlider.setValue(((SCALE_TOTAL - 1)/4.5599173134922495) * 100);
				}

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

		/** This is the section for key listeners.
		 *  Press Ctrl + Open Bracket for zoom in
		 *  Press Ctrl + Close Bracket for zoom out
		 *  Press Ctrl + DIGIT1 to view the map for floor 1
		 *  Press Ctrl + DIGIT2 to view the map for floor 2
		 *  Press Ctrl + DIGIT3 to view the map for floor 3
		 *  Press Ctrl + DIGIT4 to view the map for floor 4
		 *  Press Ctrl + DIGIT5 to view the map for floor 5
		 *  Press Ctrl + DIGIT6 to view the map for floor 6
		 *  Press Ctrl + DIGIT7 to view the map for floor 7
		 *  Press Shift + Right to move the view to the right
		 *  Press Shift + Left to move the view to the left
		 *  Press Shift + Up to move the view to the up
		 *  Press Shift + down to move the view to the down
		 *
		 */

		// TODO: Allow changing of floor without building, then add this back in
		parentBorderPane.setOnKeyPressed(e -> {
//			System.out.println(e); // Prints out key statements
			System.out.println(e.getCode());// Prints out key statements
			if (e.getCode() == KeyCode.OPEN_BRACKET && e.isControlDown()) {
				increaseZoomButtonPressed();
			}else if (e.getCode() == KeyCode.CLOSE_BRACKET && e.isControlDown()) {
				decreaseZoomButtonPressed();
			}else if (e.getCode() == KeyCode.RIGHT && e.isShiftDown()) {
				contentAnchor.setTranslateX(contentAnchor.getTranslateX() - 10);
			}else if (e.getCode() == KeyCode.LEFT && e.isShiftDown()) {
				contentAnchor.setTranslateX(contentAnchor.getTranslateX() + 10);
			}else if (e.getCode() == KeyCode.UP && e.isShiftDown()) {
				contentAnchor.setTranslateY(contentAnchor.getTranslateY() + 10);
			}else if (e.getCode() == KeyCode.DOWN && e.isShiftDown()) {
				contentAnchor.setTranslateY(contentAnchor.getTranslateY() - 10);
			}
			e.consume();
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
	public void initFloorChoiceBox() {
//		// We are able to change what this list is of.
//		this.floorComboBox.setItems(FXCollections.observableArrayList("Floor 1", "Floor 2", "Floor 3", "Floor 4", "Floor 5", "Floor 6", "Floor 7"));
//		this.floorComboBox.setValue(this.floorComboBox.getItems().get(floor-1)); // default the selection to be whichever floor we start on
		this.floorChoiceBox.setItems(FXCollections.observableArrayList(FloorProxy.getFloors()));
		this.floorChoiceBox.getSelectionModel().selectedItemProperty().addListener(
				(ignored, ignoredOld, choice) -> this.changeFloor(choice));
		//this.floorChoiceBox.setConverter(FloorImage.FLOOR_STRING_CONVERTER);

		this.floorChoiceBox.setValue(this.floorChoiceBox.getItems().get(getFloorNum() - 1)); // default the selection to be whichever floor we start on

	}

	@FXML
	public void logAsAdminClicked()
			throws IOException, InvocationTargetException {
		// TODO: Review
		// Unset navigation targets for after logout
		startRoom = null;
		endRoom = null;
		Parent loginPrompt = (BorderPane) FXMLLoader.load(this.getClass().getResource("/LoginPrompt.fxml"));
		this.getScene().setRoot(loginPrompt);


	}

	public void displayRooms() {
		Set<javafx.scene.Node> roomShapes = new HashSet<>();
		for (Room room : directory.getRoomsOnFloor(getFloor())) {
			roomShapes.add(room.getUserSideShape());
			/* This is code to make a context menu appear when you right click on the shape for a room
			 * setonContextMenuRequested pretty much checks the right click- meaning right clicking is how you request a context menu
			 * that is reallllllllly helpful for a lot of stuff
			 */
			room.getUserSideShape().setOnMouseClicked((MouseEvent e) -> {
				if (e.getButton() == MouseButton.PRIMARY) this.clickRoomAction(room);
			});
			room.getUserSideShape().setOnContextMenuRequested(e -> {

				ContextMenu optionsMenu = new ContextMenu();

				MenuItem startRoomItem = new MenuItem("Set as starting location");
				startRoomItem.setOnAction(e1 -> selectStartRoom(room));
				MenuItem endRoomItem = new MenuItem("Set as destination");
				endRoomItem.setOnAction(e2-> selectEndRoom(room));
				optionsMenu.getItems().addAll(startRoomItem, endRoomItem);
				optionsMenu.show(room.getUserSideShape(), e.getScreenX(), e.getScreenY());
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


	protected void changeFloor(FloorImage floor) {
		Image map = this.switchFloors(floor);
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
//		this.bottomToolbar.prefWidthProperty().bind(this.parentBorderPane.widthProperty());
//		//this.contentAnchor.prefWidthProperty().bind(this.mapSplitPane.widthProperty());
//		if(this.getDirectionsBtn != null) {
//			this.getDirectionsBtn.relocate((parentBorderPane.getWidth()/ 2), 0);
//		}
//
//		double windowWidth = parentBorderPane.getWidth();
//		//destGridPane.setPrefWidth(windowWidth / 4);
//
//		//directoryView.setPrefWidth(destGridPane.getWidth() - 30.0);
//		//destGridPane.minWidthProperty().set(directoryView.getWidth() + 30);
//		if(this.bottomGridPane != null) {
//			bottomGridPane.setPrefWidth(bottomToolbar.getPrefWidth() - 100);
//			for (ColumnConstraints c : bottomGridPane.getColumnConstraints()) {
//				c.setPrefWidth(bottomToolbar.getWidth() / 3);
//			}
//		}

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

	@FXML
	public void aboutBtnClicked () throws IOException {
		UserAboutPage aboutPageController = new UserAboutPage();
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(this.getClass().getResource("/aboutPage.fxml"));
		Scene addAboutScene = new Scene(loader.load());
		Stage addAboutStage = new Stage();
		addAboutStage.initOwner(contentAnchor.getScene().getWindow());
		addAboutStage.setScene(addAboutScene);
		addAboutStage.showAndWait();
	}


}
