package entities;

import java.util.HashSet;

/* DEV NOTES

getX and getY should eventually be replaced with getCoordinates or
something similar; we should never need to get just one of the two.

Todo items in this file: (not all TODOs in this file)
 */
//TODO: implement Node.connect()
//TODO: implement Node.disconnect()
//TODO: implement Node.disconnectAll()
//TODO: implement Node.distance()
//TODO: implement Node.angle()

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
	}

	public int getX() {
		return this.x;
	}

	public int getY() {
		return this.y;
	}

	/** Set node coordinates */
	public moveTo(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public boolean connect(Node n) {
		return false;
	}

	public boolean disconnect(Node n) {
		return false;
	}

	public boolean disconnectAll(Node n) {
		return false;
	}

	public double distance(Node n) {
		return 0;
	}

	public double angle(Node A, Node B) {
		return 0;
	}

}
