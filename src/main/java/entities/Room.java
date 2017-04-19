package entities;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import javax.swing.text.DefaultTextUI;

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
	// TODO: Fix room shape operations
	private static final double DEFAULT_STROKE_WIDTH = 1.5;
	private static final double RECTANGLE_WIDTH = 7;
	private static final double RECTANGLE_HEIGHT = 7;
	private static final String KIOSK_NAME = "You Are Here";
	private static final String DEFAULT_IMAGE_PATH = "/MysteryRoom.png";
	private static final int FONT_SIZE = 9;

	/* Attributes */
	private Node location;
	private String name;
	private String description;
	private Set<Professional> professionals;
	private String image; // The String path of the image for this room
	//TODO: This should be a Node and a Label, not a StackPane
	private Icon shape;

	/* Constructors */
	Room(String name, String description, String image) {
		this.location = null;
		this.name = name;
		this.description = description;
		this.professionals = new HashSet<Professional>();
		this.image = image;
		this.makeShape();
	}

	Room(String name, String description) {
		this(name, description, Room.DEFAULT_IMAGE_PATH);
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

	/** Get this room's shape, and create it if it does not exist */
	public Icon getShape() {
		if(this.shape == null) {
			this.makeShape(); // maybe move this to the constructor
		}
		return this.shape;
	}

	void setName(String name) {
		this.name = name;
	}

	void setDescription(String description) {
		this.description = description;
	}

	void setImage(String imagepath) {
		this.image = imagepath;
	}

//	public void setShape(StackPane shape) {
//		this.shape = shape;
//	}

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
	Collection<Professional> getProfessionals() {
		return new HashSet<>(this.professionals);
	}


	// TODO: Remove Room::toString; replace with custom method
	@Override
	public String toString() {
		return this.name;
	}


	private void makeShape() {
		this.makeShape(ColorScheme.DEFAULT_ROOM_STROKE_COLOR, ColorScheme.DEFAULT_ROOM_FILL_COLOR);
	}

	private void makeShape(Color stroke, Color fill) {
		if (this.location != null) {
			Rectangle shape = new Rectangle(this.location.getX(), this.location.getY(), RECTANGLE_WIDTH, RECTANGLE_HEIGHT);
//			this.shape = shape;
			shape.setStroke(stroke);
			shape.setStrokeWidth(DEFAULT_STROKE_WIDTH);
			shape.setFill(fill);

			Text text = new Text(this.location.getX(), this.location.getY(), this.name);
			text.setFont(new Font(FONT_SIZE));

			// A pane with the text on top of the shape; this is what actually represents the room
			Icon icon = new Icon(shape, text);
			this.shape = icon;
//			icon.setLayoutX(this.location.getX());
//			icon.setLayoutY(this.location.getY());
			//icon.setAlignment(Pos.TOP_LEFT);
		//	icon.setMargin(text, new Insets(0, 0, 0, RECTANGLE_WIDTH*2));
		}
	}
}
