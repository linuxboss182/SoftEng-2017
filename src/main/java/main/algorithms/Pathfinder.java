package main.algorithms;

import java.util.List;

import entities.Node;

//TODO: Add documentation

/**
 * UserController class wrapping pathfinding algorithms.
 * (Currently contains algorithms.)
 */
public class Pathfinder
{

	public static List<Node> findPath(Node a, Node b) {
		Algorithm alg = new AStar();
		return alg.findPath(a, b);
	}
}
