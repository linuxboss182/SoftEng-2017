package main.algorithms;

import entities.Node;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;


public enum DepthFirst
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
	public List<Node> findPath(Node start, Node dest)
			throws PathNotFoundException {
		Set<Node> visited = new HashSet<>();
		LinkedList<Node> path = recurse(start, dest, visited);
		path.addFirst(start);
		return path;
		//throw new PathNotFoundException("No path found between the two given nodes.");
	}


	public LinkedList<Node> recurse(Node current, Node dest, Set<Node> visited) {
		if(current == dest) {
			LinkedList<Node> output = new LinkedList<>();
			output.add(current);
			return output;
		}
		else{
			visited.add(current);
			LinkedList<Node> list = new LinkedList<>();
			for(Node n: current.getNeighbors()) {
				if(!visited.contains(n)) {
					list = recurse(n, dest, visited);
				}
				if (!list.isEmpty()) {
					list.push(n);
					return list;
				}
			}
		}
		return new LinkedList<>();
	}
}
