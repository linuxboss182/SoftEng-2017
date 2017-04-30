package entities;

import controllers.icons.IconManager;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import controllers.icons.Icon;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;


/**
 * A class for Room(s).
 *
 */
public class Room
{
	/* Attributes */
	private Node location;
	private String name;
	private String displayName;
	private String description;
	private Set<Professional> professionals;
	private RoomType type;
	private Icon icon;

	private double labelOffsetX;
	private double labelOffsetY;

	public double getLabelOffsetX() {
		return labelOffsetX;
	}

	public double getLabelOffsetY() {
		return labelOffsetY;
	}

	/* Constructors */
	Room(String name, String displayName, String description) {
		this.name = name;
		this.displayName = displayName;
		this.description = description;
		this.location = null;
		this.type = RoomType.DEFAULT;
		this.professionals = new HashSet<>();
	}

	Room(String name, String description, String displayName, double x, double y) {
		this(name, displayName, description);
		this.labelOffsetX=x;
		this.labelOffsetY=y;
	}

	Room(String name, String description, String displayName, RoomType type) {
		this(name, displayName, description);
		this.type = type;
	}

	/* Methods */

	public void setLabelOffset(double x, double y) {
		this.labelOffsetX = x - this.getLocation().getX();
		this.labelOffsetY = y - this.getLocation().getY();
		this.icon.setLabelOffset(x, y);
	}

	public String getName() {
		return this.name;
	}

	public String getDisplayName() {
		return this.displayName;
	}

	public String getDescription() {
		return this.description;
	}

	public Node getLocation() {
		return this.location;
	}

	public RoomType getType() {
		return this.type;
	}

	public Icon getIcon() {
		return this.icon;
	}

	void setName(String name) {
		this.name = name;
	}

	void setDisplayName(String displayName) {
		this.displayName = displayName;
		if (this.icon != null) {
			this.icon.updateLabel(displayName);
		}
	}

	void setDescription(String description) {
		this.description = description;
	}

	public void setIcon(Icon icon) {
		this.icon = icon;
	}

	public void setType(RoomType type) {
		this.type = type;
	}

	void setLocation(Node location) {
		this.location = location;
	}

	void unsetLocation() {
		this.location = null;
	}

	void addProfessional(Professional professional) {
		this.professionals.add(professional);
	}

	void removeProfessional(Professional professional) {
		this.professionals.remove(professional);
	}

	// TODO: Add "getProfessionalsForRoom" to Directory, returning a sorted TreeSet
	// Do we actually need rooms to have professionals? It's cheap, but we never use them.
	Collection<Professional> getProfessionals() {
		return new HashSet<>(this.professionals);
	}


	// TODO: Remove Room::toString; replace with custom method
	@Override
	public String toString() {
		return this.name;
	}
}
