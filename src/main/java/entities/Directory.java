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
		this.professionals = new HashSet<>();
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
		return new HashSet<>(this.professionals);
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

	public boolean removeNode(Node node) {
		return this.nodes.remove(node);
	}

	public boolean removeRoom(Room room) {
		return this.rooms.remove(room);
	}

	public boolean removeProfessional(Professional professional) {
		return this.professionals.remove(professional);
	}

	// TODO: Write this method properly
	private int getHeight() {
		return 4;
	}

}
