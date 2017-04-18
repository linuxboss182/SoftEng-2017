package controllers.shared;

import javafx.scene.image.Image;

import java.util.Arrays;
import java.util.LinkedList;

class Floor implements FloorImage
{
	// Attributes of the floor class
	private int floorNum;
	private String building;
	private Image image;
	private Image thumb;
	private String path;
	private String thumbPath;
	// Linked list containing the string names of the different floor images.
	private static LinkedList<String> floorImages = new LinkedList<>(Arrays.asList(
							  "/1_thefirstfloor.png", "/2_thesecondfloor.png", "/3_thethirdfloor.png",
							  "/4_thefourthfloor.png", "/5_thefifthfloor.png", "/6_thesixthfloor.png",
							  "/7_theseventhfloor.png"));

	// Linked list containing the string names of the different floor images.
	private static LinkedList<String> thumbnails = new LinkedList<>(Arrays.asList(
			"/Thumb_First_Floor.png", "/Thumb_Second_Floor.png", "/Thumb_Third_Floor.png",
			"/Thumb_Fourth_Floor.png", "/Thumb_Fifth_Floor.png", "/Thumb_Sixth_Floor.png",
			"/Thumb_Seventh_Floor.png"));

	// constructor for the floor class
	/** @deprecated We should NEVER hard-code a list of floors like this.
	 * If we want to hard-code these, do it outside the constructor: pass the paths in. 
	 */
	@Deprecated
	public Floor(int floorNum, String building) {
		this.floorNum = floorNum;
		this.path = floorImages.get(floorNum - 1);
		this.thumbPath = thumbnails.get(floorNum - 1);
		this.image = null;
		this.building = building;
	}

	public Floor(Image map, Image thumb, int floorNum, String building) {
		this.thumb = thumb;
		this.image = map;
		this.floorNum = floorNum;
		this.building = building;
	}

	public String getName() {
		return this.building;
	}
	
	public int getFloorNum() {
		return this.floorNum;
	}
	
	public String getBuildingName() {
		return this.building;
	}

	/** takes the String building of the path attribute and loads the image attached to that path
	 *
 	 * @return an image of the specified path.
	 * TODO: The image doesn't load sometimes. Having it load in the background prevents it from crashing
	 */
	public Image display() {
		if (this.image == null) {
			return new Image(this.path, true);
		} else {
			return this.image;
		}
	}

	public Image displayThumb() {
		if (this.thumb == null) {
			return new Image(this.thumbPath, true);
		} else {
			return this.thumb;
		}
	}


}
