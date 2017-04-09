package entities;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

import java.util.HashSet;
import java.util.Set;


/* DEV NOTES
This should inherit from javafx.geometry.Point2D.

Todo items in this file: (not all TODOs in this file)
 */
//TODO: Inherit from javafx.geometry.Point2D instead of reimplementing
//TODO: clean up Node.angleTo() (reimplement as adjustment for Point2D.angle())

/**
 * Represents a node in the graph, and its adjacencies.
 *
 */
public class Node
{
	private double x;
	private double y;
	// TODO: add floor or z coord
	private HashSet<Node> neighbors;
	private Room room;
	private Circle circ;

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

	public Node(double x, double y) {
		this.x = x;
		this.y = y;
		this.neighbors = new HashSet<>();
		this.room = null;
	}

	public double getX() {
		return this.x;
	}

	public double getY() {
		return this.y;
	}

	public Room getRoom() {
		return this.room;
	}

	public void setRoom(Room room) {
		this.room = room;
	}

	/** Set node coordinates */
	public void moveTo(double x, double y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * Get a copy of this node's neighbor set.
	 */
	public Set<Node> getNeighbors() {
		return new HashSet<>(this.neighbors);
	}


	/**
	 * Create an edge between this and the given node.
	 *
	 * Adds the given node to this node's neighbor set, and vice versa.
	 *
	 * @param n The node to connect to this node
	 *
	 * @return false if the edge already existed or this is the given node
	 */
	public boolean connect(Node n) {
		if (this == n) return false;
		n.neighbors.add(this);
		return this.neighbors.add(n);
	}

	/**
	 * Remove the edge between this and the given node.
	 *
	 * Removes the given node from this node's neighbor set, and vice versa.
	 *
	 * @param n The node to disconnect from this node
	 *
	 * @return false if the nodes were not connected
	 */
	public boolean disconnect(Node n) {
		n.neighbors.remove(this);
		return this.neighbors.remove(n);
	}

	/**
	 * Either connect this node to or disconnect this node from the given node.
	 *
	 * If the nodes are not connected, connect them.
	 * If they are connected, disconnect them.
	 *
	 * @param n The node to connect or disconnect.
	 */
	public void connectOrDisconnect(Node n) {
		if (this.neighbors.contains(n)) {
			this.disconnect(n);
		} else {
			this.connect(n);
		}
	}

	/**
	 * Remove any edges between this and other nodes
	 *
	 * Remove this node from the neighbor set of all nodes adjacent to it,
	 * and empty this node's neighbor set
	 */
	public void disconnectAll() { // void only because HashSet.clear() is void-typed
		this.neighbors.forEach(node -> node.neighbors.remove(this));
		this.neighbors.clear();
	}

	/**
	 * Compute the distance between this and the given node
	 *
	 * @param n The node to calculate distance to
	 *
	 * @return The distance between this and the given node
	 */
	public double distance(Node n) {
		return Math.hypot((n.y - this.y), (n.x - this.x));
	}

	/**
	 * Get angle between three Nodes.
	 *
	 * Calculates the angle of the turn taken when moving through this point
	 * from A to B. An angle of 0 indicates no turn. A positive angle indicates
	 * TODO:INCOMPLETE. A negative angle indicates TODO:INCOMPLETE.
	 *
	 * @param A The initial node for the angle
	 * @param B The terminal node for the angle
	 *
	 * @return The angle of the turn through this point when moving from A to B.
	 * returns an angle between [0 and 360) where 0 is a Right turn, 90 is Straight, 180 is Left, and 270 is Backwards
	 */
	// TODO: Determine which way is positive (answer: whichever makes the math easier)
	public double angle(Node A, Node B) {
		double AtoThis = A.angleTo(this);
		double thisToB = this.angleTo(B);
		double diff = thisToB - AtoThis;
		return (diff + 450) % 360; // Jo and Ted talked about this in a meeting on Thursday 3/30/2017
	}


	/**
	 * Calculate the angle between the two points.
	 *
	 * Right = 0. If (Node(x = 0, y = 0)).angleTo((Node(x = 4,y = 4))) gets called then
	 * the expected value should be 45 (degrees)
	 *
	 * @param n The other node.
	 *
	 * @return the angle between the nodes
	 **/
	private double angleTo(Node n) {
		if ((n.y > this.y) && (n.x > this.x)) {
			return (Math.atan((n.y - this.y)/(n.x - this.x))*180)/Math.PI;
		} else if ((n.y > this.y) && (this.x > n.x)) {
			return 180 + ((Math.atan((n.y - this.y)/(n.x - this.x))*180)/Math.PI);
		} else if ((this.y > n.y) && (this.x > n.x)) {
			return 180 + ((Math.atan((n.y - this.y)/(n.x - this.x))*180)/Math.PI);
		} else if ((this.y > n.y) && (n.x > this.x)) {
			return 360 + ((Math.atan((n.y - this.y)/(n.x - this.x))*180)/Math.PI);
		} else if ((n.y > this.y) && (n.x == this.x)) {
			return 90;
		} else if ((n.y == this.y) && (this.x > n.x)) {
			return 180;
		} else if ((this.y > n.y) && (n.x == this.x)) {
			return 270;
		} else if ((this.y == n.y) && (n.x > this.x)) {
			return 0;
		} else {
			return Double.NaN;
		}
	}

	public boolean containsRoom(){
		if(this.room == null){
			return false;
		}else{
			return true;
		}
	}

	public Circle getShape() {
		return this.circ;
	}

	public void makeShape() {
		this.circ = new Circle(this.getX(), this.getY(), this.CIRCLE_RADIUS, this.DEFAULT_SHAPE_COLOR);
		this.circ.setStroke(this.DEFAULT_STROKE_COLOR);
		this.circ.setStrokeWidth(this.DEFAULT_STROKE_WIDTH);
	}

}
