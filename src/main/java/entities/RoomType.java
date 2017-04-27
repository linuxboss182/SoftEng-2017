package entities;

import javafx.scene.image.Image;

/**
 *
 */
public enum RoomType
{
	DEFAULT("Default Room", "/MysteryRoom.png"),
	KIOSK("Kiosk", "/MysteryRoom.png"),
	ELEVATOR("Elevator", "/Elevator.png"),
	STAIRS("Stairs", "/MysteryRoom.png"),
	PORTAL("Portal", "/MysteryRoom.png"),
	PARKING("Parking", "/MysteryRoom.png"),
	HALLWAY("Hallway", "/MysteryRoom.png"),
	BATHROOM_M("Male Bathroom", "/Bathroom.png"),
	BATHROOM_F("Women's Bathroom", "/Bathroom.png"),
	BATHROOM_U("Unisex Bathroom", "/Bathroom.png"),
	SHOP("Gift Shop", "/MysteryRoom.png"),
	CAFE("Cafe", "/MysteryRoom.png"),
	;

	private String name;
	private Image image;

	//Constructor
	RoomType(String name, String imagePath) {
		this.name = name;
		this.image = new Image(imagePath);
	}

	public String getName() {
		return name;
	}

	public Image getImage() {
		return image;
	}
}
