package entities;

import javafx.scene.image.Image;

/**
 *
 */
public enum RoomType
{
	DEFAULT("Default Room", "/MysteryRoom.png", "/MysteryRoomOrigin.png", "/MysteryRoomDest.png"),
	KIOSK("Kiosk", "/MysteryRoom.png", "/MysteryRoomOrigin.png", "/MysteryRoomDest.png"),
	ELEVATOR("Elevator", "/Elevator.png", "/ElevatorOrigin.png", "/ElevatorDest.png"),
	STAIRS("Stairs", "/MysteryRoom.png", "/Elevator.png", "/Bathroom.png"),
	PORTAL("Portal", "/MysteryRoom.png", "/Elevator.png", "/Bathroom.png"),
	PARKING("Parking", "/MysteryRoom.png", "/Elevator.png", "/Bathroom.png"),
	HALLWAY("Hallway", "/MysteryRoom.png", "/Elevator.png", "/Bathroom.png"),
	BATHROOM_M("Male Bathroom", "/Bathroom.png", "/Elevator.png", "/Bathroom.png"),
	BATHROOM_F("Women's Bathroom", "/Bathroom.png", "/Elevator.png", "/MysteryRoom.png"),
	BATHROOM_U("Unisex Bathroom", "/Bathroom.png", "/Elevator.png", "/MysteryRoom.png"),
	SHOP("Gift Shop", "/MysteryRoom.png", "/Elevator.png", "/Bathroom.png"),
	CAFE("Cafe", "/MysteryRoom.png", "/Elevator.png", "/Bathroom.png"),
	;

	private String name;
	private Image image;
	private Image originImage;
	private Image destImage;

	//Constructor
	RoomType(String name, String imagePath, String originImage, String destImage) {
		this.name = name;
		this.image = new Image(imagePath);
		this.originImage = new Image(originImage);
		this.destImage = new Image(destImage);
	}

	public String getName() {
		return name;
	}

	public Image getImage() {
		return image;
	}

	public Image getOriginImage() {
		return originImage;
	}

	public Image getDestImage() {
		return destImage;
	}
}
