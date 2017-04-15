package controllers.filereader;

import javafx.scene.control.Alert;
import java.io.File;
import java.io.FileNotFoundException;

import entities.Directory;

public class FileParser
{
	/**
	 * Parse professionals from the given file into the given directory
	 *
	 * See {@link ProfessionalTSVParser} for details.
	 */
	public static void parseProfessionals(File file, Directory directory) {
		ProfessionalTSVParser reader = new ProfessionalTSVParser(file, directory);
		try {
			reader.open();
		} catch (FileNotFoundException e) {
			Alert a = new Alert(Alert.AlertType.ERROR, e.getMessage());
			a.showAndWait();
			return;
		}
		reader.parseToDirectory();
		reader.close();
	}
}
