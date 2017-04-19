package controllers.shared;

import javafx.scene.image.Image;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;

public class Floor implements FloorImage
{
	// Attributes of the floor class

	private int floorNum;
	private String path;
	private String thumbPath;
	private String buildingName;

	// Linked list containing the string names of the different floor images.
	private LinkedList<String> faulknerFloorImages = new LinkedList<>(Arrays.asList(
			"/1_thefirstfloor.png", "/2_thesecondfloor.png", "/3_thethirdfloor.png",
			"/4_thefourthfloor.png", "/5_thefifthfloor.png", "/6_thesixthfloor.png",
			"/7_theseventhfloor.png"));

	private LinkedList<String> belkinFloorImages = new LinkedList<>(Arrays.asList(
			"/belkin1.png", "/belkin2.png", "/belkin3.png", "/belkin4.png"));

	private LinkedList<String> outsideFloorImages = new LinkedList<>(Arrays.asList(
			"/outsidearea.png"));

	private HashMap<String, LinkedList<String>> floorImages = new HashMap<>();

	private HashMap<String, LinkedList<String>> getFlImg(){
		floorImages.put("Faulkner", faulknerFloorImages);
		floorImages.put("Belkin", belkinFloorImages);
		floorImages.put("Outside", outsideFloorImages);
		return floorImages;
	}

	// Linked list containing the string names of the different floor images.
	private LinkedList<String> faulknerThumbnails = new LinkedList<>();

	//private LinkedList<String> belkinThumbnals = new

	private static HashMap<String, LinkedList<String>> ALL_THUMBS = new HashMap<>();
	static {
		LinkedList<String> faulknerThumbs = new LinkedList<>();
		faulknerThumbs.add("/t_faulk1.png");
		faulknerThumbs.add("/t_faulk2.png");
		faulknerThumbs.add("/t_faulk3.png");
		faulknerThumbs.add("/t_faulk4.png");
		faulknerThumbs.add("/t_faulk5.png");
		faulknerThumbs.add("/t_faulk6.png");
		faulknerThumbs.add("/t_faulk7.png");
		ALL_THUMBS.put("FAULKNER", faulknerThumbs);

		LinkedList<String> belkinThumbs = new LinkedList<>();
		belkinThumbs.add("/t_belkin1.png");
		belkinThumbs.add("/t_belkin2.png");
		belkinThumbs.add("/t_belkin3.png");
		belkinThumbs.add("/t_belkin4.png");
		ALL_THUMBS.put("BELKIN", belkinThumbs);

		LinkedList<String> outsideThumbs = new LinkedList<>();
		outsideThumbs.add("/t_outside.png");
		ALL_THUMBS.put("OUTSIDE", outsideThumbs);
	}

	// constructor for the floor class
	public Floor(String building, int floorNum) {
		this.floorNum = floorNum;
		this.path = getFlImg().get(building).get(floorNum - 1);
		this.thumbPath = ALL_THUMBS.get(building.toUpperCase()).get(floorNum - 1);
	}

	/** takes the String name of the path attribute and loads the image attached to that path
	 *
	 * @return an image of the specified path.
	 * TODO: The image doesn't load sometimes. Having it load in the background prevents it from crashing
	 */
	public Image display() {
		Image ret;
		ret = new Image(path, true);

		return ret;
	}

	public Image displayThumb() {
		Image ret;
		ret = new Image(thumbPath, true);

		return ret;
	}

	@Override
	public String getName() {
		return this.buildingName;
	}

	@Override
	public int getNumber() {
		return this.floorNum;
	}


}
