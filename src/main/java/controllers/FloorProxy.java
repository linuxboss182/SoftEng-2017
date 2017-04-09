package controllers;

import javafx.scene.image.Image;

/**
 * Created by tbasl_000 on 4/8/2017.
 */
public class FloorProxy implements FloorImage
{
	public FloorProxy(int floor) {
		this.floorNum = floor;
	}

	public void floorProxy(int floorNum) {
		if(floor == null || this.floorNum != floorNum) {
			this.floorNum = floorNum;
			floor = new Floor(this.floorNum, floorImages[floorNum - 1]);
		}
	}

	/** Ensures that it is loading the correct floor, then returns an image of it
	 *
	 * @return The image of the floor we are asking for
	 */
	public Image display() {
		floorProxy(floorNum);
		return floor.display();
	}

	private int floorNum;
	private Floor floor;
	private String[] floorImages = {"/MysteryRoom.png", "/MysteryRoom.png", "/MysteryRoom.png", "/4_thefourthfloor.png", "/MysteryRoom.png", "/MysteryRoom.png", "/MysteryRoom.png"};
}
