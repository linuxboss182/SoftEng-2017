package controllers.icons;

import entities.Room;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Shape;


/**
 * Class for room Icons displayed to users
 */
public class Icon
		extends Group
{
	private Room room;
	@Deprecated
	private Shape symbol;
	private Label label;
	private ImageView image;
	private double labelOffsetX = 0;
	private double labelOffsetY = 0;

	Icon(ImageView image, Label label) {
		this(null, image, label);
	}
	Icon(Room room, ImageView image, Label label) {
		super(image, label);
		this.image = image;
		this.label = label;
	}

	@Deprecated
	public Icon(Shape symbol, Label label) {
		this(null, symbol, label);
	}

	public Icon(Room room, Shape symbol, Label label) {
		super(symbol, label);
		this.room = room;
		this.symbol = symbol;
		this.label = label;
	}

	@Deprecated
	Icon(Shape symbol) {
		this(symbol, null);
	}

	@Override
	public void relocate(double x, double y) {
		this.image.relocate(x, y);
		this.label.relocate(x, y);
	}

	@Deprecated
	public Shape getSymbol() {
		return this.symbol;
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

	@Deprecated
	void setSymbol(Shape symbol) {
		this.symbol = symbol;
	}

	@Deprecated
	void setLabel(Label label) {
		this.label = label;
	}

	/** @deprecated Use relocate() instead */
	@Deprecated
	public void moveTo(double x, double y) {
		this.image.setLayoutX(x);
		this.image.setLayoutY(y);
		this.label.setLayoutX(x + this.labelOffsetX);
		this.label.setLayoutY(y + this.labelOffsetY);
	}

	public void setLabelOffset(double x, double y) {
		this.labelOffsetX = x;
		this.labelOffsetY = y;
		this.label.setTranslateX(x);
		this.label.setTranslateY(y);
		// TODO: if the above lines work, remove the lines below, and the labelOffset fields
//		this.label.setLayoutX(this.label.getLayoutX() + this.labelOffsetX);
//		this.label.setLayoutY(this.label.getLayoutY() + this.labelOffsetY);
	}
}
