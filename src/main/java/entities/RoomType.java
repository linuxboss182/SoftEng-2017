package entities;

import javafx.scene.image.Image;

/**
 *
 */
public enum RoomType
{
	DEFAULT("Default Room", "/Room.png", "/RoomOrigin.png", "/RoomDest.png"),
	KIOSK("Kiosk", "/Kiosk.png", "/KioskOrigin.png", "/KioskDest.png"),
	ELEVATOR("Elevator", "/Elevator.png", "/ElevatorOrigin.png", "/ElevatorDest.png"),
	STAIRS("Stairs", "/Stairs.png", "/StairsOrigin.png", "/StairsDest.png"),
	PORTAL("Portal", "/Room.png", "/RoomOrigin.png", "/RoomDest.png"),
	PARKING("Parking", "/Parking.png", "/ParkingOrigin.png", "/ParkingDest.png"),
	HALLWAY("Hallway", "/Room.png", "/RoomOrigin.png", "/RoomDest.png"),
	BATHROOM_M("Male Bathroom", "/Bathroom_M.png", "/Bathroom_MOrigin.png", "/Bathroom_MDest.png"),
	BATHROOM_W("Women's Bathroom", "/Bathroom_W.png", "/Bathroom_WOrigin.png", "/Bathroom_WDest.png"),
	BATHROOM_U("Unisex Bathroom", "/Bathroom_U.png", "/Bathroom_UOrigin.png", "/Bathroom_UDest.png"),
	SHOP("Gift Shop", "/Room.png", "/RoomOrigin.png", "/RoomDest.png"),
	CAFE("Cafe", "/Room.png", "/RoomOrigin.png", "/RoomDest.png"),
	;

	private String name;
	private Image image;
	private Image originImage;
	private Image destImage;

	//Constructor
	RoomType(String name, String imagePath, String originImage, String destImage) {
		this.name = name;
		try{
			this.image = new Image(imagePath);
			this.originImage = new Image(originImage);
			this.destImage = new Image(destImage);
		} catch (Exception e) {
			System.out.println("Failed to load images");
		}
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
