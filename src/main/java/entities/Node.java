package entities;

import javafx.scene.shape.Circle;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;


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
	private int floor;
	private HashSet<Node> neighbors;
	private Optional<Room> room;
	private Circle circ;

	/* Default shape parameters */
	// TODO: Fix all Node shape operations
	private static final double CIRCLE_RADIUS = 5;

	public Node(double x, double y, int floor) {
		this.x = x;
		this.y = y;
		this.floor = floor;
		this.neighbors = new HashSet<>();
		this.room = Optional.empty();
	}

	// TODO: Change default floor to 0 once floor-switching works
	public Node(double x, double y) {
		this(x, y, 4);
	}

	public double getX() {
		return this.x;
	}

	public double getY() {
		return this.y;
	}

	public int getFloor() {
		return this.floor;
	}

	// TODO: Refactor Node::getRoom uses to accept an Optional, and return the Optional
	public Room getRoom() {
		return this.room.orElse(null);
	}

	/**
	 * Apply the given consumer function to this node's associated room, if present
	 *
	 * With lambdas, this allows you to pretend all nodes have a room
	 *
	 * e.g. {@code node.applyToRoom(room -> room.setName("Room 1"));}
	 *
	 * @param consumer A function that may take a single Room as its only argument
	 */
	public void applyToRoom(Consumer<? super Room> consumer) {
		this.room.ifPresent(consumer);

		// Non-Optional equivalent
//		if (this.room != null) {
//			consumer.accept(this.room);
//		}
	}

	public void setFloor(int floor) {
		this.floor = floor;
	}

	public void setRoom(Room room) {
		// if this function throws NullPointerException, you passed it a null value
		// Don't do that.
		this.room = Optional.of(room);
	}

	/** Remove this node's association with a room, if any */
	public void unsetRoom() {
		this.room = Optional.empty();
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

	/** @deprecated Use applyToRoom instead */
	@Deprecated
	public boolean containsRoom(){
		if (this.room == null) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * Get the this node's shape
	 *
	 * If it does not have a shape, create one.
	 *
	 * @return This node's shape, a Circle
	 */
	//TODO: Maybe make a Node's shape be a Shape rather than a Circle
	public Circle getShape() {
		if(this.circ == null) {
			this.makeShape(); // maybe move this to the constructor
		}
		return this.circ;
	}

	private void makeShape() {
		this.circ = new Circle(this.x, this.y, Node.CIRCLE_RADIUS);
		if (this.room.isPresent()) {
			this.circ.setFill(COLORS.ROOM.bodyColor());
			this.circ.setStroke(COLORS.ROOM.lineColor());
			this.circ.setStrokeWidth(COLORS.ROOM.strokeWidth());
		} else {
			this.circ.setFill(COLORS.NO_ROOM.bodyColor());
			this.circ.setStroke(COLORS.NO_ROOM.lineColor());
			this.circ.setStrokeWidth(COLORS.NO_ROOM.strokeWidth());
		}
	}

}
