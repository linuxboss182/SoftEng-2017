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
	PORTAL("Exit/Entrance", "/Portal.png", "/PortalOrigin.png", "/PortalDest.png"),
	PARKING("Parking", "/Parking.png", "/ParkingOrigin.png", "/ParkingDest.png"),
	HALLWAY("Hallway", "/Room.png", "/RoomOrigin.png", "/RoomDest.png"),
	BATHROOM("Bathroom", "/Bathroom_U.png", "/Bathroom_UOrigin.png", "/Bathroom_UDest.png"),
	SHOP("Gift Shop", "/Shop.png", "/ShopOrigin.png", "/ShopDest.png"),
	CAFE("Cafe/Food", "/Cafe.png", "/CafeOrigin.png", "/CafeDest.png"),
	NONE("NO ROOM", "/MysteryRoom.png", "/MysteryRoom.png", "/MysteryRoom.png"),
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

	@Override
	public String toString() {
		return this.getName();
	}
}
