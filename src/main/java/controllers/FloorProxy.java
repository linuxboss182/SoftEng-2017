package controllers;

import javafx.scene.image.Image;

/**
 * Created by tbasl_000 on 4/8/2017.
 */
public class FloorProxy implements FloorImage
{

	private int floorNum;
	private Floor floor;

	public FloorProxy(int floor) {
		this.floorNum = floor;
	}
//  this secondary method is unecessary I believe
//	public void floorProxy(int floorNum) {
//		if(floor == null || this.floorNum != floorNum) {
//			this.floorNum = floorNum;
//			floor = new Floor(this.floorNum, floorImages[floorNum - 1]);
//		}
//	}

	/** Ensures that it is loading the correct floor, then returns an image of it
	 *
	 * @return The image of the floor we are asking for
	 */
	public Image display() {
		if(this.floor == null){
			this.floor = new Floor(this.floorNum);
		}
		return floor.display();
	}
}
