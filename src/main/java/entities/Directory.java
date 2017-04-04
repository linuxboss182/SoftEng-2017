package entities;

import java.util.Collection;
import java.util.Set;
import java.util.HashSet;

// TODO: Improve documentation

/**
 * The class for a Directory
 *
 */
//TODO: I made this based off of the class diagram as best I could but there's still stuff to do
public class Directory
{

	/* Attributes */

	private HashSet<Node> nodes;
	private HashSet<Room> rooms;
	private HashSet<Professional> professionals;


	/* Constructors */

	// A barren constructor for Directory, currently doesn't do anything differently than normal
	public Directory() {
		this.nodes = new HashSet<>();
		this.rooms = new HashSet<>();
		this.professionals = new HashSet<>();
	}


	/* Methods */

	//TODO: make this work
	public void getRoomData(Object n) {
	}

	/**
	 * Returns the list of nodes in the directory
	 */
	public Set<Node> getNodeSet() {
		return this.nodes;
	}

	/**
	 * Adds a node to the list
	 *
	 * @param node The node that is being added to the list
	 */
	public void addNode(Node node) {
		this.nodes.add(node);
	}

	public void addRoom(Room room) {
		this.rooms.add(room);
	}

	/**
	 * Removes a node from the list
	 *
	 * @param node The reference to the node that is being removed. This must be the same reference as the one in the list
	 *
	 * @return true if the node has successfully been removed, false otherwise
	 */
	public boolean removeNode(Node node) {
		return this.nodes.remove(node);
	}

	public boolean removeRoom(Room room) {
		return this.rooms.remove(room);
	}

	// TODO: Write this method properly
	private int getHeight() {
		return 4;
	}

}
