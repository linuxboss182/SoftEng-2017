package controllers.icons;

import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Shape;


/**
 * Color schemes for Room icons
 */
enum ROOM
		//implements ShapeScheme
{
	// User UI schemes
	//DEFAULT(Color.YELLOW, 1.5, Color.BLACK),
	//KIOSK(null, 2.0, Color.RED),
	//START(Color.BLUE, null, null),
	//END(Color.RED, null, null),
	//ELEVATOR(Color.FUCHSIA, null, null),
	; // end of schemes
	private final Color fill;
	private final Double strokeWidth;
	private final Color stroke;

	ROOM(Color fill, Double strokeWidth, Color stroke) {
		this.fill = fill;
		this.strokeWidth = strokeWidth;
		this.stroke = stroke;
	}
}
