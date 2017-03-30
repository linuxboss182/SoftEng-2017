///**
// * Created by WilsonWong on 3/19/2017.
// */

//package main;
//import java.sql.*;

//public class DatabaseController
//{
//	public static void main(String[] args) {
//		System.out.println("-------Embedded Java DB Connection Testing --------");
//		try {
//			Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
//		} catch (ClassNotFoundException e) {
//			System.out.println("Java DB Driver not found. Add the classpath to your module.");
//			System.out.println("For IntelliJ do the following:");
//			System.out.println("File | Project Structure, Modules, Dependency tab");
//			System.out.println("Add by clicking on the green plus icon on the right of the window");
//			System.out.println("Select JARs or directories. Go to the folder where the Java JDK is installed");
//			System.out.println("Select the folder java/jdk1.8.xxx/db/lib where xxx is the version.");
//			System.out.println("Click OK, compile the code and run it.");
//			e.printStackTrace();
//			return;
//		}

//		System.out.println("Java DB driver registered!");
//		Connection connection = null;
//		/* TODO: Don't use null as a value.
//		   If this is only for the sake of having a default, don't declare.
//		   If you ever plan to check if the variable has been set, use Optional. (See the style guide.)
//		*/

//		try {
//			connection = DriverManager.getConnection("jdbc:derby:DB;create=true");
//		} catch (SQLException e) {
//			System.out.println("Connection failed. Check output console.");
//			e.printStackTrace();
//			return;
//		}
//		System.out.println("Java DB connection established!");
//	}
//}
