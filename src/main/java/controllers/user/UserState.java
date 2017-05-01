package controllers.user;

import entities.Node;
import entities.Room;
import javafx.scene.Parent;
import javafx.scene.Scene;


public class UserState
{
	private Scene scene;
	private Parent root;

	public UserState(Parent root, Scene scene) {
		this.root = root;
		this.scene = scene;
	}

	public Parent getRoot() {
		return this.root;
	}

}
