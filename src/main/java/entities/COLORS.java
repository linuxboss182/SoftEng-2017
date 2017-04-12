package entities;

import javafx.scene.paint.Color;

public enum COLORS
{
	// NAME(strokeWidth, bodyColor, lineColor)
	NODE(1.5, Color.BLUE, Color.BLACK),
	SELECTED_NODE(2, Color.BLUE, Color.WHITE),
	ROOM(1.5, Color.YELLOW, Color.BLACK),
	SELECTED_ROOM(2, Color.YELLOW, Color.WHITE),
	ELEVATOR(1.5, Color.FUCHSIA, Color.BLACK),
	SELECTED_ELEVATOR(2, Color.FUCHSIA, Color.WHITE),
	;

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
