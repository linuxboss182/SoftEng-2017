package main.algorithms;

import entities.Node;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Created by rober on 4/16/2017.
 */
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
		LinkedList<Node> path = new LinkedList<>();
		path.push(start);
		while(path.peek() != dest){
//			recurse;
		}
		throw new PathNotFoundException("todo: add ebtter error message");
	}
	/*
	public list recurse(current, dest, visited) {
		if current == dest:
			output = [current]
			return output
		else:
			visited.add(current)
			Node n = null;
			for n in current.getNeighbors():
				if n not in visited:
					list = recurse(n, dest, visited)
					if list not empty:
						list.push(n)
						return list
			return empty list
	}
	*/
}
