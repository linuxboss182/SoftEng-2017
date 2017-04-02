package entities;

import java.util.ArrayList;

// TODO: Improve documentation

/**
 * The class for a Directory
 *
 */
//TODO: I made this based off of the class diagram as best I could but there's still stuff to do
public class Directory
{

	/* Attributes */

	private ArrayList<Node> rooms;


	/* Constructors */

	// A barren constructor for Directory, currently doesn't do anything differently than normal
	public Directory() {
		this.rooms = new ArrayList<>();
	}


	/* Methods */

	//TODO: make this work
	public void getRoomData(Object n) {
	}

	/**
	 * Adds a node to the list
	 *
	 * @param node The node that is being added to the list
	 */
	public void addNode(Node node) {
		this.rooms.add(node);
	}

	/**
	 * Removes a node from the list
	 *
	 * @param node The reference to the node that is being removed. This must be the same reference as the one in the list
	 *
	 * @return true if the node has successfully been removed, false otherwise
	 */
	public boolean removeNode(Node node) {
		return this.rooms.remove(node);
	}

	// TODO: Write this method properly
	private int getHeight() {
		return 4;
	}


}
