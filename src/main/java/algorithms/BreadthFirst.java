package algorithms;

import entities.Node;

import java.util.*;
import java.util.function.Function;


enum BreadthFirst
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
	public List<Node> findPath(Node start, Node dest,
	                           Function<Node, Set<Node>> nodeNeighborGetter)
	                           throws PathNotFoundException {

		Set<Node> visited = new HashSet<>(); // list nodes already visited
		Queue<Node> next = new LinkedList<>(); // list of nodes that need to be visited
		Map<Node, Node> pathHistory = new HashMap<>(); // got to keys from values
		next.add(start);

		while (! next.isEmpty()) {
			Node current = next.remove();
			visited.add(current);
			if (current == dest) {
				return makePath(pathHistory, current);
			}
			for (Node neighbor: nodeNeighborGetter.apply(current)) {
				if (!visited.contains(neighbor)) {
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

	@Override
	public String toString() {
		return this.getName();
	}
}
