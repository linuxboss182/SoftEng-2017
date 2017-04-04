package main;

import java.sql.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.HashMap;

import entities.Directory;
import entities.Node;
import entities.Room;

public class DatabaseController
{

	private Connection db_connection;
	private String connection_string;

	public DatabaseController(){
		this.connection_string = "jdbc:derby:DB;create=true";
	}

	public DatabaseController(String connection_string) {
		this.connection_string = connection_string;
	}

	//initialize the database
	//returns true if success, false if failure
	public boolean initDB() {
		this.db_connection = null;
		try {
			Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
		} catch(ClassNotFoundException e) {
			System.out.println("Java DB Driver not found. Add the classpath to your module.");
			return false;
		}
		System.out.println("Stuff works");

		try {
			// substitute your database name for myDB
			this.db_connection = DriverManager.getConnection(this.connection_string);
		} catch (SQLException e) {
			System.out.println("Connection failed. Check output console.");
			e.printStackTrace();
			return false;
		}
		System.out.println("Java DB connection established!");
		return true;
	}

	//initializes the database empty with the desired schema
	//returns true if success, false if error
	public boolean initSchema() {
		boolean result;
		Statement initSchema = null;
		try {
			initSchema = this.db_connection.createStatement();
		} catch (SQLException e) {
			//something's really bad if we get here
			//like "we don't have a database" bad
			e.printStackTrace();
			return false;
		}
		String[] schema = StoredProcedures.getSchema();
		//find our tables in the schema
		for (int i=0; i < schema.length; i++) {
			Pattern matchTable = Pattern.compile("\\bCREATE\\b\\s\\bTABLE\\b\\s(\\w*)");
			Matcher matcher = matchTable.matcher(schema[i]);
			boolean found = false;
			while (matcher.find() && found == false) {
				//we're making a table
				String table = matcher.group(1); //group zero = entire expression
				//drop the table if it exists
				try {
					initSchema.executeUpdate("DROP TABLE " + table);
				} catch (SQLException e) {
					System.out.println("Table " + table + " does not exist, continuing...");
				}
				//commit changes to the database
				try {
					this.db_connection.commit();
				} catch (SQLException e) {
					//fail if we can't commit changes
					e.printStackTrace();
					return false;
				}
				//make the table if it doesn't exist
				try {
					initSchema.executeUpdate(schema[i]);
				} catch (SQLException e) {
					System.out.println("Table" + table + " already exists, continuing...");
				}
				//commit changes to the database
				//close connection via statement
				try {
					this.db_connection.commit();
					initSchema.close();
				} catch (SQLException e) {
					//fail if we can't commit changes
					e.printStackTrace();
					return false;
				}
				found = true;
			}
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
			System.out.println("Failed to close connection");
			e.printStackTrace();
		 	return false;
		}
	}

	//adds a node to the database
	//returns true if success, false if failure
	public boolean addNode(Node node, int id){
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
	public Node getNodeAtID(int id){
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

	//returns all nodes(including rooms) as a directory
	public boolean getNodes(Directory directory){
		HashMap<Integer, Node> nodes = new HashMap<Integer, Node>();
		HashMap<Integer, Node> rooms = new HashMap<Integer, Node>();
		try{
			Statement query = this.db_connection.createStatement();
			ResultSet result = query.executeQuery(StoredProcedures.procRetrieveNodes());
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
			for(Node n: rooms.values()){
				directory.addRoom(n);
			}
			result.close();
			query.close();
			return true;
		} catch (SQLException e){
			return false;
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
			System.out.println("Query failed");
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
//			System.out.println("SQL error while inserting sample data.");
//			System.out.println("Failed on this insertion: " + insertion);
//			e.printStackTrace();
//			return false;
//		}
//		return true;
//	}

}
