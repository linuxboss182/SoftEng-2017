package icons;

import entities.Room;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;


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

	ImageView getImage() {
		return this.image;
	}

	Label getLabel() {
		return this.label;
	}

	void setImage(Image image) {
		this.image.setImage(image);
		double x = this.room.getLocation().getX();
		double y = this.room.getLocation().getY();
		double imageHeight = image.getHeight();
		double imageWidth = image.getWidth();
		this.image.setLayoutX(x - imageWidth/2);
		this.image.setLayoutY(y - imageHeight/2);
	}

	/**
	 * Update the label text for this icon, or hide the label if given an empty string
	 *
	 * @param text The text to display, or the empty string
	 */
	public void updateLabel(String text) {
		if ("".equals(text) || text == null) {
			this.label.setVisible(false);
			this.label.setText("");
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
