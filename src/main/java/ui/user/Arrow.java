package ui.user;

import javafx.scene.Group;
import javafx.scene.shape.Line;

/**
 * Created by tbasl_000 on 4/24/2017.
 */
public class Arrow extends Group {

	private final Line line;

	public Arrow(Line direction) {
		this(direction, new Line(), new Line());
	}

	private static final double arrowLength = 5; // This is the length of each of the side arrow lines
	private static final double arrowWidth = 5; // This is the angle between the side arrow lines and the main line

	private Arrow(Line line, Line arrow1, Line arrow2) {
		super(line, arrow1, arrow2);
		this.line = line;
		double ex = line.getEndX();
		double ey = line.getEndY();
		double sx = line.getStartX();
		double sy = line.getStartY();

		arrow1.setEndX(ex);
		arrow1.setEndY(ey);
		arrow2.setEndX(ex);
		arrow2.setEndY(ey);
		arrow1.setStrokeWidth(line.getStrokeWidth());
		arrow1.setStroke(line.getStroke());
		arrow2.setStrokeWidth(line.getStrokeWidth());
		arrow2.setStroke(line.getStroke());

		if (ex == sx && ey == sy) {
			arrow1.setStartX(ex);
			arrow1.setStartY(ey);
			arrow2.setStartX(ex);
			arrow2.setStartY(ey);
		} else {
			double factor = arrowLength / Math.hypot(sx - ex, sy - ey);
			double factorO = arrowWidth / Math.hypot(sx - ex, sy - ey);

			double dx = (sx - ex) * factor;
			double dy = (sy - ey) * factor;

			double ox = (sx - ex) * factorO;
			double oy = (sy - ey) * factorO;

			arrow1.setStartX(ex + dx - oy);
			arrow1.setStartY(ey + dy + ox);
			arrow2.setStartX(ex + dx + oy);
			arrow2.setStartY(ey + dy - ox);
		}
	}
}
