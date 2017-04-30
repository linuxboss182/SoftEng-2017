package controllers.icons;

import entities.RoomType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Shape;

import entities.Directory;
import entities.Node;
import entities.Room;


// TODO: Move to entities; complete integration with Room
// How much of IconController should be exposed?

/**
 * This class manages icon modification
 * This is facade and a decorator.
 */
public class IconController
{
	private Directory directory;

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

		boolean isElevator = directory.getNodeNeighbors(node).stream()
				.anyMatch(n -> (node.getFloor() != n.getFloor()));

		boolean isPortal = directory.getNodeNeighbors(node).stream()
				.anyMatch(n -> (!node.getBuildingName().equalsIgnoreCase(n.getBuildingName())));

		if (node.isRestricted()) {
			NODE.RESTRICTED.applyTo(node.getShape());

			if (isElevator) NODE.RESTRICTED_ELEVATOR.applyTo(node.getShape());
			else if (isPortal) NODE.RESTRICTED_PORTAL.applyTo(node.getShape());
			else if (node.getRoom() != null) NODE.ROOM.applyTo(node.getShape());
		} else {
			NODE.DEFAULT.applyTo(node.getShape());

			if (isElevator) NODE.ELEVATOR.applyTo(node.getShape());
			else if (isPortal) NODE.PORTAL.applyTo(node.getShape());
			else if (node.getRoom() != null) NODE.RESTRICTED_ROOM.applyTo(node.getShape());
		}

		if (directory.getKiosk() == node.getRoom()) {
			NODE.KIOSK.applyTo(node.getShape());
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
		icon.setImage(RoomType.DEFAULT.getImage());

		room.getIcon().setImage(room.getType().getImage());
	}

	public void resetAllRooms() {
		this.startRoom = null;
		this.endRoom = null;
		this.directory.getRooms().forEach(this::resetRoom);
	}

//	private void resetAllRoomsExcept(Room keep) {
//		this.directory.getRooms().forEach(r -> {
//			if (r != keep) this.resetRoom(r);
//		});
//	}

	public void selectEndRoom(Room room) {
		if (endRoom != null) this.resetRoom(endRoom);
		endRoom = room;
		room.getIcon().setImage(room.getType().getDestImage());
	}

	public void selectStartRoom(Room room) {
		if (startRoom != null) this.resetRoom(startRoom);
		startRoom = room;
		room.getIcon().setImage(room.getType().getOriginImage());
	}
}
