package main;

import java.sql.*;
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
		System.err.println("DBC.init");
		boolean flag = this.initDB();
		if (! flag) {
			throw new DatabaseException("Connection failed");
		}

		SQLWarning warning;
		try {
			// db warning was issued if db existed before init
			warning = this.db_connection.getWarnings();
			// if null, no warning = new database
		} catch (SQLException e) {
			throw new DatabaseException("Failed to check connecton warnings", e);
		}

		if (warning == null) { //if null, DB exists
			flag = this.reInitSchema();
			if (! flag) {
				throw new DatabaseException("Failed to initialize database schema");
			}
		}
	}

	/** true if the database already exists */
	private boolean checkDBExists() {
		try {
			SQLWarning check = this.db_connection.getWarnings();
			return (check != null);
		} catch (SQLException e) {
			System.err.println("Failed to get JDBC warnings.");
			return false;
		}
	}

	//initialize the database
	//returns true if success, false if failure
	// (do not add db calls after the line indicated below)
	private boolean initDB() {
		System.err.println("DBC.initDB");
		try {
			Class.forName(DatabaseController.driver);
		} catch(ClassNotFoundException e) {
			System.err.println("Java DB Driver not found. Add the classpath to your module.");
			return false;
		}
		System.out.println("Stuff works");

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
		System.err.println("DBC.reInitSchema");
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

	//returns all nodes(including rooms) as a directory
	public boolean populateDirectory(Directory directory) {
		HashMap<Integer, Node> nodes = new HashMap<>();
		HashMap<Integer, Room> rooms = new HashMap<>();
		try{
			Statement query = this.db_connection.createStatement();
			ResultSet result = query.executeQuery(StoredProcedures.procRetrieveNodesAndRooms());

			//populate hash maps
			while(result.next()){
				if(result.getString("roomName") == null){
					//node, not room
					Node node = new Node(result.getDouble("nodeX"),
										 result.getDouble("nodeY"));

					nodes.put(result.getInt("nodeID"), node);
				} else {
					//room, not node
					Room room = new Room(result.getDouble("nodeX"),
										 result.getDouble("nodeY"),
										 result.getString("roomName"),
										 result.getString("roomDescription"));

					rooms.put(result.getInt("nodeID"),room); //image where?
				}
			}
			//populate directory
			for(Node n: nodes.values()){
				directory.addNode(n);
			}
			for(Room n: rooms.values()){
				directory.addRoom(n);
			}

			result.close();
			query.close();
			return true;
		} catch (SQLException e){
			return false;
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

		for (Room r : dir.getRooms()) {
			query = StoredProcedures.procInsertRoom(r.hashCode(), r.getName(), r.getDescription());
			db.executeUpdate(query);
			query = StoredProcedures.procInsertNode(r.hashCode(), r.getX(), r.getY());
			db.executeUpdate(query);
		}

		for (Professional p : dir.getProfessionals()) {
			query = StoredProcedures.procInsertEmployee(
					p.hashCode(), p.getGivenName(), p.getSurname(), p.getTitle());
			db.executeUpdate(query);

			for (Room r : p.getLocations()) {
				query = StoredProcedures.procInsertEmployeeRoom(p.hashCode(), r.hashCode());
				db.executeUpdate(query);
			}
		}

		for (Node n : dir.getNodes()) {
			for (Node m : n.getNeighbors()) {
				query = StoredProcedures.procInsertEdge(n.hashCode(), m.hashCode());
				db.executeUpdate(query);
			}
		}

		for (Room n : dir.getRooms()) {
			for (Node m : n.getNeighbors()) {
				query = StoredProcedures.procInsertEdge(n.hashCode(), m.hashCode());
				db.executeUpdate(query);
			}
		}
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
