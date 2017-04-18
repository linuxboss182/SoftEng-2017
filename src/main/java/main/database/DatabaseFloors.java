package main.database;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.*;

public class DatabaseFloors
{
	/** class for returning two images */
	// I know this isn't the best, but it works, and it means Floor can stay package-private
	public class MapThumbPair
	{
		public Image map;
		public Image thumb;
		public MapThumbPair(Image map, Image thumb) {
			this.map = map;
			this.thumb = thumb;
		}
	}

	private Connection conn;
	private static final String SCHEMA = "CREATE TABLE Floors ("
						+" , buildingName varchar(100) "
						+" , floorNumber integer PRIMARY KEY "
						+" , map blob NOT NULL"
						+" , thumb blob"
						+" , constraint Floors_pk PRIMARY KEY (buildingName, floorNumber))"
						;

	DatabaseFloors(DatabaseConnector DBConn) {
		this.conn = DBConn.getConnection();
	}

	void drop() {
		try {
			Statement stmt = this.conn.createStatement();
			stmt.executeUpdate("DROP TABLE Floors");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/** 
	 * Save a floor with the given information to the database
	 * 
	 * @return true if successful
	 */
	public boolean saveFloor(int number, String building, Image map, Image thumb) {
		try {
			PreparedStatement prep = this.conn.prepareStatement(
					"INSERT INTO FLOORS (floorNumber, buildingName, map, thumb) values (?, ?, ?, ?)");
			prep.setInt(1, number);
			prep.setString(2, building);

			BufferedImage mapBufImg = SwingFXUtils.fromFXImage(map, null);
			ByteArrayOutputStream mapOut = new ByteArrayOutputStream();
			try {
				ImageIO.write(mapBufImg, "png", mapOut);
			} catch (IOException e) {
				throw new RuntimeException("Failed to convert map image to blob, sorry; everything may be broken now.");
			}
			ByteArrayInputStream mapIn = new ByteArrayInputStream(mapOut.toByteArray());
			prep.setBlob(3, mapIn);

			BufferedImage thumbBufImg = SwingFXUtils.fromFXImage(map, null);
			ByteArrayOutputStream thumbOut = new ByteArrayOutputStream();
			try {
				ImageIO.write(thumbBufImg, "png", thumbOut);
			} catch (IOException e) {
				throw new RuntimeException("Failed to convert thumbnail image to blob, sorry; everything may be broken now.");
			}
			ByteArrayInputStream thumbIn = new ByteArrayInputStream(thumbOut.toByteArray());
			prep.setBlob(4, thumbIn);

			prep.execute();
			prep.close();

			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			System.err.println("Failed prepare statement");
			return false;
		}
	}

	public MapThumbPair loadFloor(int number, String building) {
		try {
			PreparedStatement prep = this.conn.prepareStatement(
					"SELECT map, thumb FROM Floors WHERE floorNumber = ? AND buildingName = ?");
			prep.setInt(1, number);
			prep.setString(2, building);
			ResultSet result = prep.executeQuery();
			
			Blob mapBlob = result.getBlob("map"); // "NOT NULL"
			Blob thumbBlob = result.getBlob("thumb");
			if (result.wasNull()) {
				thumbBlob = mapBlob;
			}
			Image map = new Image(mapBlob.getBinaryStream());
			Image thumb = new Image(thumbBlob.getBinaryStream());
			return new MapThumbPair(map, thumb);
		} catch (SQLException e) {
			System.err.println("Failed to get floor from database");
			throw new RuntimeException(e);
		}
	}

	// TODO: Move this to DatabaseController, probably
	// TODO: Fix the entire DB setup
	public static void saveRoom(Connection conn, int roomID, String roomName, String roomDescription, int nodeID, Image image)
			throws SQLException {
		PreparedStatement prep = conn.prepareStatement("INSERT INTO Rooms (roomID, roomName, roomDescription, nodeID, image) values (?, ?, ?, ?, ?)");
		prep.setInt(1, 1);
		prep.setString(2, roomName);
		prep.setString(3, roomDescription);
		prep.setInt(4, nodeID);
		BufferedImage bufimg = SwingFXUtils.fromFXImage(image, null);
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		try {
			ImageIO.write(bufimg, "png", bout);
		} catch (IOException e) {
			throw new RuntimeException("Failed to convert image to blob, sorry; everything may be broken now.");
		}
		byte[] blob = bout.toByteArray();
		ByteArrayInputStream blobout = new ByteArrayInputStream(blob);
		prep.setBlob(5, blobout);
		prep.execute();
		prep.close();
	}
	/*
		Blob blob = resultRooms.getBlob("image");
					if (! resultRooms.wasNull()) {
		room.setImage(blob.getBinaryStream());
	*/
}
