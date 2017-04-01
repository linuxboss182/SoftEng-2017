package main;

import javafx.application.Application;


public class ApplicationController
{
	public static void main(String[] args) {
		DatabaseController dbc = new DatabaseController();
		dbc.initDB();
		dbc.initSchema();
		dbc.initData();
		dbc.exampleQueries();
		dbc.close();

		Application.launch(userpanel.Window.class, args);

	}
}

