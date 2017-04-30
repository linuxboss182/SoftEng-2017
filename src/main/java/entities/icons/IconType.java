package entities.icons;

import javafx.scene.image.Image;

public enum IconType
{
	STRAIGHT("STRAIGHT", new Image("/arrow_up.png")),
	HLEFT("HLEFT", new Image("/arrow_left.png")),
	SLEFT("SLEFT", new Image("/arrow_left.png")),
	HRIGHT("HRIGHT", new Image("/arrow_right.png")),
	SRIGHT("SRIGHT", new Image("/arrow_right.png")),
	ELEVATOR("ELEVATOR", new Image("/Elevator.png")),
	STAIRS("STAIRS", new Image("/Stairs.png")),
	PORTAL("PORTAL", new Image("/Portal.png")),
	RIGHT("RIGHT", new Image("/arrow_right.png")),
	LEFT("LEFT", new Image("/arrow_left.png")),
	BACKWARDS("LEFT", new Image("/arrow_down.png"))
	;

	private String name;
	private Image image;

	//Constructor
	IconType(String name, Image image) {
		this.name = name;
		this.image = image;
	}

	public String getName() {
		return name;
	}

	public Image getImage() {
		return image;
	}
}
