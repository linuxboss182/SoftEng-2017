package entities.icons;

import javafx.scene.image.Image;

/**
 *
 */
public enum RoomType
{
	DEFAULT("Default Room", new Image("/MysteryRoom.png")),
	KIOSK("Kiosk", new Image("/MysteryRoom.png")),
	ELEVATOR("Elevator", new Image("/Elevator.png")),
	STAIRS("Stairs", new Image("/MysteryRoom.png")),
	PORTAL("Portal", new Image("/MysteryRoom.png")),
	PARKING("Parking", new Image("/MysteryRoom.png")),
	HALLWAY("Hallway", new Image("/MysteryRoom.png")),
	BATHROOM_M("Male Bathroom", new Image("/Bathroom.png")),
	BATHROOM_F("Women's Bathroom", new Image("/Bathroom.png")),
	BATHROOM_U("Unisex Bathroom", new Image("/Bathroom.png")),
	SHOP("Gift Shop", new Image("/MysteryRoom.png")),
	CAFE("Cafe", new Image("/MysteryRoom.png")),
	;

	private String name;
	private Image image;

	//Constructor
	RoomType(String name, Image image) {
		this.name = name;
		this.image = image;
	}

	public String getName() {
		return name;
	}



}
