package main.algorithms;

import java.util.List;

import entities.Node;

public class AlgorithmTemplate
		implements Algorithm
{
	private final String name = "Algorithm Template";

	@Override
	public List<Node> findPath(Node start, Node dest) {
		return null;
	}

	@Override
	public String getName() {
		return this.name;
	}
}
