package entities;

//TODO: Improve documentation

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * A class to represent someone in the hospital's staff
 */
public class Professional
		implements Comparable<Professional>
{
	/* Attributes */
	private String givenName;
	private String surname;
	private String title;
	private Set<Room> locations;


	/* Constructors */

	/** @deprecated Access and modify professionals through the database instead */
	@Deprecated
	Professional(String givenName, String surname,
	                    String title, Collection<Room> locations) {
		// for anyone who didn't know:
		// Room... locations allows infinite parameters of type Room
		// and it condenses them into type Room[]
		this.givenName = givenName;
		this.surname = surname;
		this.title = title;
		this.locations = new HashSet<>(locations);
	}

	Professional(String givenName, String surname, String title) {
		// for anyone who didn't know:
		// Room... locations allows infinite parameters of type Room
		// and it condenses them into type Room[]
		this.givenName = givenName;
		this.surname = surname;
		this.title = title;
		this.locations = new HashSet<>();
	}

	/* Interface methods */

	/**
	 * Compare this and another professional
	 *
	 * This allows Professionals to be sorted.
	 *
	 * Comparison order is surname, then given name, then title, then identity.
	 *
	 * @return -1, 0, or 1 if this professional is less than, equal to, or greater than the other
	 */
	@Override
	public int compareTo(Professional other) {
		int compareSurname = this.surname.compareTo(other.surname);
		if (compareSurname != 0) return compareSurname;

		int compareGivenName = this.givenName.compareTo(other.givenName);
		if (compareGivenName != 0) return compareGivenName;

		int compareTitle = this.title.compareTo(other.title);
		if (compareTitle != 0) return compareTitle;

		return (this == other) ? 0 : 1; // handle identity or SortedSet won't take people with the same name
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

	void addLocation(Room room){
		this.locations.add(room);
	}

	public String toString(Professional p) {
		return this.getSurname() + ", " + this.getGivenName();
	}

	void removeLocation(Room r) {
		this.locations.remove(r);
	}

	@Override
	public String toString() {
		return this.givenName;
	}

}
