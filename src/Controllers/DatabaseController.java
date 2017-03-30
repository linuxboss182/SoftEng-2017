/**
 * Created by WilsonWong on 3/19/2017.
 */

package Controllers;
import java.sql.*;

/**
 * Created by Kenneth on 3/28/17.
 */

public class DatabaseController {

    private Connection db_connection;
    private String connection_string;

    private final String nodes = "CREATE TABLE Nodes (\n" +
            "    nodeID integer PRIMARY KEY\n" +
            "  , nodeX  integer\n" +
            "  , nodeY  integer\n" +
            ");";
    private final String edges = "CREATE TABLE Edges (\n" +
            "    node1 integer references Nodes(nodeID) NOT NULL ON DELETE CASCADE\n" +
            "  , node2 integer references Nodes(nodeID) NOT NULL ON DELETE CASCADE\n" +
            ");";
    private final String rooms = "CREATE TABLE Rooms (\n" +
            "    roomName        varchar(200) PRIMARY KEY\n" +
            "  , roomDescription varchar(1000)\n" +
            "  , nodeID          integer references Nodes(nodeID)\n" +
            ");";
    private final String employees = "CREATE TABLE Employees (\n" +
            "    employeeID        integer PRIMARY KEY\n" +
            "  , employeeGivenName varchar(100)\n" +
            "  , employeeSurname   varchar(100)\n" +
            "  , employeeTitle     varchar(100)\n" +
            ");";

    private final String employee_rooms = "CREATE TABLE EmployeeRooms (\n" +
            "    roomName   varchar(200) references Rooms(roomName)\n" +
            "  , employeeID integer references Employees(employeeID)\n" +
            "  , constraint EmployeeRooms_pk PRIMARY KEY (roomName, employeeID)\n" +
            ");";

    public DatabaseController(){
        initDB(this.connection_string);
    }

    public DatabaseController(String connection_string){
        initDB(connection_string);
    }
    private boolean initDB(String connection_string){
        db_connection = null;
        this.connection_string = connection_string;

        try {
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
        } catch(ClassNotFoundException e) {
            System.out.println("Java DB Driver not found. Add the classpath to your module.");
            return false;
        }
        System.out.println("Stuff works");

        try {
            // substitute your database name for myDB
            db_connection = DriverManager.getConnection(this.connection_string);
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
    private boolean initSchema(){
        try {
            boolean result;
            Statement init_schema = this.db_connection.createStatement();
            result = init_schema.execute(nodes); //no need for result set here
            result = init_schema.execute(edges);
            result = init_schema.execute(rooms);
            result = init_schema.execute(employees);
            result = init_schema.execute(employee_rooms);
        } catch(SQLException e){
            e.printStackTrace();
            return false;
        }
        return true;
    }
    //TODO init data

}
