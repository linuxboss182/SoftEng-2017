package algorithms;

import java.util.*;

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
	public List<Node> findPath(Node start, Node dest) {
		Double inf = Double.POSITIVE_INFINITY;
		// This is the list of Nodes that have already been visted by the algorithm
		Set<Node> visitedNodes = new HashSet<>();

		// This is the list of Nodes that have not yet been looked at by  the algorithm,
		// starting with the origin point
		Set<Node> seenNodes = new HashSet<>(Arrays.asList(start));

		// This is a map container that allows for backtracing of the path so it can be
		// reconstructed later
		Map<Node, Node> pathHistory = new HashMap<>();

		// This is a map container that holds the distance of getting to that node from start
		Map<Node, Double> distFromStart = new HashMap<>();
		distFromStart.put(start, 0.0);

		// This is a map container which holds the guessed total distance from current to dest
		Map<Node, Double> bestGuess = new HashMap<>();
		bestGuess.put(start, start.distance(dest));

		Node current = null;

		// main algorithm where path is determined
		while (! seenNodes.isEmpty()){
			// set current to a shortest distance Node in seenNodes list
			for(Node n : seenNodes){
				if (current == null ||
						bestGuess.getOrDefault(n, inf) <= bestGuess.getOrDefault(current, inf)) {
					current = n;
				}
			}

			if(current == dest){
				return this.makePath(pathHistory, current);
			}
			// remove current node from start list
			seenNodes.remove(current);
			// add current to the visited nodes list so we don't touch it again later
			visitedNodes.add(current);
			// look through the neighbors of the current node
			for(Node neighbor: current.getNeighbors()){
				// if neighbor has already been visited, then ignore it
				if(visitedNodes.contains(neighbor)){
					continue;
				}
				// get distance from the start to the neighbor.
				double guessDist = distFromStart.get(current) + current.distance(neighbor);
				// add neighbor the the seenNodes list
				seenNodes.add(neighbor);
				if(guessDist < bestGuess.getOrDefault(neighbor, inf)){
					// if you found a better guess, then overwrite maps
					pathHistory.put(neighbor, current);
					distFromStart.put(neighbor, guessDist);
					bestGuess.put(neighbor, guessDist + neighbor.distance(dest));
				}
			}
		}
		return Collections.emptyList();
	}

	private List<Node> makePath(Map<Node, Node> pathHistory,Node current){
		List<Node> finalPath = new LinkedList<>();
		finalPath.add(current);
		while(pathHistory.containsKey(current)){
			current = pathHistory.get(current);
			finalPath.add(0, current);
		}
		return finalPath;
	}

	public List<Node> findPath(Node destination) {
		return Pathfinder.findPath(this.start, destination);
	}

	public List<Node> findPath() {
		return Pathfinder.findPath(this.start, this.destination);
	}

}
