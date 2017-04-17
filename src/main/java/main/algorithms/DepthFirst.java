package main.algorithms;

import entities.Node;

import java.util.List;

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
		return null;
	}
}
