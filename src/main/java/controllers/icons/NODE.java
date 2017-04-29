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
	SELECTED(null, 2.0, Color.BEIGE, 1.5),

	DEFAULT(Color.BLUE, 1.5, Color.BLACK, 1.0),
	ROOM(Color.YELLOW, null, null, null),
	ELEVATOR(Color.FUCHSIA, null, null, null),
	PORTAL(Color.DARKORANGE, null, null, null),

	RESTRICTED(Color.LIGHTBLUE, 1.5, Color.BLACK, 1.0),
	RESTRICTED_ELEVATOR(Color.LIGHTPINK, null, null, null),
	RESTRICTED_ROOM(Color.LIGHTYELLOW, null, null, null),
	RESTRICTED_PORTAL(Color.ORANGE, null, null, null),
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
