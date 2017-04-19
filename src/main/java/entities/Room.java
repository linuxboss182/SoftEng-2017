package entities;

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
	private double labelOffsetX;
	private double labelOffsetY;

	/* Constructors */
	Room(String name, String description, String image) {
		this.location = null;
		this.name = name;
		this.description = description;
		this.professionals = new HashSet<Professional>();
		this.image = image;
		this.makeUserSideShape();
	}

	Room(String name, String description) {
		this(name, description, Room.DEFAULT_IMAGE_PATH);
	}

	/* Methods */

	public void setLabelOffset(double x, double y) {
		this.labelOffsetX = x;
		this.labelOffsetY = y;
	}

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

	/** Get this room's shape to be displayed to a non-admin, and create it if it does not exist */
	public Icon getUserSideShape() {
		this.makeUserSideShape(); // maybe move this to the constructor
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


	private void makeUserSideShape() {
		this.makeUserSideShape(ColorScheme.DEFAULT_ROOM_STROKE_COLOR, ColorScheme.DEFAULT_ROOM_FILL_COLOR);
	}


	private void makeUserSideShape(Color stroke, Color fill) {
		if (this.location != null) {
			Rectangle shape = new Rectangle(this.location.getX(), this.location.getY(), RECTANGLE_WIDTH, RECTANGLE_HEIGHT);
//			this.shape = shape;
			shape.setStroke(stroke);
			shape.setStrokeWidth(DEFAULT_STROKE_WIDTH);
			shape.setFill(fill);

			Text text = new Text(this.location.getX(), this.location.getY(), this.name);
			text.setFont(new Font(FONT_SIZE));
			text.setX(shape.getX() + labelOffsetX);
			text.setY(shape.getY() + labelOffsetY);
			// A pane with the text on top of the shape; this is what actually represents the room
			Icon icon = new Icon(shape, text);
			this.shape = icon;
//			icon.setLayoutX(this.location.getX());
//			icon.setLayoutY(this.location.getY());
			//icon.setAlignment(Pos.TOP_LEFT);
		//	icon.setMargin(text, new Insets(0, 0, 0, RECTANGLE_WIDTH*2));
		}
	}

	public Icon getAdminSideShape() {
		this.makeAdminSideShape(); // maybe move this to the constructor
		return this.shape;
	}

	private void makeAdminSideShape() {
		this.makeAdminSideShape(ColorScheme.DEFAULT_ROOM_STROKE_COLOR, ColorScheme.DEFAULT_ROOM_FILL_COLOR);
	}

	private void makeAdminSideShape(Color stroke, Color fill) {
		if (this.location != null) {
			Rectangle shape = new Rectangle(this.location.getX(), this.location.getY(), RECTANGLE_WIDTH, RECTANGLE_HEIGHT);
//			this.shape = shape;
			shape.setStroke(stroke);
			shape.setStrokeWidth(DEFAULT_STROKE_WIDTH);
			shape.setFill(fill);

			Text text = new Text(this.location.getX(), this.location.getY(), this.name);
			text.setFont(new Font(FONT_SIZE));
			text.setX(shape.getX() + labelOffsetX);
			text.setY(shape.getY() + labelOffsetY);
			/**
			 * This is so you can move the labels, changing the labelOffsetX and Y
			 */
			text.setOnMousePressed(e->{
				System.out.println("pressed a label");
			});

			text.setOnMouseDragged(e->{
				this.labelOffsetX = e.getX() - shape.getX();
				this.labelOffsetY = e.getY() - shape.getY();
				this.shape = null;
				this.makeAdminSideShape();
			});

			text.setOnMouseReleased(e->{
				System.out.println("released a label");
			});
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
