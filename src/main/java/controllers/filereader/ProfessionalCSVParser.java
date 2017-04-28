package controllers.filereader;

import entities.Directory;
import entities.Professional;
import entities.Room;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by ScionIV on 4/27/2017.
 */
public class ProfessionalCSVParser
{
	private static Pattern professionalPattern = Pattern.compile(
			"^(?<type>\\w+?),(?<surname>.+?), (?<givenName>.+?)(, (?<titles>.+?)),(?<rooms>(\\w+.*)?)$");

	private File file;
	private Directory dir = null;
	private FileReader fr;
	private BufferedReader br;

	/**
	 * Make a new parser
	 *
	 * @param file The file to read data from
	 * @param directory The directory to add the parsed data to
	 */
	public ProfessionalCSVParser(File file, Directory directory) {
		this.file = file;
		this.dir = directory;
	}

	/**
	 * Parse the file and add the professionals to the directory
	 */
	public void parseToDirectory() {
		try {
			this.parseByLines(this.br);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Open this parser's file for reading
	 */
	public void open() throws FileNotFoundException {
		this.fr = new FileReader(this.file);
		this.br = new BufferedReader(this.fr);
	}

	/**
	 * Close this parser's file
	 */
	public void close() {
		try {
			this.fr.close();
			this.br.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Parse the lines of the given BufferedReader into professionals in the directory
	 */
	private void parseByLines(BufferedReader br)
			throws IOException {
		//i dont like these things
		Collection<String> validRooms = new ArrayList<>();
		for(Room r: this.dir.getRooms()){
			//but they must be done
			validRooms.add(r.getName());
		}
		String line;
		while ((line = br.readLine()) != null) {
			if (line.matches("^\\s*$")) {
				continue; // skip all-whitespace lines
			}
			// Remove quotes, and de-escape quote-escaped quotes
			line = line.replaceAll("^\"", "");
			line = line.replaceAll("\"\t", "\t");
			line = line.replaceAll("\"\"", "\"");

			// See if it matches, and make a professional if it does
			Professional prof = null;
			Matcher match = professionalPattern.matcher(line);
			if (match.matches()) {
				// make a professionalfg
				String titles = match.group("titles");
				prof = dir.addNewProfessional(match.group("givenName"),
						match.group("surname"),
						(titles==null) ? "" : titles);

				Collection<String> roomNames = new ArrayList<>();
				for(String s: match.group("rooms").split(",")){
					roomNames.add(s);
				}
				//add rooms

				for(Room r: this.dir.getRooms()){
					if(roomNames.contains(r.getName())){
						dir.addRoomToProfessional(r, prof);
					}
				}
			}
		}
	}
}
