package controllers.shared;

import com.jfoenix.controls.JFXDrawer;
import controllers.admin.EditorController;
import controllers.icons.IconController;
import controllers.user.UserState;
import controllers.icons.IconManager;
import entities.*;
import javafx.application.Platform;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import main.ApplicationController;
import main.TimeoutTimer;

import java.util.Collection;
import java.util.TimerTask;

// TODO: Use this class more effectively
// Move stuff here when possible, remove unneeded stuff later

public abstract class MapDisplayController
{
	protected final double SCALE_DELTA = 1.1;
	final protected double zoomMin = 1;
	final protected double zoomMax = 6;
	protected double currentScale = 1;

	protected TimeoutTimer timer = TimeoutTimer.getTimeoutTimer();

	@FXML public AnchorPane contentAnchor;
	@FXML protected ImageView imageViewMap;
	@FXML protected ScrollPane mapScroll;
	protected Directory directory;
	protected IconController iconController;

	// Primary is left click and secondary is right click
	// these keep track of which button was pressed last on the mouse
	protected boolean primaryPressed;
	protected boolean secondaryPressed;

	protected double releasedX;
	protected double releasedY;

	// TODO: Remove excessive unecessary state from ALL Controllers (not just this one)
	protected static final Color CONNECTION_LINE_COLOR = Color.BLACK;

	protected Professional selectedProf;

	// default to floor 1
	protected static FloorImage floor = FloorProxy.getFloor("FAULKNER", 1);

	@FXML protected Slider zoomSlider;
	@FXML protected BorderPane parentBorderPane;
	protected IconManager iconManager = new IconManager();


	protected void initialize() {
		this.directory = ApplicationController.getDirectory();
		this.iconController = ApplicationController.getIconController();

		this.changeFloor(this.directory.getFloor());
		this.imageViewMap.setPickOnBounds(true);

		this.setHotkeys();
		this.setScrollZoom();
		this.setZoomSliding();
		this.zoomSlider.setValue(0);

		timer.emptyTasks();
		this.initGlobalFilter();
		TimeoutTimer.getTimeoutTimer().registerTask(() -> {
			setState(directory.getCaretaker().getState());
		});
		Platform.runLater(this::initWindowResizeListener);
		Platform.runLater(this::fitMapSize);
	}

	/**
	 * Display any edges between any of the given nodes
	 *
	 * @param nodes A collection of nodes to draw edges between
	 */
	public void redrawEdges(Collection<Node> nodes) {}

	/**
	 * Method to redisplay anything that should be drawn on the map
	 */
	protected abstract void redisplayMapItems();

	protected void changeFloor(FloorImage floor) {
		String oldFloor = floor.getName();
		Image map = this.directory.switchFloors(floor);
		this.imageViewMap.setImage(map);
		this.redisplayMapItems();

		if(!directory.getFloor().getName().equals(oldFloor)) {
			this.fitMapSize();
		}
	}


	protected void setScrollZoom() {

		this.contentAnchor.setOnScroll(event -> {
			event.consume();
			if (event.getDeltaY() == 0) {
				return;
			}
			double scaleFactor = (event.getDeltaY() > 0)
								 ? SCALE_DELTA
								 : 1/SCALE_DELTA;

			if (scaleFactor * currentScale >= 1 && scaleFactor * currentScale <= 6) {
				Bounds viewPort = mapScroll.getViewportBounds();
				Bounds contentSize = contentAnchor.getBoundsInParent();

				double centerPosX = (contentSize.getWidth() - viewPort.getWidth()) * mapScroll.getHvalue() + viewPort.getWidth() / 2;
				double centerPosY = (contentSize.getHeight() - viewPort.getHeight()) * mapScroll.getVvalue() + viewPort.getHeight() / 2;

				mapScroll.setScaleX(mapScroll.getScaleX() * scaleFactor);
				mapScroll.setScaleY(mapScroll.getScaleY() * scaleFactor);
				currentScale *= scaleFactor;

				double newCenterX = centerPosX * scaleFactor;
				double newCenterY = centerPosY * scaleFactor;

				mapScroll.setHvalue((newCenterX - viewPort.getWidth() / 2) / (contentSize.getWidth() * scaleFactor - viewPort.getWidth()));
				mapScroll.setVvalue((newCenterY - viewPort.getHeight() / 2) / (contentSize.getHeight() * scaleFactor - viewPort.getHeight()));
			}

			if (scaleFactor * currentScale <= 1) {
				zoomSlider.setValue(0);

			}else if(scaleFactor * currentScale >= 5.5599173134922495) {
				zoomSlider.setValue(100);
			}else {
				zoomSlider.setValue(((currentScale - 1)/4.5599173134922495) * 100);
			}

		});

	}


	/** This is the section for key listeners.
	 *  Press Back Space for Deleting selected nodes
	 *  Press Ctrl + A for selecting all nodes
	 *  Press Ctrl + Open Bracket for zoom in
	 *  Press Ctrl + Close Bracket for zoom out
	 *  Press Shift + Right to move the view to the right
	 *  Press Shift + Left to move the view to the left
	 *  Press Shift + Up to move the view to the up
	 *  Press Shift + down to move the view to the down
	 */
	protected void setHotkeys() {
		parentBorderPane.setOnKeyPressed(e -> {
			//TODO add functionality for zooming with hotkeys
//			if (e.getCode() == KeyCode.OPEN_BRACKET && e.isControlDown()) {
//				increaseZoomButtonPressed();
//			}else if (e.getCode() == KeyCode.CLOSE_BRACKET && e.isControlDown()) {
//				decreaseZoomButtonPressed();
//			}
			if (e.getCode() == KeyCode.RIGHT && e.isShiftDown()) {
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

	protected void setZoomSliding() {
		zoomSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
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
		});
	}

	// To switch floors, call switchFloors(newFloorNumber); then this.imageViewMap.setImage(map);

	/**
	 * Rescale the map to be based off of the Scroll Pane
	 * Divides the content anchor's width and height to be based on the
	 */
	protected void initWindowResizeListener() {
		this.mapScroll.boundsInLocalProperty().addListener((observable, oldValue, newValue) -> {
			contentAnchor.setTranslateX(contentAnchor.getTranslateX() + (newValue.getMaxX() - oldValue.getMaxX())/2);
			contentAnchor.setTranslateY(contentAnchor.getTranslateY() + (newValue.getMaxY() - oldValue.getMaxY())/2);

			double ScaleX = mapScroll.getScaleX() * newValue.getMaxX()/oldValue.getMaxX();
			double ScaleY = mapScroll.getScaleY() * newValue.getMaxY()/oldValue.getMaxY();

			if(ScaleX < ScaleY) {
				mapScroll.setScaleX(ScaleX);
				mapScroll.setScaleY(ScaleX);
//				currentScale = ScaleX;
			}else{
				mapScroll.setScaleX(ScaleY);
				mapScroll.setScaleY(ScaleY);
//				currentScale = ScaleY;
			}
		});
	}

	public void fitMapSize() {

		double potentialY =
				+ mapScroll.getHeight()/2
						- contentAnchor.getHeight()/2;

		double potentialX = (mapScroll.getWidth()) / 2
				- contentAnchor.getWidth() / 2;

		contentAnchor.setTranslateX(potentialX);
		contentAnchor.setTranslateY(potentialY);

	}

	/**
	 * Initializes the global filter that will reset the timer whenever an action is performed.
	 */
	protected void initGlobalFilter() {
		this.parentBorderPane.addEventFilter(MouseEvent.ANY, e-> {
			timer.resetTimer();
		});
		this.parentBorderPane.addEventFilter(KeyEvent.ANY, e-> {
			timer.resetTimer();
		});
	}

	protected TimerTask getTimerTask() {
		return new TimerTask()
		{
			public void run() {
				setState(directory.getCaretaker().getState());
			}
		};
	}

	// place inside controller
	protected void setState(UserState state) {
		try {
			parentBorderPane.getScene().setRoot(state.getRoot());
			this.directory.logOut();
		} catch(NullPointerException e) {}
	}
}
