package entities;

//TODO: Improve documentation

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * A class to represent someone in the hospital's staff
 */
public class Professional
{
	/* Attributes */
	private String givenName;
	private String surname;
	private String title;
	private Set<Room> locations;


	/* Constructors */

	public Professional(String givenName, String surname,
	                    String title, Collection<Room> locations) {
		// for anyone who didn't know:
		// Room... locations allows infinite parameters of type Room
		// and it condenses them into type Room[]
		this.givenName = givenName;
		this.surname = surname;
		this.title = title;
		this.locations = new HashSet<>(locations);
	}

	public Professional(String givenName, String surname, String title) {
		// for anyone who didn't know:
		// Room... locations allows infinite parameters of type Room
		// and it condenses them into type Room[]
		this.givenName = givenName;
		this.surname = surname;
		this.title = title;
		this.locations = new HashSet<>();
	}

	public Professional() {
		this("Enter given name", "Enter surname", "Enter title");
	}


	/* Getters and Setters */

	public String getGivenName() {
		return this.givenName;
	}

	public String getSurname() {
		return this.surname;
	}

	public String getTitle() {
		return this.title;
	}

	public Set<Room> getLocations() {
		return new HashSet<>(this.locations);
	}

	public void addLocation(Room room){
		this.locations.add(room);
	}

	public String toString() {
		return this.getSurname() + ", " + this.getGivenName();
	}

	public void removeLocation(Room r) {
		this.locations.remove(r);
	}
}
