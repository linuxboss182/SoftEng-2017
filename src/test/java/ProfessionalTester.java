import entities.Directory;
import entities.Professional;
import entities.Room;
import org.junit.Test;
import org.junit.Assert;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Michael on 4/4/2017.
 * Test the Professional Entity Class
 */
public class ProfessionalTester
{
	@Test
	public void addLocationTester() {
		Directory dir = new Directory();
		Room r = dir.addNewRoom("Room 1", "", "the first room");
		Room r1 = dir.addNewRoom("Room 2", "", "the second room");

		Professional doctor = dir.addNewProfessional("FirstName", "LastName", "Dr.");
		dir.addRoomToProfessional(r, doctor);
		dir.addRoomToProfessional(r1, doctor);
		Set<Room> expectDocLoc = new HashSet<>();
		expectDocLoc.add(r);
		expectDocLoc.add(r1);
		Assert.assertEquals(expectDocLoc, doctor.getLocations());
	}
}
