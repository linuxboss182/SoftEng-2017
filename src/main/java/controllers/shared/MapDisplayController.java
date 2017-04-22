package controllers.shared;

import controllers.icons.IconController;
import entities.*;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

// TODO: Use this class more effectively
// Move stuff here when possible, remove unneeded stuff later

public abstract class MapDisplayController
{
	protected final double SCALE_DELTA = 1.1;
	protected double SCALE_TOTAL;

	@FXML
	public AnchorPane contentAnchor;
	@FXML
	protected ImageView imageViewMap;
	@FXML
	protected ScrollPane mapScroll;

	protected Image map;
	protected List<Line> lines = new ArrayList<Line>();
	protected static Directory directory;
	protected static IconController iconController;

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

	protected Pane botPane;
	protected Pane topPane;

	// default to floor 1
	protected static FloorImage floor = FloorProxy.getFloor("FAULKNER", 1);

	@FXML
	protected Slider zoomSlider;

	public void setPanes(Pane botPane, Pane topPane) {
		this.botPane = botPane;
		this.topPane = topPane;
	}


	//This function takes in two nodes, displays a
	@Deprecated
	public void paintLine(Node start, Node finish) {
		if (start.getFloor() == finish.getFloor()) {
			Line line = new Line();
			line.setStartX(start.getX());
			line.setStartY(start.getY());
			line.setFill(this.CONNECTION_LINE_COLOR);
			line.setEndX(finish.getX());
			line.setEndY(finish.getY());
			this.lines.add(line);
			this.botPane.getChildren().add(line);
			line.setVisible(true);
		}
	}

	/**
	 * Display any edges between any of the given nodes
	 *
	 * @param nodes A collection of nodes to draw edges between
	 */
	public void redrawEdges(Collection<Node> nodes) {
	}

	protected void setScrollZoom() {
		this.contentAnchor.setOnScroll(new EventHandler<ScrollEvent>() {
			@Override
			public void handle(ScrollEvent event) {
				event.consume();
				System.out.println("HERE");
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
	}

	// To switch floors, call switchFloors(newFloorNumber); then this.imageViewMap.setImage(map);
}
