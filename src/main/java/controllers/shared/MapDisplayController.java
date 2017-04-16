package controllers.shared;

import controllers.icons.IconController;
import entities.Directory;
import entities.Node;
import entities.Professional;
import entities.Room;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public abstract class MapDisplayController
{
	// TODO: Add click+drag to select a rectangle area of nodes/a node

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

	// TODO: Make global and load from config file
	protected static final double DEFAULT_STROKE_WIDTH = 1.5;
	protected static final double RECTANGLE_WIDTH = 7;
	protected static final double RECTANGLE_HEIGHT = 7;
	protected static final Color DEFAULT_SHAPE_COLOR = Color.web("0x0000FF");
	protected static final Color DEFAULT_STROKE_COLOR = Color.BLACK;
	protected static final Color SELECTED_SHAPE_COLOR = Color.BLACK;
	protected static final Color CONNECTION_LINE_COLOR = Color.BLACK;
	protected static final Color KIOSK_COLOR = Color.YELLOW;
	protected static final String KIOSK_NAME = "You Are Here";
	protected static final double CIRCLE_RADIUS = 5;

	protected Professional selectedProf;
	protected String roomList;
	protected List<Professional> proList;
	protected List<Node> directionNodes = new ArrayList<>();
	protected ListProperty<Room> listProperty = new SimpleListProperty<>();
	protected Node destNode;

	protected Pane botPane;
	protected Pane topPane;

	// Default floor is the fourth because that's the one we emotionally attached to
	protected static int floor = 4;

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

	/**
	 * Switches the map to the given floor
	 *
	 * @param floor the floor we want to switch to
	 */
	public void switchFloors(int floor) {
		this.floor = floor;
		this.map = new FloorProxy(floor).display();
	}

	public void loadMap() {
		switchFloors(floor);
	}

	// To switch floors, call switchFloors(newFloorNumber); then this.imageViewMap.setImage(map);
}
