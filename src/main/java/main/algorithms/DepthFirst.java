package main.algorithms;

import entities.Node;

import java.util.*;
import java.util.function.Function;


enum DepthFirst
		implements Algorithm
{
	instance;
	private DepthFirst(){}

	private static final String NAME = "Depth First";

	@Override
	public String getName() {
		return DepthFirst.NAME;
	}

	@Override
	public List<Node> findPath(Node start, Node dest,
	                           Function<Node, Set<Node>> nodeNeighborGetter)
	                           throws PathNotFoundException {

		Set<Node> visited = new HashSet<>();
		LinkedList<Node> path = this.recurse(start, dest, visited, nodeNeighborGetter);
		if (path.isEmpty()) {
			throw new PathNotFoundException("No path found between the two connections");
		} else {
			return path;
		}
		//path.addFirst(start);
		//return path;
		//throw new PathNotFoundException("No path found between the two given nodes.");
	}


	public LinkedList<Node> recurse(Node current, Node dest, Set<Node> visited, Function<Node, Set<Node>> nodeNeighborGetter) {
		if (current == dest) {
			LinkedList<Node> output = new LinkedList<>();
			output.add(current);
			return output;
		} else {
			visited.add(current);
			LinkedList<Node> list = new LinkedList<>();
			for (Node n: nodeNeighborGetter.apply(current)) {
				if (!visited.contains(n)) {
					list = this.recurse(n, dest, visited, nodeNeighborGetter);
				}
				if (!list.isEmpty()) {
					list.push(current);
					return list;
				}
			}
		}

		return new LinkedList<>();
	}

	@Override
	public String toString() {
		return this.getName();
	}

}
