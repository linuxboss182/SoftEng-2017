package controllers.user;

import entities.Node;
import entities.Room;
import javafx.scene.Parent;

/**
 * Created by tbasl_000 on 4/27/2017.
 */
public class UserState
{
	private Parent root;
	private boolean loggedIn;

	private Room startRoom;
	private Room endRoom;
	private String textFieldContents;

	public UserState(Parent root, boolean loggedIn, Room startRoom, Room endRoom, String textFieldContents) {
		this.root = root;
		this.loggedIn = loggedIn;
		this.startRoom = startRoom;
		this.endRoom = endRoom;
		this.textFieldContents = textFieldContents;
	}

	public Parent getRoot() {
		return this.root;
	}

	public boolean getLoggedIn() {
		return this.loggedIn;
	}

	public Room getStartRoom() {
		return this.startRoom;
	}

	public Room getEndRoom() {
		return this.endRoom;
	}

	public String getTextFieldContents() {
		return this.textFieldContents;
	}

}
