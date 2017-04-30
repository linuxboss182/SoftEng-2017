package entities;

import entities.icons.IconType;

public class Direction
{
	private String textDirection;
	private IconType icon;
	private Node node;

	public Direction(String textDirection, IconType icon, Node node){
		this.textDirection = textDirection;
		this.icon = icon;
		this.node = node;
	}

	public String getTextDirection(){
		return this.textDirection;
	}

	public IconType getIcon(){
		return this.icon;
	}

	public Node getNode() {
		return this.node;
	}

}
