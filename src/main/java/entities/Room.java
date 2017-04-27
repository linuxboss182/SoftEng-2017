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
	// TODO: Clean up room icon operations
	private static final double DEFAULT_STROKE_WIDTH = 1.5;
	private static final double RECTANGLE_WIDTH = 7;
	private static final double RECTANGLE_HEIGHT = 7;
	private static final double CIRCLE_RADIUS = 5;
	private static final String KIOSK_NAME = "You Are Here";
	private static final String DEFAULT_IMAGE_PATH = "/MysteryRoom.png";
	private static final int FONT_SIZE = 9;
	private static final Color BACKGROUND_COLOR = Color.DARKGRAY.deriveColor(0, 0, 0, 0.5);
	private static final BackgroundFill BACKGROUND_FILL = new BackgroundFill(BACKGROUND_COLOR,
	                                                      new CornerRadii(0),
	                                                      new Insets(0, -2, 0, -2));
	private static final Background LABEL_BACKGROUND = new Background(BACKGROUND_FILL);


	/* Attributes */
	private Node location;
	private String name;
	private String displayName;
	private String description;
	private Set<Professional> professionals;
	private RoomType type;
	private Icon icon;

	@Deprecated
	private Group adminShape;
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


	/* Methods */

	public void setLabelOffset(double x, double y) {
		this.labelOffsetX = x;
		this.labelOffsetY = y;
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
		if ("ELEVATOR".equalsIgnoreCase(this.description)) {
			return RoomType.ELEVATOR;
		} else {
			return type;
		}
	}

	public Icon getIcon() {
		return this.icon;
	}

	void setName(String name) {
		this.name = name;
	}

	void setDisplayName(String displayName) {
		this.displayName = displayName;
		if ((this.icon != null) && (this.icon.getLabel() != null)) {
			this.icon.getLabel().setText(displayName);
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


	public Group getAdminSideShape() {
		this.makeAdminSideShape(); // maybe move this to the constructor
		return this.adminShape;
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
				this.icon = null;
				this.makeAdminSideShape();
			});

			text.setOnMouseReleased(e->{
				System.out.println("released a label");
			});
			// A pane with the text on top of the shape; this is what actually represents the room
			Group icon = new Group(shape, text);
			this.adminShape = icon;
//			icon.setLayoutX(this.location.getX());
//			icon.setLayoutY(this.location.getY());
			//icon.setAlignment(Pos.TOP_LEFT);
			//	icon.setMargin(text, new Insets(0, 0, 0, RECTANGLE_WIDTH*2));
		}
	}

//	private void makeIcon() {
//		this.makeIcon(ColorScheme.DEFAULT_ROOM_STROKE_COLOR, ColorScheme.DEFAULT_ROOM_FILL_COLOR);
//	}
//
//	private void makeIcon(Color stroke, Color fill) {
//		if (this.location != null) {
//			Circle shape = new Circle(this.location.getX(), this.location.getY(), CIRCLE_RADIUS);
//
//			Label label = new Label(this.name);
//			label.setLayoutX(this.location.getX());
//			label.setLayoutY(this.location.getY());
//			label.setFont(new Font(FONT_SIZE));
//
//			// A pane with the text on top of the icon; this is what actually represents the room
//			this.icon = new Icon(shape, label);
//		}
//	}

}
