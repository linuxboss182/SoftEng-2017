package main.algorithms;

import java.nio.file.Path;
import java.util.List;

import entities.Node;


/**
 * UserController class wrapping pathfinding algorithms.
 */
public class Pathfinder
{
	private Pathfinder() {} // Pathfinder can not be instantiated

	private static Algorithm strategy = AStar.instance;

	/**
	 * Set the current pathfinding strategy.
	 *
	 * @param strategy An instance of the algorithm to get.
	 */
	public static void setStrategy(Algorithm strategy) {
		Pathfinder.strategy = strategy;
	}

	/**
	 * Run the current algorithm to find a path between the given nodes
	 *
	 * @param a The node to start the path from
	 * @param b The node the path should lead to
	 *   
	 * @return A list of nodes representing the path
	 */
	public static List<Node> findPath(Node a, Node b) throws PathNotFoundException {
		return Pathfinder.strategy.findPath(a, b);
	}
}
