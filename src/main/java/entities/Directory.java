package entities;

import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.Predicate;
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
	private Room kiosk;
	
	/** Comparator to allow comparing rooms by name */
	private static Comparator<Room> roomComparator = (r1, r2) -> {
		int compName = r1.getName().compareTo(r2.getName());
		if (compName != 0) return compName;
		return (r1 == r2) ? 0 : 1;
	};


	/* Constructors */

	// A barren constructor for Directory, currently doesn't do anything differently than normal
	public Directory() {
		this.nodes = new HashSet<>();
		this.rooms = new HashSet<>();
		this.professionals = new TreeSet<>(); // these are sorted
		this.kiosk = null;
	}


	/* Methods */

	/* Getters */

	public Set<Node> getNodes() {
		return new HashSet<>(this.nodes);
	}

	/**
	 * Get a copy of this directory's rooms, sorted by name
	 */
	// TODO: Maybe make Room Comparable, then make getRooms look like getProfessionals
	public Set<Room> getRooms() {
		Set<Room> rooms = new TreeSet<>(Directory.roomComparator);
		rooms.addAll(this.rooms);
		return rooms;
	}

	public SortedSet<Professional> getProfessionals() {
		return new TreeSet<>(this.professionals);
	}

	public Room getKiosk() {
		return this.kiosk;
	}

	public void setKiosk(Room k) {
		this.kiosk = k;
	}

	/* Element addition methods */

	public void addNode(Node node) {
		this.nodes.add(node);
	}

	public void addRoom(Room room) {
		this.rooms.add(room);
	}

	public void addProfessional(Professional professional) {
		this.professionals.add(professional);
	}

	/* Element removal methods */

	/** @deprecated May be restored once nodeless rooms are possible */
	@Deprecated
	private boolean removeNode(Node node) {
		node.disconnectAll();
		return this.nodes.remove(node);
	}

	private boolean removeRoom(Room room) {
		this.professionals.forEach(p -> p.removeLocation(room));
		return this.rooms.remove(room);
	}

	/** Remove the given node and its associated room from this directory */
	public boolean removeNodeAndRoom(Node  n) {
		n.disconnectAll();
		n.applyToRoom(this::removeRoom);
		return this.nodes.remove(n);
	}

	public boolean removeProfessional(Professional professional) {
		// Must check null because naturally-ordered TreeSets can't use nulls
		if (professional == null) return false;
		return this.professionals.remove(professional);
	}

	public Professional addNewProfessional(String name, String surname, String title){
		Professional newPro = new Professional(name, surname, title);
		this.addProfessional(newPro);
		return newPro;
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
	public Node addNewRoomNode(double x, double y, int floor, String name, String desc) {
		Room newRoom = new Room(name, desc);
		Node newNode = new Node(x, y, floor);
		newRoom.setLocation(newNode);
		newNode.setRoom(newRoom);
		this.nodes.add(newNode);
		this.rooms.add(newRoom);
		return newNode;
	}

	/**
	 * Add a new room with the given attributes to the given node
	 */
	public void addNewRoomToNode(Node node, String name, String desc) {
		Room room = new Room(name, desc);
		node.setRoom(room);
		room.setLocation(node);
		this.rooms.add(room);
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
	public Node addNewNode(double x, double y, int floor) {
		Node newNode = new Node(x, y, floor);
		this.nodes.add(newNode);
		return newNode;
	}

	// TODO: Add test cases for new Directory methods

	/* Filtered getters */
	/**
	 * Get a set of the nodes on the given floor
	 *
	 * @param floor The floor number to get noes for.
	 *
	 * @return A set of the nodes in this directory on the given floor.
	 */
	public Set<Node> getNodesOnFloor(int floor) {
		return this.filterNodes(node -> node.getFloor() == floor);
	}

	/**
	 * Gets a set of all of the rooms on a given floor
	 *
	 * A room's floor is determined by its associated node
	 *
	 * @param floor
	 * @return
	 */
	public Set<Room> getRoomsOnFloor(int floor) {
		return this.filterRooms(room -> room.getLocation() != null && room.getLocation().getFloor() == floor);
	}

	/**
	 * Gets all nodes in this directory that match the given predicate
	 */
	public Set<Node> filterNodes(Predicate<Node> predicate) {
		return this.nodes.stream().filter(predicate).collect(Collectors.toSet());
	}

	/**
	 * Gets all rooms in this directory that match the given predicate
	 */
	public Set<Room> filterRooms(Predicate<Room> predicate) {
		return this.rooms.stream().filter(predicate)
				.collect(Collectors.toCollection(() -> new TreeSet<>(Directory.roomComparator)));
		// Collect the filtered rooms into a TreeSet with roomComparator as the ordering function
	}

	/* Entity modification functions */

	/**
	 * Toggle the edge between the given nodes
	 */
	public void connectOrDisconnectNodes(Node n1, Node n2) {
		n1.connectOrDisconnect(n2);
	}

	/* Program logic functions */

	/** return whether this directory has a kiosk */
	public boolean hasKiosk() {
		return this.kiosk != null;
	}

	/**
	 * Determine if the rooms accessibly to the user are all connected
	 * 
	 * This only considers rooms that have locations
	 *
	 * @return Whether all rooms are connected
	 */
	public boolean roomsAreConnected() {
		// targets = all rooms with nodes
		Set<Node> targets = this.rooms.stream()
				.filter(room -> room.getLocation() != null)
				.map(Room::getLocation)
				.collect(Collectors.toSet());
		if (targets.isEmpty()) return true; // no rooms, so all are connected

		Node start = targets.stream().findAny().orElse(null);
		if (start == null) throw new RuntimeException("Impossible: had rooms, but couldn't get any from the stream");

		Set<Node> visited = new HashSet<>();
		List<Node> toVisit = new LinkedList<>();

		toVisit.add(start);

		while (! toVisit.isEmpty()) {
			Node current = toVisit.remove(0);
			visited.add(current);
			for (Node n : current.getNeighbors()) {
				if (! visited.contains(n)) {
					toVisit.add(n);
				}
			}
		}

		targets.removeAll(visited);
		return targets.isEmpty();
	}
}

