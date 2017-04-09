package controllers;

import javafx.scene.image.Image;

/**
 * Created by tbasl_000 on 4/8/2017.
 */
public class Floor implements FloorImage
{
	public Floor(int floorNum, String path) {
		this.floorNum = floorNum;
		this.path = path;
	}

	public Image display() {
		return new Image(path);
	}

	private int floorNum;
	private String path;
}
