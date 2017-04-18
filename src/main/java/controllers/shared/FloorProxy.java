package controllers.shared;

import javafx.scene.image.Image;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * Created by tbasl_000 on 4/8/2017.
 */
public class FloorProxy implements FloorImage
{

	private String building;
	private int floorNum;
	private Floor floor;

	public FloorProxy(String building, int floor) {
		this.building = building;
		this.floorNum = floor;
	}

	/** checks to see if the floor has been loaded, then returns the image attached to that
	 *  floor.
	 *
	 * @return The image of the floor we are asking for
	 */
	public Image display() {
		if(this.floor == null){
			this.floor = new Floor(this.building, this.floorNum);
		}
		return floor.display();
	}

	public Image displayThumb() {
		if(this.floor == null){
			this.floor = new Floor(this.building, this.floorNum);
		}
		return floor.displayThumb();
	}
	// create floorProxies for every floor
	private static FloorProxy faulknerFloor1 = new FloorProxy("Faulkner",1);
	private static FloorProxy faulknerFloor2 = new FloorProxy("Faulkner",2);
	private static FloorProxy faulknerFloor3 = new FloorProxy("Faulkner",3);
	private static FloorProxy faulknerFloor4 = new FloorProxy("Faulkner",4);
	private static FloorProxy faulknerFloor5 = new FloorProxy("Faulkner",5);
	private static FloorProxy faulknerFloor6 = new FloorProxy("Faulkner",6);
	private static FloorProxy faulknerFloor7 = new FloorProxy("Faulkner",7);
	private static FloorProxy belkinFloor1 = new FloorProxy("Belkin", 1);
	private static FloorProxy belkinFloor2 = new FloorProxy("Belkin", 2);
	private static FloorProxy belkinFloor3 = new FloorProxy("Belkin", 3);
	private static FloorProxy belkinFloor4 = new FloorProxy("Belkin", 4);
	private static FloorProxy outsideFloor = new FloorProxy("Outside", 1);

	private static LinkedList<FloorProxy> faulknerMaps = new LinkedList<>(
			Arrays.asList(faulknerFloor1, faulknerFloor2, faulknerFloor3, faulknerFloor4,
					faulknerFloor5, faulknerFloor6, faulknerFloor7));

	private static LinkedList<FloorProxy> belkinMaps = new LinkedList<>(
			Arrays.asList(belkinFloor1, belkinFloor2, belkinFloor3, belkinFloor4));

	private static LinkedList<FloorProxy> outsideMaps = new LinkedList<>(
			Arrays.asList(outsideFloor));

	private static HashMap<String, LinkedList<FloorProxy>> floorMaps = new HashMap<>();

	public static HashMap<String, LinkedList<FloorProxy>> getFloorMaps() {
		floorMaps.put("Faulkner", faulknerMaps);
		floorMaps.put("Belkin", belkinMaps);
		floorMaps.put("Outside", outsideMaps);
		return floorMaps;
	}
}
