package controllers.shared;

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

	/** checks to see if the floor has been loaded, then returns the image attached to that
	 *  floor.
	 *
	 * @return The image of the floor we are asking for
	 */
	public Image display() {
		if(this.floor == null){
			this.floor = new Floor(this.floorNum);
		}
		return floor.display();
	}

	public Image displayThumb() {
		if(this.floor == null){
			this.floor = new Floor(this.floorNum);
		}
		return floor.displayThumb();
	}
}
