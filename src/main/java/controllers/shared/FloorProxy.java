package controllers.shared;

import javafx.scene.image.Image;

import java.util.Arrays;
import java.util.LinkedList;

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
	// create floorProxies beforehand
	private static FloorProxy floor1 = new FloorProxy(1);
	private static FloorProxy floor2 = new FloorProxy(2);
	private static FloorProxy floor3 = new FloorProxy(3);
	private static FloorProxy floor4 = new FloorProxy(4);
	private static FloorProxy floor5 = new FloorProxy(5);
	private static FloorProxy floor6 = new FloorProxy(6);
	private static FloorProxy floor7 = new FloorProxy(7);
	public static LinkedList<FloorProxy> maps = new LinkedList<>(
			Arrays.asList(floor1, floor2, floor3, floor4, floor5, floor6, floor7));
}
