package entities;

//TODO: Improve documentation

/**
 * A class to represent someone in the hospital's staff
 */
public class Professional
{
	/* Attributes */
	private String name;
	private String title;
	private Room[] locations;


	/* Constructors */

	public Professional(String name, String title, Room... locations) {
		// for anyone who didn't know:
		// Room... locations allows infinite parameters of type Room
		// and it condenses them into type Room[]
		this.name = name;
		this.title = title;
		this.locations = locations;
	}

	public Professional() {
		this("John Doe", "Anonymous");
	}


	/* Methods */

}
