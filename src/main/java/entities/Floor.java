package entities;

import javafx.scene.image.Image;
import main.ApplicationController;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;

public class Floor implements FloorImage
{
	// Attributes of the floor class
	private int floorNum;
	private FloorImages path;
	private String thumbPath;
	private String buildingName;


	class FloorImages {
		public String vistorImage;
		public String professionalImage;

		FloorImages(String vistorImage, String professionalImage){
			this.vistorImage = vistorImage;
			this.professionalImage = professionalImage;
		}
	}

	// Linked list containing the string names of the different floor images.
	private LinkedList<FloorImages> faulkner = new LinkedList<>(Arrays.asList(
			new FloorImages("/1_thefirstfloor.png", "/Admin1.png"),
			new FloorImages("/2_thesecondfloor.png", "/Admin2.png"),
			new FloorImages("/3_thethirdfloor.png", "/Admin3.png"),
			new FloorImages("/4_thefourthfloor.png","/Admin4.png" ),
			new FloorImages("/5_thefifthfloor.png", "/Admin5.png"),
			new FloorImages("/6_thesixthfloor.png", "/Admin6.png"),
			new FloorImages("/7_theseventhfloor.png", "/Admin7.png")));

	private LinkedList<FloorImages> belkin = new LinkedList<>(Arrays.asList(
			new FloorImages("/belkin1.png", "/belkin1.png"),
			new FloorImages("/belkin2.png", "/belkin1.png"),
			new FloorImages("/belkin3.png", "/belkin1.png"),
			new FloorImages("/belkin4.png", "/belkin1.png")));

	private LinkedList<FloorImages> outline = new LinkedList<>(Arrays.asList(
			new FloorImages("/outsidearea.png", "/outsidearea.png")));

	private HashMap<String, LinkedList<FloorImages>> floorImages = new HashMap<>();

	private HashMap<String, LinkedList<FloorImages>> getFlImg(){
		floorImages.put("Faulkner", faulkner);
		floorImages.put("Belkin", belkin);
		floorImages.put("Outside", outline);
		return floorImages;
	}

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
		outsideThumbs.add("/t_outsidearea.png");
		ALL_THUMBS.put("OUTSIDE", outsideThumbs);
	}

	// constructor for the floor class
	public Floor(String building, int floorNum) {
		this.floorNum = floorNum;
		this.thumbPath = ALL_THUMBS.get(building.toUpperCase()).get(floorNum - 1);

		this.path = getFlImg().get(building).get(floorNum - 1);
	}

	/** takes the String name of the path attribute and loads the image attached to that path
	 *
	 * @return an image of the specified path.
	 */
	public Image display() {
		Image ret;
		if(ApplicationController.getDirectory().isProfessional()) {
			ret = new Image(path.professionalImage, true); // backgroound loading prevents crashing sometimes
		}else{
			ret = new Image(path.vistorImage, true); // backgroound loading prevents crashing sometimes
		}
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
