package main;

import javafx.application.Application;


public class ApplicationController
{

	public static DatabaseController dbc;

	public static void main(String[] args) {
		ApplicationController.dbc = new DatabaseController();

		try {
			ApplicationController.dbc.init();
		} catch (DatabaseException e) {
			System.out.println("ERROR IN DATABASE INITIALIZATION:\n" + e.getMessage());
			return;
		}

		Application.launch(userpanel.Window.class, args);

		ApplicationController.dbc.close();
	}
}

