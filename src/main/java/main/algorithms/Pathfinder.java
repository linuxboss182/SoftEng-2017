package main.algorithms;

import java.util.Arrays;
import java.util.List;

import entities.Node;
import javafx.application.Application;
import main.ApplicationController;


/**
 * UserController class wrapping pathfinding algorithms.
 */
public class Pathfinder
{
	private Pathfinder() {} // Pathfinder can not be instantiated

	private static Algorithm strategy = AStar.instance;

	// List of all available algorithms
	private static Algorithm[] algorithmList = {
			AStar.instance,
			DepthFirst.instance,
			BreadthFirst.instance,
	};

	public static Algorithm[] getAlgorithmList() {
		return Arrays.copyOf(Pathfinder.algorithmList, Pathfinder.algorithmList.length);
	}

	/**
	 * Set the current pathfinding strategy.
	 *
	 * @param strategy An instance of the algorithm to get.
	 */
	public static void setStrategy(Algorithm strategy) {
		Pathfinder.strategy = strategy;
	}

	/**
	 * Get the currently active strategy
	 */
	public static Algorithm getStrategy() {
		return Pathfinder.strategy;
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
		return Pathfinder.strategy.findPath(a, b, ApplicationController.getDirectory()::getNodeNeighbors);
	}
}
