package main;

import java.nio.file.Path;
import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.LinkedList;
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;

import entities.Node;

//TODO: Add documentation

/**
 * UserController class wrapping pathfinding algorithms.
 * (Currently contains algorithms.)
 */
public class Pathfinder
{
	private static final double FLOOR_HEIGHT = 240;

	private Node start;
	private Node destination;

	public void setOrigin(Node start) {
		this.start = start;
	}

	public void setDestination(Node destination) {
		this.destination = destination;
	}

	public List<Node> findPath() {
		return Pathfinder.findPath(this.start, this.destination);
	}

	public List<Node> findPath(Node destination) {
		return Pathfinder.findPath(this.start, destination);
	}

	/**
	 * Find a shortest path between two nodes
	 *
	 * @param start The node the path should end at.
	 * @param dest  The node the path should end at.
	 *
	 * @return A list of the nodes traversed in the path, in order, or an empty list if
	 *         no path is found
	 */
	public static List<Node> findPath(Node start, Node dest)throws NullPointerException {
		if(start == null || dest == null){
			throw(new NullPointerException());
		}
		Double inf = Double.POSITIVE_INFINITY;
		// list of Nodes that have already been visited
		Set<Node> visitedNodes = new HashSet<>();

		// list of Nodes that have been found but not examined
		Set<Node> seenNodes = new HashSet<>();
		seenNodes.add(start);

		// map that allows for backtracing of the path so it can be reconstructed
		Map<Node, Node> pathHistory = new HashMap<>();

		// map that holds the distance from a Node to the start Node
		Map<Node, Double> distFromStart = new HashMap<>();
		distFromStart.put(start, 0.0);

		// map that holds the guessed total distance from a Node to the destination
		Map<Node, Double> bestGuess = new HashMap<>();
		bestGuess.put(start, Pathfinder.heuristic(start, dest));

		Node current;

		/* Main loop for algorithm */
		while (! seenNodes.isEmpty()) {
			current = null; // otherwise last iteration's current will cause problems

			// set current to a shortest distance Node in seenNodes list
			for (Node n : seenNodes) {
				if ((current == null) ||
						(bestGuess.getOrDefault(n, inf) <= bestGuess.getOrDefault(current, inf))) {
					current = n;
				}
			}

			if (current == dest) {
				return Pathfinder.makePath(pathHistory, current);
			}

			seenNodes.remove(current); // don't look at this node again later
			visitedNodes.add(current); // don't try to make paths to this node later

			// look at each neighbor of the current node
			for(Node neighbor : current.getNeighbors()){
				if (visitedNodes.contains(neighbor)) {
					continue; // skip already-visited neighbors
				}

				// get distance from the start to the neighbor.
				double guessDist = distFromStart.get(current) + Pathfinder.heuristic(current, neighbor);

				seenNodes.add(neighbor); // make sure the neighbor is marked as seen

				// if the guess for the current neighbor is better than it was, use it
				if (guessDist < bestGuess.getOrDefault(neighbor, inf)) {
					pathHistory.put(neighbor, current); // "got to neighbor from current"
					distFromStart.put(neighbor, guessDist);
					bestGuess.put(neighbor, guessDist + neighbor.distance(dest));
				}
			}
		}
		return Collections.emptyList(); // TODO: replace this with some sort of "no path" indicator
	}

	private static double heuristic(Node current, Node other) {
		return current.distance(other, Pathfinder.FLOOR_HEIGHT);
	}

	private static List<Node> makePath(Map<Node, Node> pathHistory,Node current){
		List<Node> finalPath = new LinkedList<>();
		finalPath.add(current);
		while (pathHistory.containsKey(current)) {
			current = pathHistory.get(current);
			finalPath.add(0, current);
		}
		return finalPath;
	}
}
