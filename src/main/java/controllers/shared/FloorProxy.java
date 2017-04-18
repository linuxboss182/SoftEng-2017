package controllers.shared;

import javafx.scene.image.Image;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

// TODO: Reimplement FloorProxy.maps as a TreeSet sorted by building name and floor number
// Then we can also ensure no duplicates
public class FloorProxy implements FloorImage
{

	private int floorNum;
	private String building;
	private Floor floor;

	public FloorProxy(int floor, String building) {
		this.floorNum = floor;
		this.building = building;
	}

	/** checks to see if the floor has been loaded, then returns the image attached to that
	 *  floor.
	 *
	 * @return The image of the floor we are asking for
	 */
	public Image display() {
		if(this.floor == null){
			this.floor = new Floor(this.floorNum, this.building);
		}
		return this.floor.display();
	}

	public Image displayThumb() {
		if(this.floor == null){
			this.floor = new Floor(this.floorNum, this.building);
		}
		return this.floor.displayThumb();
	}

	public String getBuildingName() {
		return this.building;
	}

	public int getFloorNum() {
		return this.floorNum;
	}

	// create floorProxies beforehand
	private static FloorProxy floor1 = new FloorProxy(1, "Faulkner Hospital");
	private static FloorProxy floor2 = new FloorProxy(2, "Faulkner Hospital");
	private static FloorProxy floor3 = new FloorProxy(3, "Faulkner Hospital");
	private static FloorProxy floor4 = new FloorProxy(4, "Faulkner Hospital");
	private static FloorProxy floor5 = new FloorProxy(5, "Faulkner Hospital");
	private static FloorProxy floor6 = new FloorProxy(6, "Faulkner Hospital");
	private static FloorProxy floor7 = new FloorProxy(7, "Faulkner Hospital");
	private static LinkedList<FloorImage> maps = new LinkedList<>(
			Arrays.asList(floor1, floor2, floor3, floor4, floor5, floor6, floor7));
	
	public static List<FloorImage> getMaps() {
		return new LinkedList<>(FloorProxy.maps);
	}
	
	public static void addFloor(Image map, Image thumb, int number, String building) {
		FloorProxy.maps.add(new Floor(map, thumb, number, building));
	}

	public static void removeFloor(int number, String building) {
		FloorProxy.maps.removeIf(floor -> floor.getFloorNum() == number && floor.getBuildingName() == building);
	} 
}
