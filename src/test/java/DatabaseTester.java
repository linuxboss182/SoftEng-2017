import entities.Directory;
import entities.Node;
import entities.Room;
import main.DatabaseController;
import main.DatabaseException;
import org.junit.Test;
import org.junit.Assert;

import static org.junit.Assert.assertEquals;

/**
 * Created by Walt on 4/4/2017.
 * Testing for the database
 * Honestly I kinda hoped you would've guessed that from the filename
 */
public class DatabaseTester
{
	@Test
	public void tester(){
		Room roomA = new Room(1, 1, "A", "A", "A");
		Room roomB = new Room(2, 2, "B", "B", "B");
		Room roomC = new Room(3, 3, "C", "C", "C");

		Node nodeA = new Node(1,2);
		Node nodeB = new Node(2,3);

		DatabaseController controller = new DatabaseController();
		Directory oldDirectory = new Directory();

		oldDirectory.addRoom(roomA);
		oldDirectory.addRoom(roomB);
		oldDirectory.addRoom(roomC);

		oldDirectory.addNode(nodeA);
		oldDirectory.addNode(nodeB);

		try{
			controller.init();
		}
		catch (main.DatabaseException e){
			System.out.println("Database did not init");
			assertEquals(true,false);
		}

		try {
			controller.destructiveSaveDirectory(oldDirectory);
		} catch (DatabaseException e) {
			System.out.println(e.getMessage());
			assertEquals(true,false);
		}

		Directory newDirectory = controller.getDirectory();

		assertEquals(oldDirectory, newDirectory);
	}
}
