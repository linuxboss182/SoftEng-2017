package controllers.icons;

import javafx.scene.paint.Color;
import javafx.scene.shape.Shape;

/**
 * Color schemes for Nodes in the editor GUI
 */
enum NODE
		implements ShapeScheme
{
	/* fill, strokeWidth, stroke, scale */
	DESELECT(null, 1.5, Color.BLACK, 1.0),
	DEFAULT(Color.BLUE, 1.5, Color.BLACK, 1.0),
	ELEVATOR(Color.FUCHSIA, null, null, null),
	ROOM(Color.YELLOW, null, null, null),
	SELECTED(null, 2.0, Color.WHITE, 1.5),
	; // end of schemes
	private final Color fill;
	private final Double strokeWidth;
	private final Color stroke;
	private final Double scale;

	NODE(Color fill, Double strokeWidth, Color stroke, Double scale) {
		this.fill = fill;
		this.strokeWidth = strokeWidth;
		this.stroke = stroke;
		this.scale = scale;
	}

	/** Apply this color scheme to the given shape */
	public void applyTo(Shape shape) {
		if (this.fill != null) shape.setFill(this.fill);
		if (this.strokeWidth != null) shape.setStrokeWidth(this.strokeWidth);
		if (this.stroke != null) shape.setStroke(this.stroke);
		if (this.scale != null) {
			shape.setScaleX(this.scale);
			shape.setScaleY(this.scale);
		}
	}
}
