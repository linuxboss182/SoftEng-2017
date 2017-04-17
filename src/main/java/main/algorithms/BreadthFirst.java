package main.algorithms;

import entities.Node;

import java.util.*;

/**
 * Created by rober on 4/16/2017.
 */
public enum BreadthFirst
		implements Algorithm
{
	instance;
	private BreadthFirst(){}

	private static final String NAME = "Breadth First";

	@Override
	public String getName() {
		return BreadthFirst.NAME;
	}

	@Override
	public List<Node> findPath(Node start, Node dest)
			throws PathNotFoundException {
		// list nodes already visited
		Set<Node> visited = new HashSet<>();
		// list of nodes that need to be visited
		Queue<Node> next = new LinkedList<>();
		next.add(start);
		while (! next.isEmpty()){
			Node current = next.remove();
			if(current == dest){
				return makePath();
			}
			for (Node neighbor: current.getNeighbors()){
				if (!visited.contains(neighbor)){
					next.add(neighbor);
				}
			}
		}
		throw new PathNotFoundException("No path exists between the two given locations");
	}

	private List<Node> makePath() {
		return null;
	}
}
