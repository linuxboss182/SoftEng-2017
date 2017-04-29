package controllers.filereader;

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
	public static void parseProfessionals(File file, Directory directory)
			throws FileNotFoundException {
		ProfessionalTSVParser reader = new ProfessionalTSVParser(file, directory);
		reader.open();
		reader.parseToDirectory();
		reader.close();
	}
	/**
	 * Parse nodes from the given file into the given directory
	 *
	 * See {@link NodeCSVParser} for details.
	 */
	public static void parseNodes(File file, Directory directory)
			throws FileNotFoundException{
		NodeCSVParser reader = new NodeCSVParser(file, directory);
		reader.open();
		reader.parseToDirectory();
		reader.close();
	}
}
