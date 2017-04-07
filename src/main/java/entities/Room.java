package entities;

/**
 * A class for Room(s).
 *
 */
//TODO: I wasn't able to tell based on the diagram if the Room would have a node or a node would have a room...
public class Room
		extends Node
{
	private static final String DEFAULT_IMAGE_PATH = "/MysteryRoom.png";

	/* Attributes */
	private String name;
	private String description;
	private String image; // The String path of the image for this room

	/* Constructors */
	public Room(double x, double y, String name, String description, String image) {
		super(x, y);
		this.name = name;
		this.description = description;
		this.image = image;
	}

	public Room(double x, double y, String name, String description) {
		this(x, y, name, description, Room.DEFAULT_IMAGE_PATH);
	}

	public Room(double x, double y) {
		this(x, y, "Anonymous Room", "A Room with no name or special description.");
	}

	/* Methods */

	public String getName() {
		return this.name;
	}

	public String getDescription() {
		return this.description;
	}

	public String getImage() {
		return this.image;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setImage(String image) {
		this.image = image;
	}

	@Override
	public String toString() { return this.name; }
}
