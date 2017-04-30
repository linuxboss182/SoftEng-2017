package entities;

import javafx.scene.image.Image;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

// TODO: Improve documentation

/**
 * The class for a Directory
 */
public class Directory
{

	/* Attributes */

	private Set<Node> nodes;
	private Set<Room> rooms;
	private Set<Professional> professionals;
	private Room kiosk;
	private boolean loggedIn;
	private Map<String, Account> Accounts;

	private FloorImage floor;

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
		this.Accounts = new HashMap<>();
		this.professionals = new TreeSet<>(); // these are sorted
		this.kiosk = null;
		this.floor = FloorProxy.getFloor("FAULKNER", 1);
		this.loggedIn = false;
	}


	/* Methods */

	/* Getters */
	public boolean isProfessional() {
		return loggedIn;
	}

	/**
	 *
	 * @return
	 */
	public Set<Node> getNodes() {
		return new HashSet<>(this.nodes);
	}

	/**
	 * Get a copy of this directory's rooms, sorted by name
	 */
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

	public Account addAccount(String user, String password, String permission){
		Account newAccount = new Account(user, password, permission);
		this.Accounts.put(user, newAccount);
		return newAccount;
	}

	public void deleteAccount(String user){
		this.Accounts.remove(user);
	}

	public void updateKey(String newName, String oldName){
		Account tempAccount = Accounts.get(oldName);
		Accounts.remove(oldName);
		Accounts.put(newName, tempAccount);
	}

	/* Account/login functions */
	public void logIn() {
		this.loggedIn = true;
	}

	public void logOut() {
		this.loggedIn = false;
	}

	public boolean isLoggedIn() { return this.loggedIn; }

	public Map<String, Account> getAccounts(){
		return Accounts;
	}

	public String getPermissions(String username){
		return Accounts.get(username).getPermissions();
	}

	public Account getAccount(String username){
		return Accounts.get(username);
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
	public Node addNewRoomNode(double x, double y, FloorImage floor, String name, String shortName, String desc) {
		Room newRoom = new Room(name, shortName, desc);
		Node newNode = new Node(x, y, floor.getNumber(), floor.getName(), loggedIn);
		newRoom.setLocation(newNode);
		newNode.setRoom(newRoom);
		this.nodes.add(newNode);
		this.rooms.add(newRoom);
		return newNode;
	}

	/**
	 * Add a new room with the given attributes to the given node
	 */
	public void addNewRoomToNode(Node node, String name, String shortName, String desc) {
		Room room = new Room(name, shortName, desc);
		node.setRoom(room);
		room.setLocation(node);
		this.rooms.add(room);
	}

	/**
	 * Create a new room in this directory
	 *
	 * This does not associate the room with a node. For that, use addNewRoomNode.
	 */
	public Room addNewRoom(String name, String shortName, String desc) {
		Room newRoom = new Room(name, shortName, desc);
		this.rooms.add(newRoom);
		return newRoom;
	}

	/**
	 * Create a new room in this directory
	 *
	 * This does not associate the room with a node. For that, use addNewRoomNode.
	 */
	public Room addNewRoom(String name, String shortName, String desc, double labelX, double labelY) {
		Room newRoom = new Room(name, desc, shortName, labelX, labelY);
		this.rooms.add(newRoom);
		return newRoom;
	}

	/**
	 * Create a new node in this directory
	 */
	public Node addNewNode(double x, double y, FloorImage floor) {
		if (floor == null) throw new RuntimeException("Tried to create node with null floor");
		Node newNode = new Node(x, y, floor.getNumber(), floor.getName(), loggedIn);
		this.nodes.add(newNode);
		return newNode;
	}

	//use this only for DB loading from CSV
	public Node addNewNode(double x, double y, int floor, String buildingName) {
		Node newNode = new Node(x, y, floor, buildingName, loggedIn);
		this.nodes.add(newNode);
		return newNode;
	}

	public Node addNewNode(double x, double y, int floor, String buildingName, boolean isRestricted) {
		Node newNode = new Node(x, y, floor, buildingName, isRestricted);
		this.nodes.add(newNode);
		return newNode;
	}

	@Deprecated
	public Node addNewNode(double x, double y, int floor) {
		return this.addNewNode(x, y, floor, "NO BUILDING");
	}

	/* Filtered getters */
	/**
	 * Get a set of the nodes on the given floor
	 *
	 * @param floor The floor number to get noes for.
	 *
	 * @return A set of the nodes in this directory on the given floor.
	 */
	public Set<Node> getNodesOnFloor(FloorImage floor) {
		return this.filterNodes(node ->
				(node.getFloor() == floor.getNumber())
						&&
				node.getBuildingName().equalsIgnoreCase(floor.getName())
		);
	}

	/**
	 * Gets a set of all of the rooms on a given floor
	 *
	 * A room's floor is determined by its associated node
	 *
	 * @note Only this function, getUserRooms, and getNodeNeighbors filter by permissions.
	 */
	public Set<Room> getRoomsOnFloor() {
		return this.filterRooms(room -> (room.getLocation() != null)
				&& (room.getLocation().getFloor() == this.floor.getNumber())
				&& room.getLocation().getBuildingName().equalsIgnoreCase(this.floor.getName())
				&& (! room.getLocation().isRestricted() || this.loggedIn));
	}

	/**
	 * Get all rooms accessible by the current user
	 *
	 * @note Only this function, getRoomsOnFloor and getNodeNeighbors natively filter by permissions
	 */
	public Set<Room> getUserRooms() {
		return this.filterRooms(room -> (room.getLocation() != null)
				&& (!room.getLocation().isRestricted()
				    || this.isLoggedIn()));
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

	public void connectNodes(Node n1, Node n2) {
		n1.connect(n2);
	}

	public void updateRoom(Room room, String name, String shortName, String description) {
		room.setName(name);
		room.setDisplayName(shortName);
		room.setDescription(description);
	}

	public void setRoomLocation(Room room, Node node) {
		room.setLocation(node);
		node.setRoom(room);
	}

	/**
	 * Switches the map to the given floor
	 *
	 * @param floor the floor we want to switch to
	 */
	public Image switchFloors(FloorImage floor) {
		this.floor = floor;
		return this.floor.display();
	}

	public FloorImage getFloor() {
		return this.floor;
	}

	public int getFloorNum() {
		return this.floor.getNumber();
	}

	public String getFloorName() {
		return this.floor.getName();
	}

	public void unsetRoomLocation(Room room) {
		Node n = room.getLocation();
		if (n != null) {
			room.unsetLocation();
			n.unsetRoom();
		}
	}

	public void unsetNodeRoom(Node node) {
		node.applyToRoom(Room::unsetLocation);
		node.unsetRoom();
	}

	public void addRoomToProfessional(Room room, Professional professional) {
		professional.addLocation(room);
		room.addProfessional(professional);
	}

	public void removeRoomFromProfessional(Room room, Professional professional) {
		professional.removeLocation(room);
		room.removeProfessional(professional);
	}

	public void addNewElevatorUp(Node node) {
		if (node == null) return;

		this.addNewElevatorUp(node, this.floor.getNumber()+1);
	}

	private void addNewElevatorUp(Node node, int floorNum) {
		if (node == null) return;

		Set<Node> neighbors = node.getNeighbors();
		neighbors.removeIf(n -> n.getFloor() != floorNum);
		if (! neighbors.isEmpty()) {
			neighbors.forEach(n -> this.addNewElevatorUp(n, floorNum+1));
		} else {
			if (node.getRoom() == null) {
				this.addNewRoomToNode(node, "", "", "Elevator to floor above");
			}
			node.getRoom().setType(RoomType.ELEVATOR);

			FloorImage targetFloor = FloorProxy.getFloor(this.getFloorName(), floorNum);
			if (targetFloor != null) {
				Node n = this.addNewRoomNode(node.getX(), node.getY(), targetFloor, "",
						"", "Elevator to floor below");
				this.connectNodes(node, n);
			}
		}
	}

	public void addNewElevatorDown(Node node) {
		if (node == null) return;

		this.addNewElevatorDown(node, this.floor.getNumber()-1);
	}

	private void addNewElevatorDown(Node node, int floorNum) {
		if (node == null) return;

		Set<Node> neighbors = node.getNeighbors();
		neighbors.removeIf(n -> n.getFloor() != floorNum);
		if (! neighbors.isEmpty()) {
			neighbors.forEach(n -> this.addNewElevatorUp(n, floorNum-1));
		} else {
			if (node.getRoom() == null) {
				this.addNewRoomToNode(node, "", "", "Elevator to floor below");
			}
			node.getRoom().setType(RoomType.ELEVATOR);

			FloorImage targetFloor = FloorProxy.getFloor(this.getFloorName(), floorNum);
			if (targetFloor != null) {
				Node n = this.addNewRoomNode(node.getX(), node.getY(), targetFloor, "",
						"", "Elevator to floor above");
				this.connectNodes(node, n);
				System.out.println("elevator down");
			}
		}
	}

	/* Program logic functions */

	/**
	 * Gets the login-aware neighbors of the given node
	 *
	 * @param node The node to get neighbors for
	 * @return The currently-available neighbors of the node
	 */
	public Set<Node> getNodeNeighbors(Node node) {
		Set<Node> neighbors = node.getNeighbors();
		if (! this.loggedIn) {
			neighbors.removeIf(Node::isRestricted);
		}
		return neighbors;
	}


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

