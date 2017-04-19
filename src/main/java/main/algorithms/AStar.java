package main.algorithms;

import entities.Node;

import java.util.*;

enum AStar
		implements Algorithm
{
	/** The instance of this singleton */
	instance; // Access with AStar.instance
	private AStar() {}

	private static final String NAME = "A*";


	@Override
	public String getName() {
		return AStar.NAME;
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
	public List<Node> findPath(Node start, Node dest) throws PathNotFoundException {
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
		bestGuess.put(start, heuristic(start, dest));

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
				return AStar.makePath(pathHistory, current);
			}

			seenNodes.remove(current); // don't look at this node again later
			visitedNodes.add(current); // don't try to make paths to this node later

			// look at each neighbor of the current node
			for(Node neighbor : current.getNeighbors()){
				if (visitedNodes.contains(neighbor)) {
					continue; // skip already-visited neighbors
				}

				// get distance from the start to the neighbor.
				double guessDist = distFromStart.get(current) + heuristic(current, neighbor);

				seenNodes.add(neighbor); // make sure the neighbor is marked as seen

				// if the guess for the current neighbor is better than it was, use it
				if (guessDist < bestGuess.getOrDefault(neighbor, inf)) {
					pathHistory.put(neighbor, current); // "got to neighbor from current"
					distFromStart.put(neighbor, guessDist);
					bestGuess.put(neighbor, guessDist + neighbor.distance(dest));
				}
			}
		}
		throw new PathNotFoundException("No path exists between the given locations.");
		// return Collections.emptyList(); // TODO: replace this with some sort of "no path" indicator
	}

	private static double heuristic(Node current, Node other) {
		return current.distance(other, Algorithm.FLOOR_HEIGHT);
	}

	private static List<Node> makePath(Map<Node, Node> pathHistory, Node current){
		List<Node> finalPath = new LinkedList<>();
		finalPath.add(current);
		while (pathHistory.containsKey(current)) {
			current = pathHistory.get(current);
			finalPath.add(0, current);
		}
		return finalPath;
	}

	@Override
	public String toString() {
		return this.getName();
	}

}
