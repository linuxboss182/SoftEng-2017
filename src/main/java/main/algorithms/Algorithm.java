package main.algorithms;

import java.util.List;

import javafx.util.StringConverter;

import entities.Node;

/**
 * Interface for algorithms.
 *
 * Besides the methods here, Algorithms should implement toString.
 */
public interface Algorithm
{
	double FLOOR_HEIGHT = 240;

	StringConverter<Algorithm> ALGORITHM_STRING_CONVERTER = new StringConverter<Algorithm>()
	{
		@Override
		public String toString(Algorithm alg) {
			return alg.getName();
		}

		@Override
		public Algorithm fromString(String string) {
			return null;
		}
	};

	String getName(); // Algorithms need a display name.
	List<Node> findPath(Node start, Node dest) throws PathNotFoundException; // Strategy execution.
}
