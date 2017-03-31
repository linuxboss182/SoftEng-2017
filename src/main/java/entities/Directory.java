package entities;

import java.util.ArrayList;

/**
 * The class for a Directory
 * //TODO: I made this based off of the class diagram as best I could but there's still stuff to do
 */
public class Directory
{
	// Constructors

	// A barren constructor for Directory, currently doesn't do anything differently than normal
	public Directory() {
		allRooms = new ArrayList<>();
	}

	// Methods

	//TODO: make this work
	public void getRoomData(Object n) {

	}

	/**
	 * Adds a node to the list
	 * @param newNode The node that is being added to the list
	 */
	public void addNode(Node newNode) {
		allRooms.add(newNode);
	}

	/**
	 * Removes a node from the list
	 * @param node The reference to the node that is being removed. This must be the same reference as the one in the list
	 * @return True: if the node has successfully been removed False: otherwise
	 */
	public boolean removeNode(Node node) {
		return allRooms.remove(node);
	}

	// TODO: Write this method properly
	private int getHeight() {
		return 4;
	}

	// Attributes

	private ArrayList<Node> allRooms;

}
