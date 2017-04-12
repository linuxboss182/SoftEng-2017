package controllers.icons;

import entities.Directory;
import entities.Node;
import entities.Room;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

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

	/**
	 * Create a color controller for the given directory
	 */
	public IconController(Directory dir) {
		this.directory = dir;
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
		NODE.RESET.applyTo(node.getShape());

		// Set elevator colors
		if (node.getNeighbors().stream().anyMatch(n -> node.getFloor() != n.getFloor())) {
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
	 * Select one node and deselect all other nodes
	 */
	public void selectSingleNode(Node node) {
		this.deselectAllNodes();
		NODE.SELECTED.applyTo(node.getShape());
	}

	/**
	 * Deselect all nodes in the directory
	 */
	public void deselectAllNodes() {
		for (Node n : this.directory.getNodes()) {
			this.resetNode(n);
		}
	}


	/* Methods for Rooms */
	// (incomplete)

	/**
	 * Set the shape of the icon for the given room
	 *
	 * @note This creates a new shape whenever this is called; this is the intended
	 *       behavior, but is likely to change in future iterations.
	 */
	// TODO: Pop some of this out into an IconBuilder class
	// TODO: Replace StackPane use with ImageView and a Label
	// TODO: Don't create a new javafx Node every time
	// TODO: Actually use IconController for Rooms
	private void resetRoom(Room room) {
		if (room == null || room.getLocation() == null) return;

		// TODO: Use javafx.scene.control.Label instead of Text
		Text label = new Text(room.getName());
		label.setFont(new Font(IconController.LABEL_FONT_SIZE));

		javafx.scene.Node icon;
		if (room.getName().equalsIgnoreCase("YOU ARE HERE")) {
			Rectangle iconShape = new Rectangle(room.getLocation().getX(), room.getLocation().getY(),
					IconController.ROOM_RECTANGLE_WIDTH, IconController.ROOM_RECTANGLE_HEIGHT);
			ROOM.DEFAULT.applyTo(iconShape);
			ROOM.KIOSK.applyTo(iconShape);
			icon = iconShape;
		} else if (room.getDescription().equalsIgnoreCase("BATHROOM")) {
			Image iconimg = new Image(IconController.BATHROOM_ICON_PATH);
			double width = iconimg.getWidth();
			icon = new ImageView(iconimg);
		} else if (room.getDescription().equalsIgnoreCase("ELEVATOR")) {
			Image iconimg = new Image(IconController.ELEVATOR_ICON_PATH);
			double width = iconimg.getWidth();
			icon = new ImageView(iconimg);
		} else {
			Rectangle iconShape = new Rectangle(room.getLocation().getX(), room.getLocation().getY(),
					IconController.ROOM_RECTANGLE_WIDTH, IconController.ROOM_RECTANGLE_HEIGHT);
			ROOM.DEFAULT.applyTo(iconShape);
			icon = iconShape;
		}
		room.setShape(new StackPane(icon, label));
	}

	public void resetAllRooms() {
		this.directory.getRooms().forEach(this::resetRoom);
	}

	// TODO: Finish implementation
	public void selectStartRoom(Room room) {
		if (room == null) return; // TODO: remove; we shouldn't need this check
		this.resetAllRooms();
//		ROOM.START.applyTo(SOMETHING);
	}

	public void setElevatorIcon(Room room) {
		//TODO: Implement in iteration 3
	}

	public void setBathroomIcon(Room room) {
		//TODO: Implement in iteration 3
	}
}
