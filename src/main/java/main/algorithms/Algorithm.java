package main.algorithms;

import java.util.List;

import entities.Node;

/**
 * Interface for algorithms.
 * 
 * Besides the 
 */
interface Algorithm
{
	static final double FLOOR_HEIGHT = 240;

	String getName(); // Algorithms need a display name.
	List<Node> findPath(Node start, Node dest) throws PathNotFoundException; // Strategy execution.
}
