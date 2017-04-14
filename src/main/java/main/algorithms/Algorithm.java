package main.algorithms;

import java.util.List;

import entities.Node;

/**
 * Interface for algorithms.
 *
 * Beyond what is shown here
 */
interface Algorithm
{
	String getName();
	List<Node> findPath(Node start, Node dest);
}
