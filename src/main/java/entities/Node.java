package entities;

import java.util.HashSet;

/* DEV NOTES

getX and getY should eventually be replaced with getCoordinates or
something similar; we should never need to get just one of the two.

 */

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

}
