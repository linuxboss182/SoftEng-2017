package controllers.icons;

import entities.Room;
import entities.RoomType;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

/**
 *
 */
public class IconManager
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
	MassMap<Room, Node> roomIcons;

	public IconManager() {
		this.roomIcons = new MassMap<>();
	}

	/**
	 * Get the objects to display for the given rooms
	 *
	 * @param rooms The rooms to get icons for
	 * @return A set of JavaFX Nodes to display
	 *
	 * @note No guarantees are made about the specific contents of the returned set
	 */
	public Set<Node> getIcons (Collection<Room> rooms) {
		return roomIcons.computeAllIfAbsent(rooms, this::makeIcon);
	}

	public Icon makeIcon(Room room) {
		RoomType type = room.getType();
		String name = room.getDisplayName();
		double x = room.getLocation().getX();
		double y = room.getLocation().getY();
		ImageView image = new ImageView(type.getImage());
		Label label = new Label(name); // TODO: Hide the label if given empty string/null

		image.setLayoutX(x);
		image.setLayoutY(y);
		label.setLayoutX(x + DEFAULT_LABEL_X_OFFSET);
		label.setLayoutY(y + DEFAULT_LABEL_Y_OFFSET);
		label.setFont(new Font(FONT_SIZE));
		label.setTextFill(Color.LIGHTGRAY);
		label.setBackground(LABEL_BACKGROUND);

		return new Icon(image, label);
	}

	private class MassMap<K, V> extends HashMap<K, V>
	{
		public Set<V> computeAllIfAbsent(Collection<K> keys,
		                                 Function<? super K, ? extends V> mappingFunction) {
			Set<V> values = new HashSet<>();
			for (K key : keys) {
				if (containsKey(key)) {
					values.add(get(key));
				} else {
					values.add(mappingFunction.apply(key));
				}
			}
			return values;
		}
	}
}
