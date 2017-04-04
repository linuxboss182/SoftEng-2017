package main;

import javafx.application.Application;


public class ApplicationController
{

	public static DatabaseController dbc;
	public static void main(String[] args) {
		dbc = new DatabaseController();
		dbc.initDB();
		dbc.initSchema();

		Application.launch(userpanel.Window.class, args);

		dbc.close();
	}
}

