package controllers;

import javafx.scene.image.Image;

import java.util.Arrays;
import java.util.LinkedList;

/**
 * Created by tbasl_000 on 4/8/2017.
 */
public class Floor implements FloorImage
{
	// Attributes of the floor class
	private int floorNum;
	private String path;
	// Linked list containing the string names of the different floor images.
	private LinkedList<String> floorImages = new LinkedList<>(Arrays.asList(
							  "/1_thefirstfloor.png", "/2_thesecondfloor.png", "/3_thethirdfloor.png",
							  "/4_thefourthfloor.png", "/5_thefifthfloor.png", "/6_thesixthfloor.png",
							  "/7_theseventhfloor.png"));

	// constructor for the floor class
	public Floor(int floorNum) {
		this.floorNum = floorNum;
		this.path = floorImages.get(floorNum - 1);
	}

	/** takes the String name of the path attribute and loads the image attached to that path
	 *
 	 * @return an image of the specified path.
	 */
	public Image display() {
		return new Image(path);
	}


}
