package entities;

import java.util.HashSet;

/* DEV NOTES

getX and getY should eventually be replaced with getCoordinates or
something similar; we should never need to get just one of the two.

Todo items in this file: (not all TODOs in this file)
 */
//TODO: implement Node.distance()
//TODO: implement Node.angle()
//TODO: clean up Node.angleTo()

/**
 * Represents a node in the graph, and its adjacencies.
 *
 */
public class Node
{
	private int x;
	private int y;
	private HashSet<Node> adjacencies;

	public Node(int x, int y) {
		this.x = x;
		this.y = y;
		this.adjacencies = new HashSet<Node>();
	}

	public int getX() {
		return this.x;
	}

	public int getY() {
		return this.y;
	}

	/** Set node coordinates */
	public void moveTo(int x, int y) {
		this.x = x;
		this.y = y;
	}


	/**
	 * Create an edge between this and the given node.
	 *
	 * Adds the given node to this node's adjacencies, and vice versa.
	 *
	 * @param n The node to connect to this node
	 *
	 * @return false if the edge already existed
	 */
	public boolean connect(Node n) {
		n.adjacencies.add(this);
		return this.adjacencies.add(n);
	}

	/**
	 * Remove the edge between this and the given node.
	 *
	 * Removes the given node from this node's adjacencies, and vice versa.
	 *
	 * @param n The node to disconnect from this node
	 *
	 * @return false if the nodes were not connected
	 */
	public boolean disconnect(Node n) {
		n.adjacencies.remove(this);
		return this.adjacencies.remove(n);
	}

	/**
	 * Remove any edges between this and other nodes
	 *
	 * Remove this node from the adjacencies of all nodes adjacent to it,
	 * and empty this node's adjacencies
	 */
	public void disconnectAll() { // void only because HashSet.clear() is void-typed
		this.adjacencies.stream().forEach(node -> node.adjacencies.remove(this));
		this.adjacencies.clear();
	}

	/**
	 * Compute the distance between this and the given node
	 *
	 * @param n The node to calculate distance to
	 *
	 * @return The distance between this and the given node
	 */
	public double distance(Node n) {
		double x2 = n.getX();
		double y2 = n.getY();
		return Math.sqrt(Math.pow((y2 - this.y), 2) + Math.pow((x2 - this.x), 2));
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
	 * returns an angle betweeon [0 and 360) where 0 is a Right turn, 90 is Straight, 180 is Left, and 270 is Backwards
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
		double x2 = n.getX();
		double y2 = n.getY();
		if (y2 > this.y && x2 > this.x) {
			return (Math.atan((y2 - this.y)/(x2 - this.x))*180)/Math.PI;
		} else if (y2 > this.y && this.x > x2) {
			return 180 + (Math.atan((y2 - this.y)/(x2 - this.x))*180)/Math.PI;
		} else if (this.y > y2 && this.x > x2) {
			return 180 + (Math.atan((y2 - this.y)/(x2 - this.x))*180)/Math.PI;
		} else if (this.y > y2 && x2 > this.x) {
			return 360 + (Math.atan((y2 - this.y)/(x2 - this.x))*180)/Math.PI;
		} else if (y2 > this.y && x2 == this.x) {
			return 90;
		} else if (y2 == this.y && this.x > x2) {
			return 180;
		} else if (this.y > y2 && x2 == this.x) {
			return 270;
		} else if (this.y == y2 && x2 > this.x) {
			return 0;
		} else {
			return Double.NaN;
		}
	}

}
