package controllers.filereader;

/**
 * Created by ScionIV on 4/27/2017.
 */

import entities.Directory;
import entities.FloorProxy;
import entities.Node;

import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
   Expected Format for Nodes:
   Node,10,20,1,FAULKNER

   Expected Format for Rooms:
   Room,10,30,1,FAULKNER,Name,ShortName,Description

   Populate your sheet with column names for readability:
   Type     X   Y   Floor   BuildingName    Name    ShortName   Description
   Node     10  20  1       FAULKNER
   Room     10  30  2       FAULKNER        TestName    TN      A Room
 */
public class NodeCSVParser
{
	private static Pattern nodePattern = Pattern.compile(
			"^(?<type>\\w+),(?<x>\\d{1,2}),(?<y>\\d{1,2}),(?<floor>\\d{1,2}),(?<buildingname>(\\w+\\s*\\w*)*),(?<name>(\\w+\\s*\\w*)*),(?<sname>(\\w+\\s*\\w*)*),(?<desc>(\\w+\\s*\\w*)*$)");

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
	public NodeCSVParser(File file, Directory directory) {
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
			Node node = null;
			Matcher match = nodePattern.matcher(line);
			if (match.matches()) {
				// make a node
				if(match.group("type").equals("Node")) {
					System.out.println("Node");
					dir.addNewNode(Double.valueOf(match.group("x")), Double.valueOf(match.group("y")),
							Integer.valueOf(match.group("floor")), match.group("buildingname"));
				} else if (match.group("type").equals("Room")){
					System.out.println("Room");
					dir.addNewRoomNode(Double.valueOf(match.group("x")), Double.valueOf(match.group("y")),
									   FloorProxy.getFloor(match.group("buildingname"), Integer.valueOf(match.group("floor"))),
									   match.group("name"), match.group("sname"), match.group("desc"));
				} else{
					//ya done fucked up
					throw new IOException();
				}
			}
		}
	}
}
