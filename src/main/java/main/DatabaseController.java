package main;

import java.sql.*;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.HashMap;

import entities.Directory;
import entities.Node;
import entities.Professional;
import entities.Room;

public class DatabaseController
{
	private static String driver = "org.apache.derby.jdbc.EmbeddedDriver";

	private Connection db_connection;
	private String connection_string;

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
			//drop the table if it exists
			try {
				initSchema.executeUpdate(table);
			} catch (SQLException e) {
				System.err.println("Failed statement: " + table);
				System.err.println(e.getMessage());
			}
		}

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
			e.printStackTrace();
			return false;
		}

		//stop once we find the first match(assume one create statement per string)
		return true;
	}

	//close the connection to the database
	//returns true if success, false if failure
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


	//adds a node to the database
	//returns true if success, false if failure
	private boolean addNode(Node node, int id){
		try {
			Statement insert = this.db_connection.createStatement();
			//do some sort of autoincrement
			insert.execute(StoredProcedures.procInsertNode(id, node.getX(),node.getY()));
			insert.close();
			return true;
		} catch (SQLException e) {
			return false;
		}
	}

	//attempts to retrieve a node at a given id
	//returns null if failure
	private Node getNodeAtID(int id){
		try{
			Statement query = this.db_connection.createStatement();
			ResultSet result = query.executeQuery(StoredProcedures.procRetrieveNodeID(id));
			//figure out adjacencies
			Node node = new Node(result.getDouble("nodeX"), result.getDouble("nodeY"));
			result.close();
			query.close();
			return node;
		} catch (SQLException e){
			return null;
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
		HashMap<Integer, Node> nodes = new HashMap<>();
		HashMap<Integer, Room> rooms = new HashMap<>();
		HashMap<Integer, Professional> professionals = new HashMap<>();
		Integer kioskID = null; // (should be Optional<Integer>)
		try {
			//retrieve nodes and rooms
			this.retrieveNodes(nodes, rooms);
			//find all them professionals
			this.retrieveProfessionals(rooms, professionals);
//			kioskID = this.retrieveKiosk();
		} catch (SQLException e){
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
//		if (kioskID != null) {
//			directory.setKiosk(rooms.get(kioskID));
//		}

		return true;
	}


	private Integer retrieveKiosk() throws SQLException { // (should return Optional<Integer>)
		Statement query = this.db_connection.createStatement();
		ResultSet result = query.executeQuery(StoredProcedures.procRetrieveKiosk());
		if (result.next()) {
			int i = result.getInt("nodeID");
			if (result.wasNull()) {
				return null;
			} else {
				return i;
			}
		} else {
			return null;
		}
	}

	/**Retrieves all employees with their location data populated(among other things)
	 *
	 * @param rooms A hash map of all rooms in the database
	 * @param professionals The hash map of professionals to populate
	 */
	private void retrieveProfessionals(HashMap<Integer, Room> rooms, HashMap<Integer, Professional> professionals) throws SQLException{
		HashMap<Integer, Room> profRooms = new HashMap<>();
		try {
			Statement queryProfRooms = this.db_connection.createStatement();
			Statement queryProfessionals = this.db_connection.createStatement();
			ResultSet resultProfRooms = queryProfRooms.executeQuery(StoredProcedures.procRetrieveEmployeeRooms());
			ResultSet resultProfessionals = queryProfessionals.executeQuery(StoredProcedures.procRetrieveEmployees());

			//find all them professionals
			while (resultProfessionals.next()) {
				Professional professional = new Professional(resultProfessionals.getString("employeeGivenName"),
						resultProfessionals.getString("employeeSurname"),
						resultProfessionals.getString("employeeTitle"));
				//look for any locations we might have
				while (resultProfRooms.next()) {
					if (resultProfessionals.getInt("employeeID") == resultProfRooms.getInt("employeeID")) {
						//we have at least one room
						professional.addLocation(rooms.get(resultProfRooms.getInt("nodeID")));
					}
				}
				//add to hashmap
				professionals.put(resultProfessionals.getInt("employeeID"), professional);
			}
			queryProfRooms.close();
			queryProfessionals.close();
			resultProfRooms.close();
			resultProfessionals.close();
		} catch (SQLException e){
			throw e;
		}
	}

	/**Retrieves nodes and rooms from the database and populates the given hash maps
	 *
	 * @param nodes The map of nodes to populate
	 * @param rooms The map of rooms to populate
	 */
	private void retrieveNodes(HashMap<Integer, Node> nodes, HashMap<Integer, Room> rooms) throws SQLException{
		try {
			Statement queryNodes = this.db_connection.createStatement();
			Statement queryEdges = this.db_connection.createStatement();
			ResultSet resultNodes = queryNodes.executeQuery(StoredProcedures.procRetrieveNodesAndRooms());
			ResultSet resultEdges = queryEdges.executeQuery(StoredProcedures.procRetrieveEdges());
			//populate initial objects
			while(resultNodes.next()){
				if(resultNodes.getString("roomName") == null){
					//node, not room
					Node node = new Node(resultNodes.getDouble("nodeX"),
							resultNodes.getDouble("nodeY"));
					nodes.put(resultNodes.getInt("nodeID"), node);
				} else {
					//room, not node
					Room room = new Room(resultNodes.getDouble("nodeX"),
							resultNodes.getDouble("nodeY"),
							resultNodes.getString("roomName"),
							resultNodes.getString("roomDescription"));
					rooms.put(resultNodes.getInt("nodeID"),room); //image where?
				}
			}
			resultNodes.close();

			//populate adjacency lists
			resultNodes = queryNodes.executeQuery(StoredProcedures.procRetrieveNodesAndRooms());
			while (resultNodes.next()) {
				while (resultEdges.next()) {
					if (resultEdges.getInt("node1") == resultNodes.getInt("nodeID")) {
						//we have adjacent nodes
						//find the initial node, add the edge
						nodes.getOrDefault(resultNodes.getInt("nodeID"),
								rooms.get(resultNodes.getInt("nodeID")))
								.connect(nodes.getOrDefault(resultEdges.getInt("node2"),
										rooms.get(resultEdges.getInt("node2"))));
						//I'm aware this looks like arse
					}
				}
			}
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
		try {
			this.reInitSchema(); // drop tables, then recreate tables
			this.saveDirectory(dir); // insert directory info into tables
		} catch (SQLException e) {
			throw new DatabaseException("Failed to update database; database may be corrupt", e);
		}
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
			query = StoredProcedures.procInsertNode(n.hashCode(), n.getX(), n.getY());
			db.executeUpdate(query);
		}
		System.out.println("nodes saved");

		for (Room r : dir.getRooms()) { // the order of these queries is important
			query = StoredProcedures.procInsertNode(r.hashCode(), r.getX(), r.getY());
			db.executeUpdate(query);
			query = StoredProcedures.procInsertRoom(r.hashCode(), r.getName(), r.getDescription());
			db.executeUpdate(query);
		}
		System.out.println("rooms saved");

//		if (dir.hasKiosk()) {
//			Room n = dir.getKiosk();
//			query = StoredProcedures.procInsertKiosk(n.hashCode());
//			db.executeUpdate(query);
//		}
//		System.out.println("kiosk saved");

		for (Node n : dir.getNodes()) {
			for (Node m : n.getNeighbors()) {
				query = StoredProcedures.procInsertEdge(n.hashCode(), m.hashCode());
				db.executeUpdate(query);
			}
		}
		System.out.println("edges saved");

		for (Room n : dir.getRooms()) {
			for (Node m : n.getNeighbors()) {
				query = StoredProcedures.procInsertEdge(n.hashCode(), m.hashCode());
				db.executeUpdate(query);
			}
		}
		System.out.println("room edges saved");

		for (Professional p : dir.getProfessionals()) {
			query = StoredProcedures.procInsertEmployee(
					p.hashCode(), p.getGivenName(), p.getSurname(), p.getTitle());
			db.executeUpdate(query);

			for (Room r : p.getLocations()) {
				query = StoredProcedures.procInsertEmployeeRoom(p.hashCode(), r.hashCode());
				db.executeUpdate(query);
			}
		}

		System.out.println("professionals saved");
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

//	public void saveDirectoryToDatabase(Directory dir) {
//		Statement query = this.db_connection.createStatement();
//
//		query.;
//
//		query.close();
//	}

	//This code is broken, the batch executes in reverse order. Unneeded at this time, was use for testing.
//	//populates the database with initial data specified in the stored proc
//	//database must have schema before running this
//	//returns true if success, false if error
//	public boolean initData(){
//		boolean result;
//		String[] data = StoredProcedures.getInitialData();
//		String insertion = "";
//		try {
//			Statement insert = this.db_connection.createStatement();
//			for  (String s : data) {
//				insertion = s;
//				insert.addBatch(s);
//			}
//			insert.executeBatch();
//			this.db_connection.commit();
//			System.out.println("Success!");
//			insert.close();
//		} catch (SQLException e) {
//			System.err.println("SQL error while inserting sample data.");
//			System.err.println("Failed on this insertion: " + insertion);
//			e.printStackTrace();
//			return false;
//		}
//		return true;
//	}

}
