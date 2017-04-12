package entities;

import javafx.scene.paint.Color;
import javafx.scene.shape.Shape;

/**
 * This is the only class that should ever change entities' colors
 *
 * This is a mediocre facade
 */
public class ColorController
{
	/** Interface for shape color schemes */
	private interface ShapeScheme {
		void applyTo(Shape shape);
	}

	private enum NODE implements ShapeScheme {
		RESET(Color.BLUE, 1.5, Color.BLACK),
		DEFAULT(null, 1.5, Color.BLACK),
		SELECTED(null, 2.0, Color.WHITE),
		ELEVATOR(Color.FUCHSIA, null, null),
		ROOM(Color.YELLOW, null, null),
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
		void applyTo(Shape shape) {
			if (this.fill != null) shape.setFill(this.fill);
			if (this.strokeWidth != null) shape.setStrokeWidth(this.strokeWidth);
			if (this.stroke != null) shape.setStroke(this.stroke);
		}
	}

	private enum ROOM implements {
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
		void applyTo(Shape shape) {
			if (this.fill != null) shape.setFill(this.fill);
			if (this.strokeWidth != null) shape.setStrokeWidth(this.strokeWidth);
			if (this.stroke != null) shape.setStroke(this.stroke);
		}
	}


	/**
	 * Set the color of the selected node to the default
	 *
	 * There are different color schemes for:
	 * - Room-less nodes
	 * - Nodes with rooms
	 * - Nodes with connections between floors
	 *
	 * @param n The node to color
	 */
	public void resetNodeColor(Node n) {
		if (n == null) return;
		if (n.getRoom() != null) {
			NODE.ROOM.applyTo(n.getShape());
		} else { // no room
			NODE.DEFAULT.applyTo(n.getShape());
			n.getShape().setFill(COLORS.NODE.bodyColor());
			n.getShape().setStroke(COLORS.NODE.lineColor());
			n.getShape().setStrokeWidth(COLORS.NODE.strokeWidth());
		}

		if (n.getNeighbors().stream().anyMatch(other -> n.getFloor() != other.getFloor())) {
			n.getShape().setFill(COLORS.ELEVATOR.bodyColor());
		}
	}

	public void setNodeSelected() {
		if (n == null) return;

	}

}
