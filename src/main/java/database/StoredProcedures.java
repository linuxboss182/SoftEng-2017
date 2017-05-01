package database;

import java.util.List;
import java.util.Arrays;

public class StoredProcedures
{
	//initial schema to setup the database
	//Define tables here in the order they should be created:

	private static final List<String> schema = Arrays.asList(
			"CREATE TABLE Nodes ("
					+ " nodeID integer PRIMARY KEY "
					+ " , nodeX  double precision NOT NULL "
					+ ", nodeY  double precision NOT NULL "
					+ ", buildingName varchar(200) NOT NULL "
					+ ", floor integer NOT NULL "
					+ ", isRestricted boolean NOT NULL "
					+  ", roomID integer)",/*references Rooms(roomID))",*/ // foreign key would cause drop problems
			"CREATE TABLE Edges ("
					+" node1 integer references Nodes(nodeID) ON DELETE CASCADE"
					+" , node2 integer references Nodes(nodeID) ON DELETE CASCADE"
					+" , constraint Edges_pk PRIMARY KEY (node1, node2))",
			"CREATE TABLE Rooms ("
					+" roomID             integer PRIMARY KEY"
					+" , roomName         varchar(200) NOT NULL"
					+" , roomDisplayName    varchar(50) NOT NULL"
					+" , roomDescription varchar(1000)"
					+" , labelX double precision NOT NULL"
					+" , labelY double precision NOT NULL"
					+" , nodeID          integer references Nodes(nodeID) ON DELETE SET NULL"
					+" , roomType   varchar(100) NOT NULL)",
			"CREATE TABLE Employees ("
					+" employeeID        integer PRIMARY KEY"
					+" , employeeGivenName varchar(100)"
					+" , employeeSurname   varchar(100)"
					+" , employeeTitle     varchar(100))",
			"CREATE TABLE EmployeeRooms ("
					+"roomID   integer references Rooms(roomID) ON DELETE CASCADE"
					+" , employeeID integer references Employees(employeeID) ON DELETE CASCADE"
					+" , constraint EmployeeRooms_pk PRIMARY KEY (roomID, employeeID))",
			"CREATE TABLE Kiosk (roomID integer references Rooms(roomID) NOT NULL)",
			"CREATE TABLE Users ("
					+"userID    varchar(100) PRIMARY KEY"
					+" , passHash  varchar(100)"
					+" , permission    varchar(100))",
			"CREATE TABLE TimeoutDuration (" + // TODO: REVIEW -TED
					"duration integer"
					+ ")"
	);

	private static final List<String> drops = Arrays.asList(
			"DROP TABLE Kiosk",
			"DROP TABLE EmployeeRooms",
			"DROP TABLE Employees",
			"DROP TABLE Rooms",
			"DROP TABLE Edges",
			"DROP TABLE Nodes",
			"DROP TABLE Users",
			"DROP TABLE TimeoutDuration"
	);

	//initial data that will be in the database upon construction
	//breaks things don't run
	private static final String[] initialData = {
			"INSERT INTO Nodes (nodeX, nodeY, nodeid) VALUES(3600, 700, 1)",
			"INSERT INTO Nodes (nodeX, nodeY, nodeid) VALUES(3700, 700, 2)",
			"INSERT INTO Nodes (nodeX, nodeY, nodeid) VALUES(3800, 800, 3)",
			"INSERT INTO Nodes (nodeX, nodeY, nodeid) VALUES(3900, 900, 4)",
			"INSERT INTO Nodes (nodeX, nodeY, nodeid) VALUES(3675, 1150, 5)",
			"INSERT INTO Nodes (nodeX, nodeY, nodeid) VALUES(3700, 1050, 6)",
			"INSERT INTO Nodes (nodeX, nodeY, nodeid) VALUES(3650, 1050, 7)",
			"INSERT INTO Nodes (nodeX, nodeY, nodeid) VALUES(3400, 1200, 8)",
			"INSERT INTO Nodes (nodeX, nodeY, nodeid) VALUES(3650, 1300, 9)",
			"INSERT INTO Nodes (nodeX, nodeY, nodeid) VALUES(3650, 1400, 10)",
			"INSERT INTO Nodes (nodeX, nodeY, nodeid) VALUES(3650, 1350, 11)",
			"INSERT INTO Nodes (nodeX, nodeY, nodeid) VALUES(3600, 1400, 12)",
			"INSERT INTO Nodes (nodeX, nodeY, nodeid) VALUES(3500, 1500, 13)",
			"INSERT INTO Nodes (nodeX, nodeY, nodeid) VALUES(3400, 1700, 14)",
			"INSERT INTO Nodes (nodeX, nodeY, nodeid) VALUES(3500, 1900, 15)",
			"INSERT INTO Nodes (nodeX, nodeY, nodeid) VALUES(1700, 2100, 16)",
			"INSERT INTO Nodes (nodeX, nodeY, nodeid) VALUES(1900, 2000, 17)",
			"INSERT INTO Nodes (nodeX, nodeY, nodeid) VALUES(2300, 2200, 18)",
			"INSERT INTO Nodes (nodeX, nodeY, nodeid) VALUES(2400, 2200, 19)",
			"INSERT INTO Nodes (nodeX, nodeY, nodeid) VALUES(3700, 1300, 20)",
			"INSERT INTO Nodes (nodeX, nodeY, nodeid) VALUES(3900, 2200, 21)",

			"INSERT INTO Edges VALUES(1, 9)",
			"INSERT INTO Edges VALUES(9, 1)",
			"INSERT INTO Edges VALUES(8, 5)",
			"INSERT INTO Edges VALUES(5, 8)",
			"INSERT INTO Edges VALUES(7, 12)",
			"INSERT INTO Edges VALUES(12, 7)",
			"INSERT INTO Edges VALUES(16, 3)",
			"INSERT INTO Edges VALUES(3, 16)",
			"INSERT INTO Edges VALUES(20, 2)",
			"INSERT INTO Edges VALUES(2, 20)",
			"INSERT INTO Edges VALUES(13, 21)",
			"INSERT INTO Edges VALUES(21, 13)",

			"INSERT INTO Rooms (roomName, nodeID, roomDescription) VALUES('4A', 1, 'Storage of Patients')",
			"INSERT INTO Rooms (roomName, nodeID, roomDescription) VALUES('4B', 2, 'Storage of Patients')",
			"INSERT INTO Rooms (roomName, nodeID, roomDescription) VALUES('4C', 3, 'Storage of Patients')",
			"INSERT INTO Rooms (roomName, nodeID, roomDescription) VALUES('4D', 4, 'Storage of Patients')",
			"INSERT INTO Rooms (roomName, nodeID, roomDescription) VALUES('Atrium Ele', 5, 'Elevators Room')",
			"INSERT INTO Rooms (roomName, nodeID, roomDescription) VALUES('4RestroomAUnisex', 6, 'A bathroom')",
			"INSERT INTO Rooms (roomName, nodeID, roomDescription) VALUES('4RestroomBUnisex', 7, 'A bathroom')",
			"INSERT INTO Rooms (roomName, nodeID, roomDescription) VALUES('4E', 8, 'Storage of Patients')",
			"INSERT INTO Rooms (roomName, nodeID, roomDescription) VALUES('4L', 9, 'Storage of Patients')",
			"INSERT INTO Rooms (roomName, nodeID, roomDescription) VALUES('4K', 10, 'Storage of Patients')",
			"INSERT INTO Rooms (roomName, nodeID, roomDescription) VALUES('4G', 11, 'Storage of Patients')",
			"INSERT INTO Rooms (roomName, nodeID, roomDescription) VALUES('4H', 12, 'Storage of Patients')",
			"INSERT INTO Rooms (roomName, nodeID, roomDescription) VALUES('4I', 13, 'Storage of Patients')",
			"INSERT INTO Rooms (roomName, nodeID, roomDescription) VALUES('4J', 14, 'Storage of Patients')",
			"INSERT INTO Rooms (roomName, nodeID, roomDescription) VALUES('Hillside Ele', 15, 'Elevators Room')",
			"INSERT INTO Rooms (roomName, nodeID, roomDescription) VALUES('TymanConfCent', 16, 'Tyman Conference Center')",
			"INSERT INTO Rooms (roomName, nodeID, roomDescription) VALUES('4N', 17, 'Maintenance Closet')",
			"INSERT INTO Rooms (roomName, nodeID, roomDescription) VALUES('4RestroomMale', 18, 'Male Bathroom')",
			"INSERT INTO Rooms (roomName, nodeID, roomDescription) VALUES('4RestroomFemale', 19, 'Female Bathroom')",
			"INSERT INTO Rooms (roomName, nodeID, roomDescription) VALUES('4F', 20, 'Storage of Patients')",
			"INSERT INTO Rooms (roomName, nodeID, roomDescription) VALUES('4S', 21, 'Storage of Patients')",

			"INSERT INTO Employees (employeeGivenName, employeeSurname, employeeID, employeeTitle) VALUES('Carla', 'Green', 1, 'Dr.')",
			"INSERT INTO Employees (employeeGivenName, employeeSurname, employeeID, employeeTitle) VALUES('Silas', 'Odoom', 2, 'Dr.')",
			"INSERT INTO Employees (employeeGivenName, employeeSurname, employeeID, employeeTitle) VALUES('Peter', 'Wong', 3, 'Dr.')",
			"INSERT INTO Employees (employeeGivenName, employeeSurname, employeeID, employeeTitle) VALUES('Willis', 'Smith', 4, 'Dr.')",
			"INSERT INTO Employees (employeeGivenName, employeeSurname, employeeID, employeeTitle) VALUES('Jess', 'Johnson', 5, 'NURSE')",
			"INSERT INTO Employees (employeeGivenName, employeeSurname, employeeID, employeeTitle) VALUES('Bill', 'Willis', 6, 'Dr.')",
			"INSERT INTO Employees (employeeGivenName, employeeSurname, employeeID, employeeTitle) VALUES('Vlad', 'Jones', 7, 'Dr.')",
			"INSERT INTO Employees (employeeGivenName, employeeSurname, employeeID, employeeTitle) VALUES('Oscar', 'Lee', 8, 'Dr.')",
			"INSERT INTO Employees (employeeGivenName, employeeSurname, employeeID, employeeTitle) VALUES('Edward', 'Wilson', 9, 'NURSE')",
			"INSERT INTO Employees (employeeGivenName, employeeSurname, employeeID, employeeTitle) VALUES('Louis', 'Moore', 10, 'NURSE')",
			"INSERT INTO Employees (employeeGivenName, employeeSurname, employeeID, employeeTitle) VALUES('Dean', 'Thomas', 11, 'Dr.')",
			"INSERT INTO Employees (employeeGivenName, employeeSurname, employeeID, employeeTitle) VALUES('Albert', 'Young', 12, 'Dr.')",

			"INSERT INTO EmployeeRooms Values('4A', 1)",
			"INSERT INTO EmployeeRooms Values('4B', 2)",
			"INSERT INTO EmployeeRooms Values('4C', 3)",
			"INSERT INTO EmployeeRooms Values('4D', 4)",
			"INSERT INTO EmployeeRooms Values('4E', 8)",
			"INSERT INTO EmployeeRooms Values('4L', 9)",
			"INSERT INTO EmployeeRooms Values('4K', 10)",
			"INSERT INTO EmployeeRooms Values('4G', 11)",
			"INSERT INTO EmployeeRooms Values('4H', 12)",
			"INSERT INTO EmployeeRooms Values('4I', 5)",
			"INSERT INTO EmployeeRooms Values('4J', 6)",
			"INSERT INTO EmployeeRooms Values('TymanConfCent', 5)",
			"INSERT INTO EmployeeRooms Values('4F', 7)",
			"INSERT INTO EmployeeRooms Values('4S', 3)"
	};


	public static List<String> getSchema() {
		return StoredProcedures.schema;
	}

	public static List<String> getDrops() {
		return StoredProcedures.drops;
	}

	public static String[] getInitialData(){
		return StoredProcedures.initialData;
	}

	//Stored procedures below this line
	//format: procOperationDataQualifier

	/* **** Insertion procedures **** */

	public static String procInsertNode(int nodeID, double nodeX, double nodeY, int floor, Integer roomID, String buildingName, boolean isRestricted){
		//query needs work
		buildingName = sanitize(buildingName);
		return "INSERT INTO Nodes (nodeID, nodeX, nodeY, floor, isRestricted, roomID, buildingName) VALUES("+nodeID+", "+nodeX+", "+nodeY+", "+floor+", "+isRestricted+", "+roomID+", '"+buildingName+"')";
	}

	public static String procInsertRoom(int roomID, String roomName, String roomDisplayName, String roomDescription, double labelX, double labelY, String roomType){
		roomName = sanitize(roomName);
		roomDisplayName = sanitize(roomDisplayName);
		roomDescription = sanitize(roomDescription);
		roomType = sanitize(roomType);
		return "INSERT INTO Rooms (roomName, roomDisplayName, roomDescription, roomID, labelX, labelY, roomType) VALUES('"+roomName
				+"','"+roomDisplayName+"','"+roomDescription+"',"+roomID+","+ labelX +","+labelY+ ",'"+roomType+"')";
	}

	public static String procInsertRoomWithLocation(int roomID, int nodeID, String roomName, String roomDisplayName, String roomDescription, double labelX, double labelY, String roomType){
		roomName = sanitize(roomName);
		roomDescription = sanitize(roomDescription);
		return "INSERT INTO Rooms (roomName, roomDisplayName, roomDescription, nodeID, roomID, labelX, labelY, roomType) VALUES('"+roomName
				+"','"+roomDisplayName+"','"+roomDescription+"',"+nodeID+","+roomID+","+ labelX +","+labelY+",'"+roomType+ "')";
	}

	public static String procInsertEdge(int node1, int node2){
		//query needs work
		return "INSERT INTO Edges (node1, node2) VALUES("+node1+","+node2+")";
	}

	public static String procInsertEmployee(int employeeID, String givenName,
	                                        String surname, String employeeTitle){
		givenName = sanitize(givenName);
		surname = sanitize(surname);
		employeeTitle = sanitize(employeeTitle);
		return "INSERT INTO Employees(employeeID,employeeGivenName,employeeSurname,employeeTitle) "
				+ "VALUES("+employeeID+", '"+givenName+"', '"+surname+"', '"+employeeTitle+"')";
	}

	public static String procInsertEmployeeRoom(int employeeID, int roomID){
		//query needs work
		return "INSERT INTO EmployeeRooms(employeeID, roomID) VALUES("+employeeID+","+roomID+")";
	}

	public static String procInsertKiosk(int roomID){
		return "INSERT INTO Kiosk (roomID) VALUES (" + roomID + ")";
	}

	// TODO: REVIEW -TED
	public static String procInsertTimeoutDuration(long timeoutDuration) {
		return "INSERT INTO TimeoutDuration (duration) VALUES (" + timeoutDuration + ")";
	}

	/* **** Retrieval procedures **** */

	public static String procRetrieveNodes(){
		//query needs work
		return "SELECT * FROM Nodes";
	}

	public static String procRetrieveNodeID(int id){
		//query needs work
		return "SELECT * FROM Nodes WHERE nodeID='"+id+"'";
	}

	public static String procRetrieveRooms(){
		//query needs work
		return "SELECT * FROM Rooms";
	}

	public static String procRetrieveRoomName(String roomName){
		//query needs work
		roomName = sanitize(roomName);
		return "SELECT * FROM Nodes WHERE roomName ='"+roomName+"'";
	}

	public static String procRetrieveEdges(){
		//query needs work
		return "SELECT * FROM Edges";
	}

	public static String procRetrieveEdge(int node1,int node2){
		//query needs work
		return "SELECT * FROM Nodes WHERE node1 ='"+node1+"' AND node2 = '"+node2+"'";
	}

	public static String procRetrieveEmployees(){
		//query needs work
		return "SELECT * FROM Employees";
	}

	public static String procRetrieveEmployeeID(int id){
		//query needs work
		return "SELECT * FROM Employee WHERE employeeID='"+id+"'";
	}

	public static String procRetrieveEmployeeRooms(){
		//query needs work
		return "SELECT * FROM EmployeeRooms";
	}

	public static String procRetrieveEmployeeRoom(int employeeID, String roomID){
		//query needs work
		return "SELECT * FROM EmployeeRooms WHERE employeeID='"+employeeID+"' AND roomID = '"+roomID+"'";
	}

	public static String procRetrieveNodesAndRooms(){
		return "SELECT * FROM Nodes LEFT OUTER JOIN Rooms on rooms.NODEID = nodes.NODEID";
	}

	// TODO: REVIEW -TED
	public static String procRetrieveTimeoutDuration() {
		return "SELECT duration FROM TimeoutDuration";
	}

	private static String sanitize(String str){
		return str.replace("'","''");
	}

	public static String procRetrieveKiosk() {
		return "SELECT roomID FROM Kiosk";
	}

	public static String procRetrieveUsers(){
		return "SELECT * FROM Users";
	}

	public static String procInsertUser(String userID, String passHash, String permission){
		userID = sanitize(userID);
		passHash = sanitize(passHash);
		permission = sanitize(permission);
		return "INSERT INTO Users(userID, passHash, permission) VALUES('"+userID+"', '"+passHash+"', '"+permission+"')";
	}
}
