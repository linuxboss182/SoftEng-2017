package entities;

import javafx.geometry.Bounds;
import javafx.scene.image.Image;
import main.ApplicationController;

import java.util.*;

public class FloorProxy implements FloorImage
{

	private String building;
	protected Viewport defaultView;
	private int floorNum;
	private Floor floor;

	public FloorProxy(String building, int floor, Viewport defaultView) {
		this.defaultView = defaultView;
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

	public static Viewport getDefaultView(String building, int floorNum){
		return FloorProxy.getFloor(building, floorNum).defaultView;
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

	private static LinkedList<FloorProxy> FLOORS = new LinkedList<>(Arrays.asList(
				new FloorProxy("Faulkner",1, new Viewport(100, 200, 100, 200)),
				new FloorProxy("Faulkner",2, new Viewport(100, 300, 100, 300)),
				new FloorProxy("Faulkner",3, new Viewport(50, 300, 50, 300)),
				new FloorProxy("Faulkner",4, new Viewport(50, 300, 50, 300)),
				new FloorProxy("Faulkner",5, new Viewport(50, 300, 50, 300)),
				new FloorProxy("Faulkner",6, new Viewport(50, 300, 50, 300)),
				new FloorProxy("Faulkner",7, new Viewport(50, 300, 50, 300)),
				new FloorProxy("Belkin", 1,  new Viewport(50, 300, 50, 300)),
				new FloorProxy("Belkin", 2,  new Viewport(50, 300, 50, 300)),
				new FloorProxy("Belkin", 3,  new Viewport(50, 300, 50, 300)),
				new FloorProxy("Belkin", 4,  new Viewport(50, 300, 50, 300)),
				new FloorProxy("Outside", 1, new Viewport(50, 300, 50, 300))));

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

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(this.getName());
		sb.append(" floor ");
		sb.append(this.getNumber());
		return sb.toString();
	}


	public static class Viewport
	{
		public double minX;
		public double maxX;
		public double minY;
		public double maxY;

		public Viewport(double minX, double maxX, double minY, double maxY){
			this.minX = minX;
			this.maxX = maxX;
			this.minY = minY;
			this.maxY = maxY;
		}
	}

}
