package algorithms;

import java.util.Collections; //remove when implementing findPath(a,b)
import java.util.List;

import entities.Node;

//TODO: Add documentation

/**
 * Controller class wrapping pathfinding algorithms.
 * (Currently contains algorithms.)
 */
public class Pathfinder
{
	private Node start;
	private Node destination;

	public void setOrigin(Node start) {
		this.start = start;
	}

	public void setDestination(Node destination) {
		this.destination = destination;
	}

	// Method for pathfinding through the hospital
	public static List<Node> findPath(Node start, Node destination) {
		return Collections.emptyList();
	}

	public List<Node> findPath(Node destination) {
		return Pathfinder.findPath(this.start, destination);
	}

	public List<Node> findPath() {
		return Pathfinder.findPath(this.start, this.destination);
	}

}
