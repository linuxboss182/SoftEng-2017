package controllers;

import entities.Directory;
import entities.Node;
import entities.Professional;
import entities.Room;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
//import userpanel.Window;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;


/**
 * Created by s7sal on 4/7/2017.
 */
public abstract class MapDisplayController
{
	// TODO: Add click+drag to select a rectangle area of nodes/a node

	protected Image map;
	protected List<Line> lines = new ArrayList<Line>();
	protected Directory directory;
	protected Room kiosk;

	protected Node selectedNode; // you select a node by double clicking
	protected Shape selectedShape; // This and the selectedNode should be set at the same time

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

	protected int floor = 4;
	protected FloorProxy floorProxy = new FloorProxy(floor); // Default to the fourth floor because that's the only image we have in our resources


	public void setPanes(Pane botPane, Pane topPane) {
		this.botPane = botPane;
		this.topPane = topPane;
	}

	//Editor
	public void displayNodes(Set<Node> nodes) {
		this.topPane.getChildren().clear();
		nodes.forEach(node -> this.paintNode(node));
	}

	//Editor
	public void displayRooms(Set<Room> rooms) {
		this.topPane.getChildren().clear();
		rooms.forEach(room -> this.paintNode(room.getLocation()));
	}

	//Editor
	public void paintNode(Node n) {
		if(!this.topPane.getChildren().contains(n.getShape())) {
			this.topPane.getChildren().add(n.getShape());
			n.getShape().setVisible(true);
		}
	}

	//This function takes in two nodes, displays a
	public void paintLine(Node start, Node finish){
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

	/** Switches the map to the given floor
	 *
	 * @param floor the floor we want to swtich to
	 */
	public void switchFloors(int floor) {
		this.floorProxy.floorProxy(floor);
		this.map = floorProxy.display();
	}

	public void loadMap() {
		switchFloors(floor);
	}

	// To switch floors, call switchFloors(newFloorNumber); then this.imageViewMap.setImage(map);
}
