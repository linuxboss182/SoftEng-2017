package entities;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

// TODO: Improve documentation

/**
 * The class for a Directory
 *
 */
//TODO: I made this based off of the class diagram as best I could but there's still stuff to do
public class Directory
{

	/* Attributes */

	private Set<Node> nodes;
	private Set<Room> rooms;
	private Set<Professional> professionals;
	private Optional<Room> kiosk;

	/*
	addNode, addRoom
	removeNode, removeRoom
	addProf, removeProf

	getProfessionals, getRooms, getNodes
	 */

	/* Constructors */

	// A barren constructor for Directory, currently doesn't do anything differently than normal
	public Directory() {
		this.nodes = new HashSet<>();
		this.rooms = new HashSet<>();
		this.professionals = new TreeSet<>(); // these are sorted
		this.kiosk = Optional.empty();
	}


	/* Methods */

	public Set<Node> getNodes() {
		return new HashSet<>(this.nodes);
	}

	public Set<Room> getRooms() {
		return new HashSet<>(this.rooms);
	}

	public Set<Professional> getProfessionals() {
		return new TreeSet<>(this.professionals);
	}

	public void addNode(Node node) {
		this.nodes.add(node);
	}

	public void addRoom(Room room) {
		this.rooms.add(room);
	}

	public void addProfessional(Professional professional) {
		this.professionals.add(professional);
	}

//	public void setKiosk(Room k) {
//		this.kiosk = Optional.of(k);
//	}

	public boolean removeNode(Node node) {
		return this.nodes.remove(node);
	}

	public boolean removeRoom(Room room) {
		return this.rooms.remove(room);
	}

	/** Remove the given node and its associated room from this directory */
	public boolean removeNodeAndRoom(Node  n) {
		n.disconnectAll();
		n.applyToRoom(room -> this.rooms.remove(room));
		return this.nodes.remove(n);
	}

	public boolean removeProfessional(Professional professional) {
		return this.professionals.remove(professional);
	}


	/** return whether this directory has a kiosk */
	public boolean hasKiosk() {
		return this.kiosk.isPresent();
	}

//	/**
//	 * Get kiosk if present, or null otherwise.
//	 *
//	 * If kiosk is not present, return null
//	 *
//	 * @todo This should return Optional&lt;Room&rt;, not Room.
//	 */
//	public Room getKiosk() {
//		return this.kiosk.orElse(null);
//	}

	// TODO: We probably don't need this.
	private int getHeight() {
		return 4;
	}

	/**
	 * Create a Room and an associated Node in this directory
	 *
	 * The new room and node are created and associated with each other, and are added
	 * to this directory.
	 *
	 * @param x The x-coordinate of the new room.
	 * @param y The y-coordinate of the new room.
	 * @param name The name of the new room.
	 * @param desc A description of the new room.
	 *
	 * @return The new node.
	 */
	public Node addNewRoomNode(double x, double y, String name, String desc) {
		Room newRoom = new Room(name, desc);
		Node newNode = new Node(x, y);
		newRoom.setLocation(newNode);
		newNode.setRoom(newRoom);
		this.nodes.add(newNode);
		this.rooms.add(newRoom);
		return newNode;
	}

	/**
	 * Create a new room in this directory
	 *
	 * This does not associate the room with a node. For that, use addNewRoomNode.
	 */
	public Room addNewRoom(String name, String desc) {
		Room newRoom = new Room(name, desc);
		this.rooms.add(newRoom);
		return newRoom;
	}

	/**
	 * Create a new node in this directory
	 */
	// TODO: Add "floor" argument
	public Node addNewNode(double x, double y) {
		Node newNode = new Node(x, y);
		this.nodes.add(newNode);
		return newNode;
	}

	// TODO: Add test cases for new Directory methods like getNodesOnFloor

	/**
	 * Get a set of the nodes on the given floor
	 *
	 * @param floor The floor number to get noes for.
	 *
	 * @return A set of the nodes in this directory on the given floor.
	 */
	public Set<Node> getNodesOnFloor(int floor) {
		return this.nodes.stream()
				// Stream::filter removes elements for which the lambda returns false
				.filter(node -> node.getFloor() == floor)
				.collect(Collectors.toSet()); // make the stream back into a set
	}

	public Set<Room> getRoomsOnFloor(int floor) {
		return this.getRooms(); // TODO: CHANGE THIS
	}
}


