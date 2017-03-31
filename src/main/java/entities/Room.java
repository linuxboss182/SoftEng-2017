package entities;

/**
 * A class for Room(s).
 * TODO: I wasn't able to tell based on the diagram if the Room would have a node or a node would have a room...
 */
public class Room
{
	// Constructors
	public Room(String name, String description, String image) {
		this.name = name;
		this.description = description;
		this.image = image;
	}

	public Room(String name, String description) {
		this(name, description, DEFAULT_IMAGE_PATH);
	}

	public Room() {
		this("Anonymous Room", "A Room with no name or special description.");
	}

	// Methods

	// Attributes
	private String name;
	private String description;
	private String image; // The String path of the image for this room
	private static final String DEFAULT_IMAGE_PATH = "/src/main/resources/MysteryRoom.png"; // TODO: make sure this path works
}
