package entities.icons;

import javafx.scene.image.Image;

/**
 *
 */
public enum IconType
{
	STRAIGHT("STRAIGHT", new Image("/MysteryRoom.png")),
	HLEFT("HLEFT", new Image("/MysteryRoom.png")),
	SLEFT("SLEFT", new Image("/MysteryRoom.png")),
	HRIGHT("HRIGHT", new Image("/MysteryRoom.png")),
	SRIGHT("SRIGHT", new Image("/MysteryRoom.png")),
	ELEVATOR("ELEVATOR", new Image("/Elevator.png")),
	STAIRS("STAIRS", new Image("/Stairs.png")),
	PORTAL("PORTAL", new Image("/Portal.png")),
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
