package entities;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;

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

	//TODO: make this work
	public void getRoomData(Object n) {
	}

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

	/** Remove the given node or room from this directory */
	public boolean removeNodeOrRoom(Node  n) {
		return this.nodes.remove(n) || this.rooms.remove(n);
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

	// TODO: Write this method properly
	private int getHeight() {
		return 4;
	}

	public void newRoom(double x, double y, String name, String desc) {
		Room newRoom = new Room(x, y, name, desc);
		this.rooms.add(newRoom);
	}

	public void newNode(double x, double y) {
		Node newNode = new Node(x, y);
		this.nodes.add(newNode);
	}

}


