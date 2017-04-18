import entities.Node;
import entities.Directory;
import entities.Professional;
import entities.Room;
import org.junit.Test;
import org.junit.Assert;

import java.util.HashSet;

/**
 * Created by Michael on 4/1/2017.
 *
 *
 */
public class DirectoryTester
{
	@Test
	public void testAddNode() {
		Directory d = new Directory(); //Create a new directory
		Node n = new Node(20, 30, 4); //Create a new node
		d.addNode(n); //Add the node to the list
		Assert.assertEquals(1, d.getNodes().size()); //Assert Size Equal
		Assert.assertEquals(true, d.getNodes().contains(n));
	}

	@Test
	public void testAddProfessional() {
		Node n = new Node(20, 30, 4);
		Room r = new Room("aRoom", "desc"); //Create a new node
		r.setLocation(n);
		n.setRoom(r);
		Directory d = new Directory();
		d.addRoom(r);
		Professional Doctor = new Professional("FirstName", "LastName",
				"Dr.", d.getRooms()); //Create a Professional
		d.addProfessional(Doctor); //Add the Professional to the list
		Assert.assertEquals(1, d.getProfessionals().size()); //Assert Size Equal
		Assert.assertEquals(true, d.getProfessionals().contains(Doctor));
	}

	@Test
	public void testAddRoom() {
		Directory d = new Directory(); //Create a new directory
		Node n = new Node(20, 30, 4);
		Room r = new Room("aRoom", "desc"); //Create a new node
		r.setLocation(n);
		n.setRoom(r);
		d.addRoom(r); //Add the node to the list
		Assert.assertEquals(1, d.getRooms().size());
		Assert.assertEquals(true, d.getRooms().contains(r));
	}

	// Removed because Directory::removeNode is private
//	@Test
//	public void testRemoveNode() {
//		Directory d = new Directory(); //Create a new directory
//		Node n = new Node(20, 30, 0); //Create a new node
//		d.addNode(n); //Add the node to the list
//		Assert.assertEquals(1, d.getNodes().size()); //Make sure node is added
//		d.removeNode(n); //Remove the node from the list
//		Assert.assertEquals(false, d.getNodes().contains(n));
//	}

	// Removed because Directory::removeRoom is private
//	@Test
//	public void testRemoveRoom() {
//		Directory d = new Directory(); //Create a new directory
//		Node n = new Node(20, 30, 4);
//		Room r = new Room("aRoom", "desc"); //Create a new node
//		r.setLocation(n);
//		n.setRoom(r);
//		d.addRoom(r); //Add the room to the list
//		Assert.assertEquals(1, d.getRooms().size()); //Make sure room is added
//		d.removeRoom(r); //Remove the room from the list
//		Assert.assertEquals(false, d.getRooms().contains(r));
//	}

	@Test
	public void testRemoveProfessional() {
		Node n = new Node(20, 30, 4);
		Room r = new Room("aRoom", "desc"); //Create a new node
		r.setLocation(n);
		n.setRoom(r);
		Directory d = new Directory();
		d.addRoom(r);
		Professional Doctor = new Professional("FirstName", "LastName",
				"Dr.", d.getRooms()); //Create a Professional
		d.addProfessional(Doctor); //Add the Professional to the list
		Assert.assertEquals(true, d.getProfessionals().contains(Doctor));
		d.removeProfessional(Doctor);
		Assert.assertEquals(false, d.getProfessionals().contains(Doctor));
	}
}
