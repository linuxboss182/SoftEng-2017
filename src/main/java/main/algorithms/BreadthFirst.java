package main.algorithms;

import entities.Node;

import java.util.*;


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
		//get to key from value
		Map<Node, Node> pathHistory = new HashMap<>();
		next.add(start);
		while (! next.isEmpty()){
			Node current = next.remove();
			visited.add(current);
			if(current == dest){
				return makePath(pathHistory, current);
			}
			for (Node neighbor: current.getNeighbors()){
				if (!visited.contains(neighbor)) {
					neighbor.applyToRoom(r -> System.out.println(r.getName()));
					pathHistory.put(neighbor, current);
					next.add(neighbor);
				}
			}
		}
		throw new PathNotFoundException("No path exists between the two given locations");
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
}
