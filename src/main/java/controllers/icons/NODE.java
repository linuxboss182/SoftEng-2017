package controllers.icons;

import javafx.scene.paint.Color;
import javafx.scene.shape.Shape;

/**
 * Color schemes for Nodes in the editor GUI
 */
enum NODE
		implements ShapeScheme
{
	DESELECT(null, 1.5, Color.BLACK),
	DEFAULT(Color.BLUE, 1.5, Color.BLACK),
	ELEVATOR(Color.FUCHSIA, null, null),
	ROOM(Color.YELLOW, null, null),
	SELECTED(null, 2.0, Color.WHITE),
	; // end of schemes
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
