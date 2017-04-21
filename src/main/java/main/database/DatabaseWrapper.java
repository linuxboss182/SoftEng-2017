package main.database;

import entities.Directory;

// TODO: Make DBWrapper a singleton

/**
 * Class for interacting with the database
 *
 * Currently, this class simply wraps the other database classes
 */
public class DatabaseWrapper
{
	private static DatabaseConnector DBConn;
	private static DatabaseLoader DBLoader;

	/**
	 * Initialize the database classes to allow other methods to be called
	 *
	 * @throws DatabaseException If something goes wrong when setting up the connection.
	 */
	public static void init() throws DatabaseException {
		DBConn = new DatabaseConnector();
		DBLoader = new DatabaseLoader(DBConn);
	}

	/**
	 * Close the connection to the database
	 *
	 * @return Whether the operation was successful
	 */
	public static boolean close() {
		return DBConn.close();
	}

	/**
	 * Save the contents of the given directory as the database
	 */
	public static void saveDirectory(Directory directory) {
		DatabaseLoader DBL = new DatabaseLoader(DBConn);
		try {
			DBL.destructiveSaveDirectory(directory);
		} catch (DatabaseException e) {
			System.err.println("\n\nDATABASE DAMAGED\n\n");
			e.printStackTrace();
			System.err.println("\n\nDATABASE DAMAGED\n\n");
		}
	}

	/**
	 * Create and popualte a directory from the database
	 */
	public static Directory getDirectory() {
		return DBLoader.getDirectory();
	}
}
