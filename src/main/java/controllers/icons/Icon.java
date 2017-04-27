package controllers.icons;

import entities.Room;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Shape;


/**
 * Class for room Icons displayed to users
 */
public class Icon
		extends Group
{
	private Room room;
	private Label label;
	private ImageView image;
	private double labelOffsetX = 0;
	private double labelOffsetY = 0;

	Icon(Room room, ImageView image, Label label) {
		super(image, label);
		this.room = room;
		this.image = image;
		this.label = label;
	}

	@Override
	public void relocate(double x, double y) {
		this.image.relocate(x, y);
		this.label.relocate(x, y);
	}

	public ImageView getImage() {
		return this.image;
	}

	public void setImage(Image image) {
		this.image.setImage(image);
		System.out.println(this.room);
		System.out.println(this.room.getLocation());
		System.out.println(this.room.getLocation().getX());
		double x = this.room.getLocation().getX();
		double y = this.room.getLocation().getY();
		double imageHeight = image.getHeight();
		double imageWidth = image.getWidth();
		this.image.setLayoutX(x - imageWidth/2);
		this.image.setLayoutY(y - imageHeight/2);
	}

	@Deprecated
	public Label getLabel() {
		return this.label;
	}

	/**
	 * Set the label text for this icon, or hide the label if given an empty string
	 *
	 * @param text The text to display, or the empty string
	 */
	void setLabelText(String text) {
		if ("".equals(text)) {
			this.label.setVisible(false);
		} else {
			this.label.setVisible(true);
			this.label.setText(text);
		}
	}

	public void setLabelOffset(double x, double y) {
		this.labelOffsetX = x;
		this.labelOffsetY = y;
//		this.label.setTranslateX(x);
//		this.label.setTranslateY(y);
		this.label.setLayoutX(x);
		this.label.setLayoutY(y);
		// TODO: if the above lines work, remove the lines below, and the labelOffset
		// fields
//		this.label.setLayoutX(this.label.getLayoutX() + this.labelOffsetX);
//		this.label.setLayoutY(this.label.getLayoutY() + this.labelOffsetY);
	}
}
