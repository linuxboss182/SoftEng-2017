package controllers.icons;

import entities.Room;
import entities.RoomType;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * Class for managing room icons
 *
 * @note instance variables named "handler" in this class are functions that generate
 * handlers, not handlers themselves
 *
 * If you want to add new listeners to icons, follow these instructions:
 *
 * 1. Add a new instance variable after the existing listener variables. This should be of
 *    type BiConsumer&lt;Room, MouseEvent&gt; (a void function that takes a room and a
 *    mouse event).
 * 2. Add a setter for the variable.
 * 3. Add it to applyListeners, following the pattern of existing listeners.
 */
public class IconManager
{

	private static final double DEFAULT_LABEL_X_OFFSET = 0;
	private static final double DEFAULT_LABEL_Y_OFFSET = 0;
	private static final int FONT_SIZE = 15;
	private static final Color BACKGROUND_COLOR = Color.DARKGRAY.deriveColor(0, 0, 0, 0.5);
	private static final BackgroundFill BACKGROUND_FILL = new BackgroundFill(
			BACKGROUND_COLOR,
			new CornerRadii(2),
			new Insets(0, -2, 0, -2)
	);
	private static final Background LABEL_BACKGROUND = new Background(BACKGROUND_FILL);

	MassMap<Room, Icon> roomIcons;
	//Map<EventType<? extends Event>, Function<Room, EventHandler<? super Event>>> handlers;

	/* Listener variables */
	private BiConsumer<Room, MouseEvent> onMouseClickedOnRoomHandler;
	private BiConsumer<Room, MouseEvent> onMouseDraggedOnLabelHandler;
	private boolean showFullNamesOnHover = true;

	public IconManager() {
		this.roomIcons = new MassMap<>();
	}

	/**
	 * Prepare a mouse click handler for the main icon
	 *
	 * The handler function should produce a handler that operates on a room.
	 *
	 * @param handler A function that consumes a room and a mouse event
	 */
	public void setOnMouseClickedOnSymbol(BiConsumer<Room, MouseEvent> handler) {
		this.onMouseClickedOnRoomHandler = handler;
	}

	/**
	 * Prepare a mouse drag handler for the icons' labels
	 *
	 * @param handler A function that consumes a room, and a mouse event
	 */
	public void setOnMouseDraggedOnLabel(BiConsumer<Room, MouseEvent> handler) {
		this.onMouseDraggedOnLabelHandler = handler;
	}

	/**
	 * Set whether icons should show full room names when the mouse hovers over the room
	 *
	 * The default value is true
	 *
	 * @param value true to show full names on hover, false to never show full names
	 */
	public void showFullNamesOnHover(boolean value) {
		this.showFullNamesOnHover = value;
	}

	/**
	 * Get the objects to display for the given rooms
	 *
	 * @param rooms The rooms to get icons for
	 * @return A set of JavaFX Nodes to display
	 *
	 * @note No guarantees are made about the specific contents of the returned set
	 */
	public Set<Icon> getIcons (Collection<Room> rooms) {
		return roomIcons.computeAllIfAbsent(rooms, this::makeIcon);
	}

	/**
	 * Makes an icon and adds it to a room
	 *
	 * @param room The room we are adding the icon to
	 * @return The Icon
	 *
	 */
	public Icon makeIcon(Room room) {
		RoomType type = room.getType();
		String name = room.getDisplayName();
		Image originalImage = type.getImage();
		double x = room.getLocation().getX();
		double y = room.getLocation().getY();
		double imageHeight = originalImage.getHeight();
		double imageWidth = originalImage.getWidth();

		ImageView image = new ImageView(originalImage);
		Label label = new Label(name); // TODO: Hide the label if given empty string/null

		image.setScaleX(0.25);
		image.setScaleY(0.25);

		//Center image on the coordinates.
		image.setLayoutX(x - imageWidth/2);
		image.setLayoutY(y - imageHeight/2);

		//Label settings
		label.setLayoutX(x + room.getLabelOffsetX());
		label.setLayoutY(y + room.getLabelOffsetY());
		label.setFont(new Font(FONT_SIZE));
		label.setTextFill(Color.LIGHTGRAY);
		label.setBackground(LABEL_BACKGROUND);

		//Create Icon and link to room
		Icon icon = new Icon(room, image, label);
		this.applyListeners(room, icon);
		room.setIcon(icon);
		return icon;
	}

	/**
	 * Generate and apply listeners to the room and icon
	 *
	 * @param room The room to reference in the listeners
	 * @param icon The room's icon
	 */
	private void applyListeners(Room room, Icon icon) {
		if (showFullNamesOnHover) {
			ImageView image = icon.getImage();
			Label label = icon.getLabel();
			image.setOnMouseEntered(event -> {
				icon.updateLabel(room.getName());
			});
			image.setOnMouseExited(event -> {
				icon.updateLabel(room.getDisplayName());
			});
		}


		if (onMouseClickedOnRoomHandler != null) {
//			Shape symbol = icon.getSymbol();
			ImageView image = icon.getImage();
			image.setOnMouseClicked(event -> {
				onMouseClickedOnRoomHandler.accept(room, event);
			});
		}

		if (onMouseDraggedOnLabelHandler != null) {
			Label label = icon.getLabel();
			label.setOnMouseDragged(event -> {
				onMouseDraggedOnLabelHandler.accept(room, event);
			});
		}
	}


	private class MassMap<K, V> extends HashMap<K, V>
	{
		public MassMap() {
			super();
		}
		/**
		 * Get the values for the given keys, computing values for missing keys
		 *
		 * The computed values are storred in the map
		 */
		public Set<V> computeAllIfAbsent(Collection<K> keys,
		                                 Function<? super K, ? extends V> mappingFunction) {
			Set<V> values = new HashSet<>();
			for (K key : keys) {
				values.add(computeIfAbsent(key, mappingFunction));
			}
			return values;
		}
	}
}
