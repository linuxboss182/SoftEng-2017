package main.algorithms;

import java.util.List;

import entities.Node;

/**
 * Interface for algorithms.
 */
interface Algorithm
{
	static final double FLOOR_HEIGHT = 240;

	String getName();
	List<Node> findPath(Node start, Node dest);
}
