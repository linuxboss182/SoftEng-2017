package controllers.icons;

import javafx.scene.shape.Shape;

import entities.Directory;
import entities.Node;
import entities.Room;

// TODO: Move to entities; complete integration with Room
// How much of IconController should be exposed?

/**
 * This is the only class that should ever change entities' colors
 *
 * This is facade and a decorator.
 */
public class IconController
{
	private static final double LABEL_FONT_SIZE = 15;
	private static final String BATHROOM_ICON_PATH = "/Bathroom.png";
	private static final String ELEVATOR_ICON_PATH = "/Elevator.png";

	private static final double ROOM_RECTANGLE_WIDTH = 7;
	private static final double ROOM_RECTANGLE_HEIGHT = 7;

	private final Directory directory;

	// Keeping state here is not ideal, but simpliflies usage immensely
	private Room startRoom;
	private Room endRoom;

	/**
	 * Create a color controller for the given directory
	 */
	public IconController(Directory dir) {
		this.directory = dir;
		this.startRoom = null;
		this.endRoom = null;
	}


	/* Private methods for nodes */

	/**
	 * Set the color of the selected node to the default
	 *
	 * There are different color schemes for:
	 * - Room-less nodes
	 * - Nodes with rooms
	 * - Nodes with connections between floors
	 *
	 * @param node The node to color
	 */
	private void resetNode(Node node) {
		if (node == null) return;
		NODE.DEFAULT.applyTo(node.getShape());

		// Set elevator colors
		// TODO: Use a different color for portals
		if (node.getNeighbors().stream()
				.anyMatch(n -> (node.getFloor() != n.getFloor())
				|| !node.getBuildingName().equalsIgnoreCase(n.getBuildingName()))) {
			NODE.ELEVATOR.applyTo(node.getShape());
		} else if (node.getRoom() != null) {
			NODE.ROOM.applyTo(node.getShape());
		} else { // no room
			// do nothing else
		}
	}

	/* Public methods for Nodes */

	/**
	 * Reset the colors of all nodes in the directory
	 */
	public void resetAllNodes() {
		this.directory.getNodes().forEach(this::resetNode);
	}

	/**
	 * Reset the colors of a single node
	 */
	public void resetSingleNode(Node node) {
		this.resetNode(node);
	}

	/**
	 * Select one node and deselect all other nodes
	 */
	public void selectSingleNode(Node node) {
		this.resetAllNodes();
		NODE.SELECTED.applyTo(node.getShape());
	}

	/**
	 * Select one node without deselection other nodes
	 */
	public void selectAnotherNode(Node node) {
		NODE.SELECTED.applyTo(node.getShape());
	}

	/**
	 * Deselect all nodes in the directory
	 */
	public void deselectAllNodes() {
		this.resetAllNodes();
	}


	/* Methods for Rooms */

	private void resetRoom(Room room) {
		if (room == null || room.getLocation() == null) return;

		Icon icon = room.getIcon();
		Shape shape = icon.getSymbol();

		ROOM.DEFAULT.applyTo(shape);

		if (room == this.directory.getKiosk()) {
			ROOM.KIOSK.applyTo(shape);
		} else if (room.getDescription().equalsIgnoreCase("ELEVATOR")) {
			ROOM.ELEVATOR.applyTo(shape);
		}

		if (room == this.endRoom) {
			ROOM.END.applyTo(shape);
		} else if (room == this.startRoom) {
			ROOM.START.applyTo(shape);
		}
	}

	public void resetAllRooms() {
		this.startRoom = null;
		this.endRoom = null;
		this.directory.getRooms().forEach(this::resetRoom);
	}

	private void resetAllRoomsExcept(Room keep) {
		this.directory.getRooms().forEach(r -> {
			if (r != keep) this.resetRoom(r);
		});
	}

	public void selectEndRoom(Room room) {
		this.endRoom = room;
		this.resetAllRoomsExcept(this.startRoom);
	}

	public void selectStartRoom(Room room) {
		this.startRoom = room;
		this.resetAllRoomsExcept(this.endRoom);
	}
}
