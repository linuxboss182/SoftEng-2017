package controllers.shared;

import javafx.scene.image.Image;

import java.util.*;

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

	@Override
	public String getName() {
		return this.building;
	}

	@Override
	public int getNumber() {
		return this.floorNum;
	}
	// create floorProxies for every floor

//	private static HashMap<String, LinkedList<FloorProxy>> EVERYTHING;
//	static {
//		LinkedList<FloorProxy> faulkner = new LinkedList<>(Arrays.asList(
//				new FloorProxy("Faulkner",1),
//				new FloorProxy("Faulkner",2),
//				new FloorProxy("Faulkner",3),
//				new FloorProxy("Faulkner",4),
//				new FloorProxy("Faulkner",5),
//				new FloorProxy("Faulkner",6),
//				new FloorProxy("Faulkner",7)
//		));
//		LinkedList<FloorProxy> belkin = new LinkedList<>(Arrays.asList(
//				new FloorProxy("Belkin", 1),
//				new FloorProxy("Belkin", 2),
//				new FloorProxy("Belkin", 3),
//				new FloorProxy("Belkin", 4)
//		));
//		LinkedList<FloorProxy> outside = new LinkedList<>();
//		outside.add(new FloorProxy("Outside", 1));
//
//		EVERYTHING = new HashMap<>();
//		EVERYTHING.put("FAULKNER", faulkner);
//		EVERYTHING.put("BELKIN", belkin);
//		EVERYTHING.put("OUTSIDE", outside);
//	}

	private static LinkedList<FloorProxy> FLOORS = new LinkedList<>(Arrays.asList(
				new FloorProxy("Faulkner",1),
				new FloorProxy("Faulkner",2),
				new FloorProxy("Faulkner",3),
				new FloorProxy("Faulkner",4),
				new FloorProxy("Faulkner",5),
				new FloorProxy("Faulkner",6),
				new FloorProxy("Faulkner",7),
				new FloorProxy("Belkin", 1),
				new FloorProxy("Belkin", 2),
				new FloorProxy("Belkin", 3),
				new FloorProxy("Belkin", 4),
				new FloorProxy("Outside", 1)));

	/**
	 * Get a floor by building and number
	 */
	public static FloorProxy getFloor(String building, int floorNum) {
		return FLOORS.stream()
				.filter(floor -> (floor.getNumber() == floorNum) && (floor.getName().compareToIgnoreCase(building) == 0))
				.findAny().orElse(null);
		//return EVERYTHING.get(building).get(floorNum-1);
	}

	public static List<FloorProxy> getFloors() {
		return new ArrayList<>(FloorProxy.FLOORS);
	}
}
