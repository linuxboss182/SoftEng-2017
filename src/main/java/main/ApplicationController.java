package main;

import javafx.application.Application;
import entities.Directory;

public class ApplicationController
{

	public static DatabaseController dbc;
	public static Directory directory;

	public static Directory getDirectory() {
		return ApplicationController.directory; // returns the single copy
	}

	public static void main(String[] args) {
		ApplicationController.dbc = new DatabaseController();

		try {
			ApplicationController.dbc.init();
		} catch (DatabaseException e) {
			System.out.println("ERROR IN DATABASE INITIALIZATION:\n" + e.getMessage());
			return;
		}

		ApplicationController.directory = ApplicationController.dbc.getDirectory();

		Application.launch(userpanel.Window.class, args);

		ApplicationController.dbc.close();
	}
}

