package controllers.icons;

import entities.Room;
import javafx.scene.Node;

import java.util.*;
import java.util.function.Function;

/**
 *
 */
public class IconManager
{
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

	private Icon makeIcon(Room room) {
		return null;
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
