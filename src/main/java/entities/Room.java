package entities;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.util.Optional;

/**
 * A class for Room(s).
 *
 */
public class Room
{
	// TODO: Fix room shape operations
	protected static final double DEFAULT_STROKE_WIDTH = 1.5;
	protected static final double RECTANGLE_WIDTH = 7;
	protected static final double RECTANGLE_HEIGHT = 7;
	protected static final Color DEFAULT_SHAPE_COLOR = Color.web("0x0000FF");
	protected static final Color DEFAULT_STROKE_COLOR = Color.BLACK;
	protected static final Color SELECTED_SHAPE_COLOR = Color.BLACK;
	protected static final Color CONNECTION_LINE_COLOR = Color.BLACK;
	protected static final Color KIOSK_COLOR = Color.YELLOW;
	protected static final String KIOSK_NAME = "You Are Here";
	private static final String DEFAULT_IMAGE_PATH = "/MysteryRoom.png";

	/* Attributes */
	private Node location;
	private String name;
	private String description;
	private String image; // The String path of the image for this room
	private javafx.scene.Node shape;

	/* Constructors */
	public Room(String name, String description, String image) {
		this.location = null;
		this.name = name;
		this.description = description;
		this.image = image;
		this.makeShape();
	}

	public Room(String name, String description) {
		this(name, description, Room.DEFAULT_IMAGE_PATH);
	}

	// TODO: Remove this constructor in favor of association with existing nodes
	@Deprecated
	public Room(double x, double y, String name, String description, String image) {
		this(name, description, image);
		this.location = new Node(x, y);
	}

	// TODO: Remove this constructor in favor of association with existing nodes
	@Deprecated
	public Room(double x, double y) {
		this(x, y, "Anonymous Room", "A Room with no name or special description.", Room.DEFAULT_IMAGE_PATH);
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

	public Node getLocation() {
		return this.location;
	}


	public void setName(String name) {
		this.name = name;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setImage(String imagepath) {
		this.image = imagepath;
	}

	public void setLocation(Node location) {
		this.location = location;
	}

	public void unsetLocation() {
		this.location = null;
	}

	// TODO: Remove Room::toString; replace with custom method
	@Override
	public String toString() {
		return this.name;
	}

	public javafx.scene.Node getShape() {
		if(this.shape == null) {
			this.makeShape(); // maybe move this to the constructor
		}
		return this.shape;
	}

	private void makeShape() {
		makeShape(ColorScheme.DEFAULT_ROOM_STROKE_COLOR, ColorScheme.DEFAULT_ROOM_FILL_COLOR);
	}

	private void makeShape(Color stroke, Color fill) {
		if(this.location != null) {
			Rectangle shape = new Rectangle(this.location.getX(), this.location.getY(), RECTANGLE_WIDTH, RECTANGLE_HEIGHT);

			shape.setStroke(stroke);
			shape.setStrokeWidth(DEFAULT_STROKE_WIDTH);
			if (this.getName().equalsIgnoreCase(KIOSK_NAME)) {
				shape.setFill(KIOSK_COLOR);
			} else {
				shape.setFill(fill);
			}

			Text text = new Text(this.location.getX(), this.location.getY(), this.name);
			text.setFont(new Font(15));

			// A pane with the text on top of the shape; this is what actually represents the room
			StackPane stackPane = new StackPane(shape, text);
			this.shape = stackPane;
			stackPane.setLayoutX(this.location.getX());
			stackPane.setLayoutY(this.location.getY());
			stackPane.setAlignment(Pos.TOP_LEFT);
			stackPane.setMargin(text, new Insets(0, 0, 0, RECTANGLE_WIDTH));
		}
	}

	public void setShapeColors(Color stroke, Color fill) {
		this.makeShape(stroke, fill);
	}
}
