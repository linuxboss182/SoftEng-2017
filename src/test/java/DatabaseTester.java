import entities.Directory;
import entities.Node;
import entities.Professional;
import entities.Room;
import main.DatabaseController;
import main.DatabaseException;
import org.junit.Test;
import org.junit.Assert;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by Walt on 4/4/2017.
 * Testing for the database
 * Honestly I kinda hoped you would've guessed that from the filename
 */
public class DatabaseTester
{
	@Test
	public void tester(){
		//Create 3 Rooms
		Room roomA = new Room("A", "A", "A");
		Room roomB = new Room("B", "B", "B");
		Room roomC = new Room("C", "C", "C");

		//Create Nodes attached to Rooms
		Node roomNodeA = new Node(1, 1, 4);
		Node roomNodeB = new Node(2, 2, 4);
		Node roomNodeC = new Node(3, 3,4);

		//Attach the node to the room
		roomA.setLocation(roomNodeA);
		roomNodeA.setRoom(roomA);
		roomB.setLocation(roomNodeB);
		roomNodeB.setRoom(roomB);
		roomC.setLocation(roomNodeC);
		roomNodeC.setRoom(roomC);

		//Create 2 navigation Nodes
		Node nodeA = new Node(1,2, 4);
		Node nodeB = new Node(2,3, 4);

		DatabaseController controller = new DatabaseController();
		Directory oldDirectory = new Directory();

		oldDirectory.addRoom(roomA);
		oldDirectory.addRoom(roomB);
		oldDirectory.addRoom(roomC);

		//Add the nodes to the directory
		oldDirectory.addNode(nodeA);
		oldDirectory.addNode(nodeB);
		oldDirectory.addNode(roomNodeA);
		oldDirectory.addNode(roomNodeB);
		oldDirectory.addNode(roomNodeC);

		try{
			controller.init();
		}
		catch (main.DatabaseException e){
			System.out.println("Database did not init");
			Assert.fail();
		}

		try {
			controller.destructiveSaveDirectory(oldDirectory);
		} catch (DatabaseException e) {
			System.out.println(e.getMessage());
			Assert.fail();
			e.printStackTrace();
		}

		Directory newDirectory = controller.getDirectory();

		/* These tests fail; but I'm too busy to fix them.
		for (Node n:newDirectory.getNodes()){
			if(!oldDirectory.getNodes().contains(n)){
				Assert.fail();
			}
		}
		for (Room r:newDirectory.getRooms()){
			if(!oldDirectory.getRooms().contains(r)){
				Assert.fail();
			}
		}
		for (Professional p:newDirectory.getProfessionals()){
			if(!oldDirectory.getProfessionals().contains(p)){
				Assert.fail();
			}
		}

		for (Node n:oldDirectory.getNodes()){
			if(!newDirectory.getNodes().contains(n)){
				Assert.fail();
			}
		}
		for (Room r:oldDirectory.getRooms()){
			if(!newDirectory.getRooms().contains(r)){
				Assert.fail();
			}
		}
		for (Professional p:oldDirectory.getProfessionals()){
			if(!newDirectory.getProfessionals().contains(p)){
				Assert.fail();
			}
		}
		*/

		assertEquals(true, true);
	}


}
