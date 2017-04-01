/**
 * Created by WilsonWong on 3/19/2017.
 */

package main;
import java.sql.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * Created by Kenneth on 3/28/17.
 */

public class DatabaseController {

	private Connection db_connection;
	private String connection_string;

	public DatabaseController(){
		this.connection_string = "jdbc:derby:DB;create=true";
	}

	public DatabaseController(String connection_string){
		this.connection_string = connection_string;
	}

	//initialize the database
	//returns true if success, false if failure
	public boolean initDB(){
		db_connection = null;
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
		for(int i=0;i<schema.length;i++){
			Pattern matchTable = Pattern.compile("\\bCREATE\\b\\s\\bTABLE\\b\\s(\\w*)");
			Matcher matcher = matchTable.matcher(schema[i]);
			if(matcher.matches() == true){
				//we're making a table
				String table = matcher.group(1); //group zero = entire expression
				//drop the table if it exists
				try{
					initSchema.executeUpdate("DROP TABLE "+table);
				} catch (SQLException e) {
					System.out.println("Table "+table+" does not exist, continuing...");
				}
				//commit changes to the database
				try{
					db_connection.commit();
				} catch (SQLException e) {
					//fail if we can't commit changes
					e.printStackTrace();
					return false;
				}
				//make the table if it doesn't exist
				try{
					initSchema.executeUpdate(schema[i]);
				} catch (SQLException e) {
					System.out.println("Table"+table+"already exists, continuing...");
				}
				//commit changes to the database
				//close connection via statement
				try{
					db_connection.commit();
					initSchema.close();
				} catch (SQLException e) {
					//fail if we can't commit changes
					e.printStackTrace();
					return false;
				}
			}
		}
		return true;
	}

	//populates the database with initial data specified in the stored proc
	//database must have schema before running this
	//returns true if success, false if error
	private boolean initData(){
		boolean result;
		String[] data = StoredProcedures.getInitialData();
		String insertion = "";
		try {
			Statement insert = this.db_connection.createStatement();
			for  (String s : data) {
				insertion = s;
				insert.addBatch(s);
			}
			insert.executeBatch();
			this.db_connection.commit();
			insert.close();
		} catch (SQLException e) {
			System.out.println("SQL error while inserting sample data.");
			System.out.println("Failed on this insertion: " + insertion);
			e.printStackTrace();
			return false;
		}
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