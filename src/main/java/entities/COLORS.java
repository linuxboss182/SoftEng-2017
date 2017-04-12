package entities;

import javafx.scene.paint.Color;

public enum COLORS
{
	NO_ROOM(1.5, Color.BLUE, Color.BLACK),
	ROOM(1.5, Color.YELLOW, Color.BLACK),
	SELECTED(1.5, Color.BLUE, Color.GREEN),
	SELECTED_ROOM(1.5, Color.YELLOW, Color.GREEN);

	// color
	private final double strokeWidth;
	private final Color bodyColor;
	private final Color lineColor;

	COLORS(double strokeWidth, Color bodyColor, Color lineColor) {
		this.strokeWidth = strokeWidth;
		this.bodyColor = bodyColor;
		this.lineColor = lineColor;
	}

	public double strokeWidth() {
		return this.strokeWidth;
	}

	public Color bodyColor() {
		return this.bodyColor;
	}

	public Color lineColor() {
		return this.lineColor;
	}
}
