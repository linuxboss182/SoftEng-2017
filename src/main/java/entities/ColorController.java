package entities;

import javafx.scene.paint.Color;
import javafx.scene.shape.Shape;

/**
 * This is the only class that should ever change entities' colors
 *
 * This is facade and a decorator.
 */
public class ColorController
{
	/** Interface for shape color schemes */
	private interface ShapeScheme {
		public void applyTo(Shape shape);
	}

	private enum NODE implements ShapeScheme {
		RESET(Color.BLUE, 1.5, Color.BLACK),
		DEFAULT(null, 1.5, Color.BLACK),
		ELEVATOR(Color.FUCHSIA, null, null),
		ROOM(Color.YELLOW, null, null),
		SELECTED(null, 2.0, Color.WHITE),
		; // End of schemes
		private final Color fill;
		private final Double strokeWidth;
		private final Color stroke;

		NODE(Color fill, Double strokeWidth, Color stroke) {
			this.fill = fill;
			this.strokeWidth = strokeWidth;
			this.stroke = stroke;
		}

		/** Apply this color scheme to the given shape */
		public void applyTo(Shape shape) {
			if (this.fill != null) shape.setFill(this.fill);
			if (this.strokeWidth != null) shape.setStrokeWidth(this.strokeWidth);
			if (this.stroke != null) shape.setStroke(this.stroke);
		}
	}

	private enum ROOM implements ShapeScheme {
		// User UI schemes
		DEFAULT(Color.YELLOW, 1.5, Color.BLACK),
		START(Color.YELLOW, 1.5, Color.BLACK),
		END(Color.GREEN, 1.5, Color.BLACK),
		;
		private final Color fill;
		private final Double strokeWidth;
		private final Color stroke;

		ROOM(Color fill, Double strokeWidth, Color stroke) {
			this.fill = fill;
			this.strokeWidth = strokeWidth;
			this.stroke = stroke;
		}

		/** Apply this color scheme to the given shape */
		public void applyTo(Shape shape) {
			if (this.fill != null) shape.setFill(this.fill);
			if (this.strokeWidth != null) shape.setStrokeWidth(this.strokeWidth);
			if (this.stroke != null) shape.setStroke(this.stroke);
		}
	}

	private final Directory directory;

	/**
	 * Create a color controller for the given directory
	 */
	public ColorController(Directory dir) {
		this.directory = dir;
	}

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
		if (node.getNeighbors().stream().anyMatch(n -> node.getFloor() != n.getFloor())) {
			NODE.ELEVATOR.applyTo(node.getShape());
		} else if (node.getRoom() != null) {
			NODE.ROOM.applyTo(node.getShape());
		} else { // no room
			// do nothing else
		}
	}

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
}
