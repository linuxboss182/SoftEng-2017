package controllers.shared;

import controllers.icons.IconController;
import entities.*;
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

// TODO: Use this class more effectively
// Move stuff here when possible, remove unneeded stuff later

public abstract class MapDisplayController
{
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

	public static FloorImage getFloor() {
		return floor;
	}

	public static int getFloorNum() {
		return floor.getNumber();
	}

	public static String getFloorName() {
		return floor.getName();
	}

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
	public Image switchFloors(FloorImage floor) {
		MapDisplayController.floor = floor;
		return floor.display();
	}

	// To switch floors, call switchFloors(newFloorNumber); then this.imageViewMap.setImage(map);
}
