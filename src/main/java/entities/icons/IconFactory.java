package entities.icons;

import entities.Room;
import entities.RoomType;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

/**
 *
 */
public class IconFactory
{
	private static final double DEFAULT_LABEL_X_OFFSET = 0;
	private static final double DEFAULT_LABEL_Y_OFFSET = 0;
	private static final int FONT_SIZE = 9;
	private static final Color BACKGROUND_COLOR = Color.DARKGRAY.deriveColor(0, 0, 0, 0.5);
	private static final BackgroundFill BACKGROUND_FILL = new BackgroundFill(
			BACKGROUND_COLOR,
			new CornerRadii(0),
			new Insets(0, -2, 0, -2)
	);
	private static final Background LABEL_BACKGROUND = new Background(BACKGROUND_FILL);


	//Takes in a room and creates an icon at the room's position
	public Icon makeIcon(Room room) {
		RoomType type = room.getType();
		String name = room.getDisplayName();
		double x = room.getLocation().getX();
		double y = room.getLocation().getY();
		ImageView image = new ImageView(type.getImage());
		Label label = new Label(name);

		image.setLayoutX(x);
		image.setLayoutY(y);
		label.setLayoutX(x + DEFAULT_LABEL_X_OFFSET);
		label.setLayoutY(y + DEFAULT_LABEL_Y_OFFSET);
		label.setFont(new Font(FONT_SIZE));
		label.setTextFill(Color.LIGHTGRAY);
		label.setBackground(LABEL_BACKGROUND);

		return new Icon(image, label);
	}
}
