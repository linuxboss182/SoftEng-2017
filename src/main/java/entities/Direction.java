package entities;

import entities.icons.IconType;

/**
 * Created by Kenneth on 4/25/17.
 */
public class Direction
{
	private String textDirection;
	private IconType icon;

	public Direction(String textDirection, IconType icon){
		this.textDirection = textDirection;
		this.icon = icon;
	}

	public String getTextDirection(){
		return this.textDirection;
	}

	public IconType getIcon(){
		return this.icon;
	}


}
