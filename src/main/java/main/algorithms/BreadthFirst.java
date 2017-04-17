package main.algorithms;

import entities.Node;

import java.util.List;

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
		return null;
	}
}
