/**
 * Really gross code
 */
// TODO: Fix this for iteration 1.

package main;
import java.sql.*;

public class DatabaseController
{

    private Connection db_connection;
    private String connection_string;

    private final String nodes = "CREATE TABLE Nodes ("
		+ "nodeID integer PRIMARY KEY , nodeX  integer , nodeY  integer)";

    private final String edges = "CREATE TABLE Edges ("
		+"node1 integer references Nodes(nodeID) NOT NULL"
		+" , node2 integer references Nodes(nodeID) NOT NULL)";

    private final String rooms = "CREATE TABLE Rooms ("
		+"roomName        varchar(200) PRIMARY KEY"
		+" , roomDescription varchar(1000)"
		+" , nodeID          integer references Nodes(nodeID))";

    private final String employees = "CREATE TABLE Employees ("
		+"employeeID        integer PRIMARY KEY"
		+" , employeeGivenName varchar(100)"
		+" , employeeSurname   varchar(100)"
		+" , employeeTitle     varchar(100))";

    private final String employee_rooms = "CREATE TABLE EmployeeRooms ("
		+"roomName   varchar(200) references Rooms(roomName)"
		+" , employeeID integer references Employees(employeeID)"
		+" , constraint EmployeeRooms_pk PRIMARY KEY (roomName, employeeID))";

    public DatabaseController() {
    	this.connection_string = "jdbc:derby:DB;create=true";
    }

    public DatabaseController(String connection_string) {
        this.connection_string = connection_string;
    }

    public boolean initConnection() {
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

    //initializes the database empty with the desired schema
    //returns true if success, false if error
    public boolean initDB() {
        try {
            int result;
            Statement init_schema = this.db_connection.createStatement();
			try {
				init_schema.executeUpdate("DROP TABLE EmployeeRooms");
			} catch (SQLException e) {
				System.out.println("Table does not exist, continuing...");
				// e.printStackTrace();
			}
			try {
				init_schema.executeUpdate("DROP TABLE Edges");
			} catch (SQLException e) {
				System.out.println("Table does not exist, continuing...");
				// e.printStackTrace();
			}
			try {
				init_schema.executeUpdate("DROP TABLE Rooms");
			} catch (SQLException e) {
				System.out.println("Table does not exist, continuing...");
				// e.printStackTrace();
			}
			try {
				init_schema.executeUpdate("DROP TABLE Employees");
			} catch (SQLException e) {
				System.out.println("Table does not exist, continuing...");
				// e.printStackTrace();
			}
			try {
				init_schema.executeUpdate("DROP TABLE Nodes");
			} catch (SQLException e) {
				System.out.println("Table does not exist, continuing...");
				// e.printStackTrace();
			}
	        db_connection.commit();
			try {
				result = init_schema.executeUpdate(nodes); //no need for result set here
			} catch (SQLException e) {

				System.out.println("Table already exists, continuing...");
				// e.printStackTrace();
			}
			try {
				result = init_schema.executeUpdate(edges);
			} catch (SQLException e) {
				System.out.println("Table already exists, continuing...");
				// e.printStackTrace();
			}
			try {
				result = init_schema.executeUpdate(rooms);
			} catch (SQLException e) {
				System.out.println("Table already exists, continuing...");
				// e.printStackTrace();
			}
			try {
				result = init_schema.executeUpdate(employees);
			} catch (SQLException e) {
 				System.out.println("Table already exists, continuing...");
				// e.printStackTrace();
			}
			try {
				result = init_schema.executeUpdate(employee_rooms);
			} catch (SQLException e) {
				System.out.println("Table already exists, continuing...");
				// e.printStackTrace();
			}
            db_connection.commit();
            init_schema.close();
        } catch(SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
    //TODO init data

    public boolean insertSampleData() {
		String[] insertions = {
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

	    String ss = "";
		try {
			Statement insert = this.db_connection.createStatement();
			for  (String s : insertions) {
				ss = s;
				insert.addBatch(s);
			}
			insert.executeBatch();
			this.db_connection.commit();
			insert.close();
		} catch (SQLException e) {
			System.out.println("SQL error while inserting sample data.");
			System.out.println("Failed on this insertion: " + ss);
			e.printStackTrace();
			return false;
		}
		return true;
    }

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

}
