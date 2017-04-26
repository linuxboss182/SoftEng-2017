package controllers.shared;

import entities.icons.IconController;
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
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;

import java.util.Collection;

// TODO: Use this class more effectively
// Move stuff here when possible, remove unneeded stuff later

public abstract class MapDisplayController
{
	protected final double SCALE_DELTA = 1.1;
	final protected double zoomMin = 1;
	final protected double zoomMax = 6;
	protected double currentScale = 1;

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
	protected ListProperty<Room> listProperty = new SimpleListProperty<>();

	// default to floor 1
	protected static FloorImage floor = FloorProxy.getFloor("FAULKNER", 1);

	@FXML protected Slider zoomSlider;
	@FXML protected BorderPane parentBorderPane;

	// TODO: move shared initializaton to MDC
//	@Override
//	public void initialize(URL location, ResourceBundle resources) {
//		directory = ApplicationController.getDirectory(); //Grab the database controller from main and use it to populate our directory
//		iconController = ApplicationController.getIconController();
//	}


//	//This function takes in two nodes, displays a
//	@Deprecated
//	public void paintLine(Node start, Node finish) {
//		if (start.getFloor() == finish.getFloor()) {
//			Line line = new Line();
//			line.setStartX(start.getX());
//			line.setStartY(start.getY());
//			line.setFill(this.CONNECTION_LINE_COLOR);
//			line.setEndX(finish.getX());
//			line.setEndY(finish.getY());
//			this.lines.add(line);
//			this.botPane.getChildren().add(line);
//			line.setVisible(true);
//		}
//	}

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
		Image map = this.directory.switchFloors(floor);
		this.imageViewMap.setImage(map);
		this.redisplayMapItems();
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

//	@FXML
//	protected void increaseZoomButtonPressed() {
//		double zoomPercent = (zoomSlider.getValue()/100);
//		zoomPercent+=.2;
//		zoomPercent = (zoomPercent > 1 ? 1 : zoomPercent);
//		zoomSlider.setValue(zoomPercent*100);
//		double zoomCoefficient = zoomMin*(1 - zoomPercent) + zoomMax*(zoomPercent);
//		contentAnchor.setScaleX(zoomCoefficient);
//		contentAnchor.setScaleY(zoomCoefficient);
//	}
//
//	@FXML
//	protected void decreaseZoomButtonPressed() {
//		double zoomPercent = (zoomSlider.getValue()/100);
//		zoomPercent-=.2;
//		zoomPercent = (zoomPercent < 0 ? 0 : zoomPercent);
//		zoomSlider.setValue(zoomPercent*100);
//		double zoomCoefficient = zoomMin*(1 - zoomPercent) + zoomMax*(zoomPercent);
//		contentAnchor.setScaleX(zoomCoefficient);
//		contentAnchor.setScaleY(zoomCoefficient);
//	}

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
		this.parentBorderPane.boundsInLocalProperty().addListener((observable, oldValue,
		                                                           newValue) -> fitMapSize());
	}

	//This function resets the zoom to default and properly centers the contentAncor to the center of the map view area (mapScroll)
	private void fitMapSize() {
		double potentialScaleX = mapScroll.getViewportBounds().getWidth() / contentAnchor.getWidth(); //Gets the ratio to default to
		double potentialScaleY = mapScroll.getViewportBounds().getHeight() / contentAnchor.getHeight();

		if(potentialScaleX < potentialScaleY) { //Preserves the ratio by taking the minimum
			contentAnchor.setScaleX(potentialScaleX);
			contentAnchor.setScaleY(potentialScaleX);
			currentScale = potentialScaleX;
		} else {
			contentAnchor.setScaleX(potentialScaleY);
			contentAnchor.setScaleY(potentialScaleY);
			currentScale = potentialScaleY;
		}

		//Fixes the offset to center
		double potentialX = contentAnchor.getTranslateX() + mapScroll.localToScene(mapScroll.getViewportBounds()).getMinX() - contentAnchor.localToScene(contentAnchor.getBoundsInLocal()).getMinX();
		double potentialY = contentAnchor.getTranslateY() + mapScroll.localToScene(mapScroll.getViewportBounds()).getMinY() - contentAnchor.localToScene(contentAnchor.getBoundsInLocal()).getMinY();
		contentAnchor.setTranslateX(potentialX);
		contentAnchor.setTranslateY(potentialY);

		zoomSlider.setValue(0);
	}
}
