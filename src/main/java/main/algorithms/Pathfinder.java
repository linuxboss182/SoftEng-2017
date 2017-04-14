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
	private static final double FLOOR_HEIGHT = 240;

	private Pathfinder instance = new Pathfinder();
	private Pathfinder() {}
	public Pathfinder getInstance() {
		return this.instance;
	}

	public static List<Node> findPath(Node a, Node b) {
		Algorithm alg = new AStar();
		return alg.findPath(a, b);
	}

	static double getFloorHeight() {
		return Pathfinder.FLOOR_HEIGHT;
	}
}
