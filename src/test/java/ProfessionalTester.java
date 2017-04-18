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
		Room n = new Room("Room 1", "the first room");
		Room n1 = new Room("Room 2", "the second room");
		HashSet<Room> docLoc = new HashSet<>();
		docLoc.add(n);

		Professional Doctor = new Professional("FirstName", "LastName",
				"Dr.", docLoc); //Create a Professional
		Doctor.addLocation(n1);
		Set<Room> expectDocLoc = new HashSet<>();
		expectDocLoc.add(n);
		expectDocLoc.add(n1);
		Assert.assertEquals(expectDocLoc, Doctor.getLocations());
	}
}
