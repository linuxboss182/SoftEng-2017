package main.database;

import java.sql.*;
import java.util.Map;
import java.util.HashMap;

import entities.*;

// Feel free to remove all the commented-out PRINTs and PRINTLNs once everything works

/**
 * Class for saving to and loading from the database
 *
 * Package methods:
 * - DatabaseLoader(): Constructor
 * - getDirectory():
 * - populateDirectory(): builds a directory by loading from the database
 * - destructiveSaveDirectory(Directory): empties the database, then saves the directory
 *                                        to the database
 */
class DatabaseLoader
{
	private static String driver = "org.apache.derby.jdbc.EmbeddedDriver";

	private DatabaseConnector dbConn;
	private Connection db_connection;

	// Debugging functions
//	private static void PRINTLN(Object o) {
//		System.out.println(o);
//	}
//	private static void PRINT(Object o) {
//		System.out.print(o);
//	}

	DatabaseLoader(DatabaseConnector dbConn) {
		this.dbConn = dbConn;
		this.db_connection = dbConn.getConnection();
	}

	Directory getDirectory() {
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
	private boolean populateDirectory(Directory directory) {
		Map<Integer, Room> rooms = new HashMap<>();
		Integer kioskID = null; // (should be Optional<Integer>) not in use
		try {
			//retrieve nodes and rooms
			this.retrieveNodesAndRooms(directory, rooms);
			//find all them professionals
			this.retrieveProfessionals(directory, rooms);
			//retrieve all user data
			this.retrieveUserData(directory);
			kioskID = this.retrieveKiosk();
		} catch (SQLException e){
			e.printStackTrace();
			System.err.println("A SQL Exception occured");
			return false;
		}

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
	 * @param directory The directory to populate
	 * @param rooms A hash map of all rooms in the database
	 */
	private void retrieveProfessionals(Directory directory, Map<Integer, Room> rooms) throws SQLException{
		HashMap<Integer, Professional> professionals = new HashMap<>();
		HashMap<Integer, Room> profRooms = new HashMap<>();
		try {
			Statement queryProfessionals = this.db_connection.createStatement();
			ResultSet resultProfessionals = queryProfessionals.executeQuery(StoredProcedures.procRetrieveEmployees());

			//find all professionals
			while (resultProfessionals.next()) {
				Professional professional = directory.addNewProfessional(
						resultProfessionals.getString("employeeGivenName"),
						resultProfessionals.getString("employeeSurname"),
						resultProfessionals.getString("employeeTitle"));
				//add to hashmap
				directory.addProfessional(professional);
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
				directory.addRoomToProfessional(room, professional);
			}
			resultProfRooms.close();
			queryProfRooms.close();
		} catch (SQLException e){
			throw e;
		}
	}

	/**Retrieves nodes and rooms from the database and populates the given hash maps
	 *
	 * @param directory The directory to populate
	 * @param rooms The map of rooms to populate
	 */
	private void retrieveNodesAndRooms(Directory directory, Map<Integer, Room> rooms) throws SQLException {
		Map<Integer, Node> nodes = new HashMap<>();
		try {
			//populate Nodes
			Statement queryNodes = this.db_connection.createStatement();
			ResultSet resultNodes = queryNodes.executeQuery(StoredProcedures.procRetrieveNodes());
			while (resultNodes.next()) {
//				PRINTLN("Loading node " + resultNodes.getInt("nodeID"));
				Node node = directory.addNewNode(resultNodes.getDouble("nodeX"),
						resultNodes.getDouble("nodeY"),
						resultNodes.getInt("floor"),
						resultNodes.getString("buildingName"),
						resultNodes.getBoolean("isRestricted"));
				nodes.put(resultNodes.getInt("nodeID"), node);
				directory.addNode(node);
			}
			resultNodes.close();

			// populate Rooms
			Statement queryRooms = this.db_connection.createStatement();
			ResultSet resultRooms = queryRooms.executeQuery(StoredProcedures.procRetrieveRooms());
			while (resultRooms.next()) {
//				PRINTLN("Loading room " + resultRooms.getInt("roomID"));
				Room room = directory.addNewRoom(resultRooms.getString("roomName"),
						resultRooms.getString("roomDisplayName"),
						resultRooms.getString("roomDescription"),
						resultRooms.getDouble("labelX"),
						resultRooms.getDouble("labelY"));
				room.setType(RoomType.valueOf(resultRooms.getString("roomType")));
				directory.addRoom(room);
				int nodeID = resultRooms.getInt("nodeID");
				if (! resultRooms.wasNull()) {
					//we have a location in the room
					directory.setRoomLocation(room, nodes.get(nodeID));
				}
				rooms.put(resultRooms.getInt("roomID"),room); //image where?
			}
			resultRooms.close();

			// add adjacency lists to nodes
			Statement queryEdges = this.db_connection.createStatement();
			ResultSet resultEdges = queryEdges.executeQuery(StoredProcedures.procRetrieveEdges());
			while (resultEdges.next()) {
				int node1 = resultEdges.getInt("node1");
				int node2 = resultEdges.getInt("node2");
//				PRINTLN("Loading edge "+node1+" "+node2);
				directory.connectNodes(nodes.get(node1), nodes.get(node2));
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

	/**Retrieves users and password hashes from the database and populates the given hash maps
	 *
	 * @param directory The directory to populate
	 */

	private void retrieveUserData(Directory directory) throws SQLException{
		try {
			//populate Users
			Statement queryUsers = this.db_connection.createStatement();
			ResultSet resultUsers = queryUsers.executeQuery(StoredProcedures.procRetrieveUsers());
			while (resultUsers.next()) {
				directory.addUser(resultUsers.getString("userID"),
						resultUsers.getString("passHash"),
						resultUsers.getString("permission"));
			}
			resultUsers.close();
			queryUsers.close();
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
	void destructiveSaveDirectory(Directory dir)
			throws DatabaseException {
		this.dbConn.reInitSchema(); // drop tables, then recreate tables
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
					n.getFloor(), n.mapToRoom(Object::hashCode),
					n.getBuildingName(),
					n.isRestricted());
			db.executeUpdate(query);
		}

		for (Room r : dir.getRooms()) {
//			PRINTLN("Saving node "+r.hashCode())
			if(r.getLocation() != null) {
				query = StoredProcedures.procInsertRoomWithLocation(r.hashCode(),
						r.getLocation().hashCode(),
						r.getName(),
						r.getDisplayName(),
						r.getDescription(),
						r.getLabelOffsetX(),
						r.getLabelOffsetY(),
						r.getType().name());
			} else {
				query = StoredProcedures.procInsertRoom(r.hashCode(),
						r.getName(),
						r.getDisplayName(),
						r.getDescription(),
						r.getLabelOffsetX(),
						r.getLabelOffsetY(),
						r.getType().name());
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
			for (Node m : dir.getNodeNeighbors(n)) {
//				PRINTLN("Saving edge to "+m.hashCode());
				query = StoredProcedures.procInsertEdge(n.hashCode(), m.hashCode());
				db.executeUpdate(query);
			}
		}

		//save professionals
		for (Professional p : dir.getProfessionals()) {
			query = StoredProcedures.procInsertEmployee(
					p.hashCode(), p.getGivenName(), p.getSurname(), p.getTitle());
			db.executeUpdate(query);

			for (Room r : p.getLocations()) {
				query = StoredProcedures.procInsertEmployeeRoom(p.hashCode(), r.hashCode());
				db.executeUpdate(query);
			}
		}

//		//save user data
//		for (int i=0;i<dir.getUsers().toArray().length;i++){
//			query = StoredProcedures.procInsertUser(dir.getUsers().toArray()[i].toString(),
//					dir.getPassHashes().toArray()[i].toString(),
//					dir.getPermissions().toArray()[i].toString());

		for (Map.Entry<String, Account> user : dir.getUsers().entrySet()) {
			Account thisAccount = user.getValue();
			query = StoredProcedures.procInsertUser(thisAccount.getName(),
													thisAccount.getPassword(),
													thisAccount.getPermissions());
			db.executeUpdate(query);
		}

		db.close();
	}

	//A test call to the database
	@Deprecated
	private void exampleQueries() {
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
