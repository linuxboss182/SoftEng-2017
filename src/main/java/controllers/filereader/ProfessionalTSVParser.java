package controllers.filereader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import entities.Directory;
import entities.Professional;

/**
 * Class to parse a TSV file
 *
 * The expected input format is format is approximately as follows:
 *
 * <ul>
 * <li>A name, followed by a tab and a list of rooms.</li>
 * <li>A list of rooms may be either a single room, two rooms separated by "and", or any
 * number of comma-seaparated rooms, followed by "and" and another room.*</li>
 * <li>A name may be either a professional's name or a special name.</li>
 * <li>A professional's name consists of a surname, a comma, other names, a comma, and a list
 * of comma-separated titles.</li>
 * <li>A special name may contain no commas or tabs.</li>
 * </ul>
 *
 * <p>Excel adds quotes to professional's names when saving as TSV, and escapes quotes
 * by adding an additional pair of quotes. This is handled here.</p>
 *
 * <p>* If a room name containing "and" appears as one of the final two elements of a
 * list of rooms, it is ambiguous and may not be registered correctly.</p>
 *
 * <p>Note that this class does not validate the file; if the file is poorly-formatted,
 * unexpected results may occur.</p>
 */
/*
   <line> -> <name> <TAB> <rooms> | <TAB>
   <name> -> <person> | <location>
   <location> -> <name>
   <person> -> <name>, <name>, <titles>
   <person> -> <name>, <name>
   <titles> -> <title>, <titles>
   <rooms> -> <room> | <room-list> and <room>
   <room-list> -> <room> | <room>, <room-list>

   <name> -> <word>
   <room> -> <word>
   <word> -> <word-char> | <word-char> <word>
   <word-char> -> anything that isn't a tab or comma
   <TAB> -> a tab character
 */
public class ProfessionalTSVParser
{
	private static Pattern professionalPattern = Pattern.compile(
			"^(?<surname>.+?), (?<givenName>.+?)(, (?<titles>[^\t]+?))?\t[^\t]*$");
	private static Pattern roomPattern = Pattern.compile(
			"^[^\t]*\t(?<locations>.*?)( and (?<last>.*))?$"
	);
//	private static Pattern locationPattern = Pattern.compile(
//			"^(?<name>.+?)\t(?<locations>.*)$");

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
	public ProfessionalTSVParser(File file, Directory directory) {
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
				prof = new Professional(match.group("givenName"),
						match.group("surname"),
						(titles==null) ? "" : titles);
				this.dir.addProfessional(prof);

				this.addLocationsToProfessional(line, prof);
			}
		}
	}

	/**
	 * Add the rooms listed in the given line to the given professional
	 */
	private void addLocationsToProfessional(String line, Professional prof) {
		Matcher match = roomPattern.matcher(line);
		if (!match.matches()) return;

		Collection<String> roomNames = new ArrayList<>();

		// First check for a room after an "and"
		if (match.group("last") != null) {
			roomNames.add(match.group("last"));
		}

		// Comma-separated rooms
		String locations = match.group("locations");
		for (String room : locations.split(", ")) {
			roomNames.add(room);
		}

		this.dir.getRooms().stream().filter(r -> roomNames.contains(r.getName())).forEach(prof::addLocation);
	}
}
