package main;

import java.sql.*;
import java.util.Map;
import java.util.HashMap;

import entities.Directory;
import entities.Node;
import entities.Professional;
import entities.Room;

// Feel free to remove all the commented-out PRINTs and PRINTLNs once everything works

public class DatabaseController
{
	private static String driver = "org.apache.derby.jdbc.EmbeddedDriver";

	private Connection db_connection;
	private String connection_string;

	// Debugging functions
//	private static void PRINTLN(Object o) {
//		System.out.println(o);
//	}
//	private static void PRINT(Object o) {
//		System.out.print(o);
//	}

	public DatabaseController(String connection_string) {
		this.connection_string = connection_string;
	}

	public DatabaseController(){
		this("jdbc:derby:DB;create=true");
	}

	/**
	 * Attempt to connect to the database
	 *
	 * If the database does not exist, it is created and the tables are created.
	 *
	 * @throws DatabaseException if the connection fails
	 */
	public void init()
			throws DatabaseException {
		boolean flag = this.initDB();
		if (! flag) {
			throw new DatabaseException("Connection failed");
		}

		SQLWarning warning;
		try {
			// db warning was issued if db existed before this function was called
			warning = this.db_connection.getWarnings();
			// if null, no warning = new database
		} catch (SQLException e) {
			throw new DatabaseException("Failed to check connecton warnings", e);
		}

		if (warning == null) { //if null, DB does not exist
			flag = this.reInitSchema();
			if (! flag) {
				throw new DatabaseException("Failed to initialize database schema");
			}
		}
	}

	//initialize the database
	//returns true if success, false if failure
	// (do not add db calls after the line indicated below)
	private boolean initDB() {
		try {
			Class.forName(DatabaseController.driver);
		} catch(ClassNotFoundException e) {
			System.err.println("Java DB Driver not found. Add the classpath to your module.");
			return false;
		}
		System.out.println("Connected to database");

		try {
			this.db_connection = DriverManager.getConnection(this.connection_string);
			// MAKE NO MORE DB CALLS AFTER THIS POINT (they could break this.init())
		} catch (SQLException e) {
			System.err.println("Connection failed. Check output console.");
			e.printStackTrace();
			return false;
		}
		System.out.println("Java DB connection established!");
		return true;
	}

	//initializes the database empty with the desired schema
	//returns true if success, false if error

	/**
	 * Initialize the database schema
	 *
	 * Deletes and recreates all tables in the database
	 *
	 * @return true if successful
	 */
	// TODO: Refactor so reInitSchema throws SQLException to be handled elsewhere in the class
	private boolean reInitSchema() {
		boolean result;
		Statement initSchema = null;
		try {
			initSchema = this.db_connection.createStatement();
		} catch (SQLException e) {
			//something's really bad if we get here. like "we don't have a database" bad
			e.printStackTrace();
			return false;
		}

		for (String dropStatement : StoredProcedures.getDrops()) {
			//drop the table if it exists
			try {
				initSchema.executeUpdate(dropStatement);
			} catch (SQLException e) {
				System.err.println("Failed statement: " + dropStatement);
				System.err.println(e.getMessage());
			}
		}

		for (String table : StoredProcedures.getSchema()) {
			try {
				initSchema.executeUpdate(table);
			} catch (SQLException e) {
				System.err.println("Failed statement: " + table);
				System.err.println(e.getMessage());
			}
		}

		/**  Code below wasn't working and was replaced with for loops above. Keeping just
		 *   in case we want to use it again. */
		/*
		String[] schema = StoredProcedures.getSchema();
		//find our tables in the schema
		for (int i=0; i < schema.length; i++) {
			Pattern matchTable = Pattern.compile("\\bCREATE\\b\\s\\bTABLE\\b\\s(\\w*)");
			Matcher matcher = matchTable.matcher(schema[i]);
			boolean found = false;
			while (matcher.find() && found == false) {
				//we're making a table
				String table = matcher.group(1); //group zero = entire expression

				//make the table if it doesn't exist
				try {
					initSchema.executeUpdate(schema[i]);
				} catch (SQLException e) {
					System.err.println("Failed to create table " + table + ". Continuing...");
					System.err.println(e.getMessage());
				}

				found = true;
			}
		}
		*/
		//close connection via statement
		try {
			initSchema.close();
		} catch (SQLException e) {
			System.err.println("Failed to close connection");
			e.printStackTrace();
			return false;
		}

		//stop once we find the first match(assume one create statement per string)
		return true;
	}

	// close the connection to the database
	// returns true if success, false if failure
	public boolean close() {
		try {
			this.db_connection.close();
			return true;
		} catch (SQLException e) {
			System.err.println("Failed to close connection");
			e.printStackTrace();
		 	return false;
		}
	}

	public Directory getDirectory() {
		Directory dir = new Directory();
		this.populateDirectory(dir);
		return dir;
	}

	/**Populates a given directory with nodes/rooms/professionals
	 *
	 *
	 * @param directory The directory to populate
	 * @return True if success, false if failure
	 */
	public boolean populateDirectory(Directory directory) {
		Map<Integer, Node> nodes = new HashMap<>();
		Map<Integer, Room> rooms = new HashMap<>();
		Map<Integer, Professional> professionals = new HashMap<>();
		Integer kioskID = null; // (should be Optional<Integer>) not in use
		try {
			//retrieve nodes and rooms
			this.retrieveNodesAndRooms(nodes, rooms);
			//find all them professionals
			this.retrieveProfessionals(rooms, professionals);
			kioskID = this.retrieveKiosk();
		} catch (SQLException e){
			e.printStackTrace();
			System.err.println("A SQL Exception occured");
			return false;
		}
		//add all to directory
		for(Node n: nodes.values()){
			directory.addNode(n);
		}
		for(Room r: rooms.values()){
			directory.addRoom(r);
		}
		for(Professional p: professionals.values()){
			directory.addProfessional(p);
		}
		// commented out because we don't have a defined kiosk location at the moment
		System.out.println("Kiosk is " + kioskID);
		if (kioskID != null) {
			directory.setKiosk(rooms.get(kioskID));
		}

		return true;
	}

	/** This method is not in use because we are not using kiosks at the moment */
	private Integer retrieveKiosk() throws SQLException { // (should return Optional<Integer>)
		System.out.println("Getting kiosk");
		Statement query = this.db_connection.createStatement();
		ResultSet result = query.executeQuery(StoredProcedures.procRetrieveKiosk());
		if (result.next()) {
			return result.getInt("roomID");
			// no null check needed because column is "NOT NULL"
		} else {
			return null; // empty kiosk table
		}
	}

	/**Retrieves all employees with their location data populated(among other things)
	 *
	 * @param rooms A hash map of all rooms in the database
	 * @param professionals The hash map of professionals to populate
	 */
	private void retrieveProfessionals(Map<Integer, Room> rooms, Map<Integer, Professional> professionals) throws SQLException{
		HashMap<Integer, Room> profRooms = new HashMap<>();
		try {
			Statement queryProfessionals = this.db_connection.createStatement();
			ResultSet resultProfessionals = queryProfessionals.executeQuery(StoredProcedures.procRetrieveEmployees());

			//find all professionals
			while (resultProfessionals.next()) {
				Professional professional = new Professional(
						resultProfessionals.getString("employeeGivenName"),
						resultProfessionals.getString("employeeSurname"),
						resultProfessionals.getString("employeeTitle"));
				//add to hashmap
				professionals.put(resultProfessionals.getInt("employeeID"), professional);
			}
			resultProfessionals.close();
			queryProfessionals.close();

			Statement queryProfRooms = this.db_connection.createStatement();
			ResultSet resultProfRooms = queryProfRooms.executeQuery(StoredProcedures.procRetrieveEmployeeRooms());

			//look for any locations we might have
			while (resultProfRooms.next()) {
				int employeeID = resultProfRooms.getInt("employeeID");
				Professional professional = professionals.get(employeeID);
				Room room = rooms.getOrDefault(resultProfRooms.getInt("roomID"), null);

				if (room != null) {
					professional.addLocation(room);
				}
			}
			resultProfRooms.close();
			queryProfRooms.close();
		} catch (SQLException e){
			throw e;
		}
	}

	/**Retrieves nodes and rooms from the database and populates the given hash maps
	 *
	 * @param nodes The map of nodes to populate
	 * @param rooms The map of rooms to populate
	 */
	private void retrieveNodesAndRooms(Map<Integer, Node> nodes, Map<Integer, Room> rooms) throws SQLException {
		try {
			//populate Nodes
			Statement queryNodes = this.db_connection.createStatement();
			ResultSet resultNodes = queryNodes.executeQuery(StoredProcedures.procRetrieveNodes());
			while (resultNodes.next()) {
//				PRINTLN("Loading node " + resultNodes.getInt("nodeID"));
				Node node = new Node(resultNodes.getDouble("nodeX"),
				                     resultNodes.getDouble("nodeY"),
				                     resultNodes.getInt("floor"));
				nodes.put(resultNodes.getInt("nodeID"), node);
			}
			resultNodes.close();

			// populate Rooms
			Statement queryRooms = this.db_connection.createStatement();
			ResultSet resultRooms = queryRooms.executeQuery(StoredProcedures.procRetrieveRooms());
			while (resultRooms.next()) {
//				PRINTLN("Loading room " + resultRooms.getInt("roomID"));
				Room room = new Room(resultRooms.getString("roomName"),
				                     resultRooms.getString("roomDescription"));
				int nodeID = resultRooms.getInt("nodeID");
				if (! resultRooms.wasNull()) {
					//we have a location in the room
					room.setLocation(nodes.get(resultRooms.getInt("nodeID")));
				}
				rooms.put(resultRooms.getInt("roomID"),room); //image where?
			}
			resultRooms.close();

			// add rooms to nodes
			// TODO: save roomIDs in a map when nodes are originally loaded, then associate rooms with nodes without a new query
			Statement queryEdges = this.db_connection.createStatement();
			//ResultSet resultEdges = null;
			resultNodes = queryNodes.executeQuery(StoredProcedures.procRetrieveNodes());
			while (resultNodes.next()) {
//				PRINTLN("Loading edges and room for node " + resultNodes.getInt("nodeID"));
				int nodeID = resultNodes.getInt("nodeID");
				int roomID = resultNodes.getInt("roomID");
//				PRINTLN("Loading room "+roomID);
				if (!resultNodes.wasNull()) {
					nodes.get(nodeID).setRoom(rooms.getOrDefault(roomID, null));
				}
			}
			resultNodes.close();

			// add adjacency lists to nodes
			ResultSet resultEdges = queryEdges.executeQuery(StoredProcedures.procRetrieveEdges());
			while (resultEdges.next()) {
				int node1 = resultEdges.getInt("node1");
				int node2 = resultEdges.getInt("node2");
//				PRINTLN("Loading edge "+node1+" "+node2);
				nodes.get(node1).connect(nodes.get(node2));
			}
			resultEdges.close();

			// Close statements
			queryEdges.close();
			queryNodes.close();
			queryRooms.close();
		} catch (SQLException e){
			throw e;
		}
	}

	/**
	 * Replace the database with the contents of the given directory
	 *
	 * The previous contents of the database will be lost.
	 *
	 * If this function fails, the database may be corrupted.
	 *
	 * @param dir The directory to write to the database
	 *
	 * @throws DatabaseException If an error occurs when dealing with the database.
	 */
	// TODO: manually create a backup of the database before destroying it
	// (i.e. copy the db directory, then operate, and remove it if successful)
	public void destructiveSaveDirectory(Directory dir)
			throws DatabaseException {
		this.reInitSchema(); // drop tables, then recreate tables
		System.out.println("START SAVING");
		try {
			this.saveDirectory(dir); // insert directory info into tables
		} catch (SQLException e) {
			throw new DatabaseException("Failed to update database; database may be corrupt", e);
		}
		System.out.println("DONE SAVING");
	}

	/**
	 * Attempt to save a directory to the database
	 *
	 * @throws SQLException if any of the insertions trigger a SQLException
	 */
	private void saveDirectory(Directory dir)
			throws SQLException {
		Statement db = this.db_connection.createStatement();
		String query;
		for (Node n : dir.getNodes()) {
//			PRINTLN("Saving node "+n.hashCode());
			query = StoredProcedures.procInsertNode(n.hashCode(), n.getX(), n.getY(),
			                                        n.getFloor(), n.mapToRoom(Object::hashCode));
			db.executeUpdate(query);
		}

		for (Room r : dir.getRooms()) {
//			PRINTLN("Saving node "+r.hashCode());
			if(r.getLocation() != null) {
				query = StoredProcedures.procInsertRoomWithLocation(r.hashCode(),
																	r.getLocation().hashCode(),
																	r.getName(),
																	r.getDescription());
			} else {
				query = StoredProcedures.procInsertRoom(r.hashCode(),
														r.getName(),
														r.getDescription());
			}
			db.executeUpdate(query);
		}
		/* commented out because, again, kiosks are not yet implemented */
		if (dir.hasKiosk()) {
			query = StoredProcedures.procInsertKiosk(dir.getKiosk().hashCode());
			db.executeUpdate(query);
		}
//		System.out.println("kiosk saved");

		for (Node n : dir.getNodes()) {
//			PRINTLN("Saving edges for node "+n.hashCode());
			for (Node m : n.getNeighbors()) {
//				PRINTLN("Saving edge to "+m.hashCode());
				query = StoredProcedures.procInsertEdge(n.hashCode(), m.hashCode());
				db.executeUpdate(query);
			}
		}
//
//		for (Room n : dir.getRooms()) {
//			for (Node m : n.getLocation().getNeighbors()) {
//				query = StoredProcedures.procInsertEdge(n.hashCode(), m.hashCode());
//				db.executeUpdate(query);
//			}
//		}
//		System.out.println("room edges saved");

		for (Professional p : dir.getProfessionals()) {
			query = StoredProcedures.procInsertEmployee(
					p.hashCode(), p.getGivenName(), p.getSurname(), p.getTitle());
			db.executeUpdate(query);

			for (Room r : p.getLocations()) {
				query = StoredProcedures.procInsertEmployeeRoom(p.hashCode(), r.hashCode());
				db.executeUpdate(query);
			}
		}
		db.close();
	}

	//A test call to the database
	public void exampleQueries() {
		try {
			Statement statement = this.db_connection.createStatement();
			ResultSet results = statement.executeQuery("SELECT employeeSurname FROM Employees WHERE employeeTitle='Dr.'");
			System.out.println("\nSurname\n-------");
			while (results.next()) {
				System.out.println(results.getString("employeeSurname"));
			}
			results.close();
			results = statement.executeQuery( "SELECT roomName, employeeGivenName, employeeSurname"
					+ " FROM Employees NATURAL INNER JOIN EmployeeRooms");
			System.out.println("\nRoom Employee\n---- --------");
			while (results.next()) {
				System.out.println(results.getString("roomName")
						+ " " + results.getString("employeeGivenName")
						+ " " + results.getString("employeeSurname"));
			}
			results.close();
			statement.close();
		} catch (SQLException e) {
			System.err.println("Query failed");
			e.printStackTrace();
		}
	}

}
