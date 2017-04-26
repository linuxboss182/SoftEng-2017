package controllers.icons;

import entities.Room;
import entities.RoomType;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import main.ApplicationController;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/**
 * Class for managing room icons
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
	Map<EventType<?>, Function<Room, EventHandler<?>>> handlers;

	public IconManager() {
		this.roomIcons = new MassMap<>();
		this.handlers = new HashMap<>();
	}

	/**
	 * Prepare a handler to add to each icon
	 *
	 * The handler function should produce a handler that operates on a room.
	 *
	 * @param type The type of event to handle
	 * @param handler A function that takes a room and returns an event handler that
	 *                operates on that room
	 */
	public void addHandler(EventType<?> type, Function<Room, EventHandler<?>> handler) {
		this.handlers.put(type, handler);
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

		Circle circle = new Circle(room.getLocation().getX(), room.getLocation().getY(), 5);

		ROOM.DEFAULT.applyTo(circle);

		return new Icon(circle, label);
	}


	private class MassMap<K, V> extends HashMap<K, V>
	{
		/**
		 * Get the values for the given keys, computing values for missing keys
		 *
		 * The computed values are storred in the map
		 */
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
