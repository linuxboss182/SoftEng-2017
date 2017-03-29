package HelloWorld;
import java.sql.*;

/**
 * Created by Kenneth on 3/28/17.
 */

public class DBController {

    private Connection db_connection;

    public DBController(String connection_string){
        db_connection = null;

        try {
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
        } catch(ClassNotFoundException e) {
            System.out.println("Java DB Driver not found. Add the classpath to your module.");
        }
        System.out.println("Stuff works");

        try {
            // substitute your database name for myDB
            db_connection = DriverManager.getConnection(connection_string);
        } catch (SQLException e) {
            System.out.println("Connection failed. Check output console.");
            e.printStackTrace();
            return;
        }
        System.out.println("Java DB connection established!");


    }
    //db get/set methods below
}
