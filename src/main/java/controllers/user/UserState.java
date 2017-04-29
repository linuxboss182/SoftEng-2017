package controllers.user;

import entities.Node;
import entities.Room;
import javafx.scene.Parent;


public class UserState
{
	private Parent root;
	private boolean loggedIn;

	private Room startRoom;
	private Room endRoom;

	public UserState(Parent root) {
		this.root = root;
		this.loggedIn = loggedIn;
		this.startRoom = startRoom;
		this.endRoom = endRoom;
	}

	public Parent getRoot() {
		return this.root;
	}

}
