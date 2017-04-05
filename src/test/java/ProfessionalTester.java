import entities.Directory;
import entities.Professional;
import entities.Room;
import org.junit.Test;
import org.junit.Assert;

import java.util.HashSet;

/**
 * Created by Michael on 4/4/2017.
 * Test the Professional Entity Class
 */
public class ProfessionalTester
{
	@Test
	public void addLocationTester() {
		Room n = new Room(20, 30); //Create a new node
		Room n1 = new Room(10, 15); //Create a new node
		HashSet<Room> DocLoc = new HashSet<>();
		DocLoc.add(n);

		Professional Doctor = new Professional("FirstName", "LastName",
				"Dr.", DocLoc); //Create a Professional
		Doctor.addLocation(n1);
		HashSet<Room> ExpectDocLoc = new HashSet<>();
		ExpectDocLoc.add(n);
		ExpectDocLoc.add(n1);
		Assert.assertEquals(ExpectDocLoc, Doctor.getLocations());
	}
}
