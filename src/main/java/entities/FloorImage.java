package entities;

import javafx.scene.image.Image;
import javafx.util.StringConverter;


public interface FloorImage
{
	Image display();
	Image displayThumb();
	String getName();
	int getNumber();

	StringConverter<FloorImage> FLOOR_STRING_CONVERTER = new StringConverter<FloorImage>()
	{
		@Override
		public String toString(FloorImage floor) {
			StringBuilder sb = new StringBuilder();
			sb.append(floor.getName());
			sb.append(" floor ");
			sb.append(floor.getNumber());
			return sb.toString();
		}

		@Override
		public FloorImage fromString(String string) {
			return null;
		}
	};

}
