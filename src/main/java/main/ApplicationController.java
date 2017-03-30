package main;
import javafx.application.Application;


public class ApplicationController {

	public static void main(String[] args) {
		// Application.launch(userpanel.Window.class, args);
		DatabaseController dbc = new DatabaseController();
		dbc.initConnection();
		dbc.initDB();
		dbc.insertSampleData();
		dbc.close();
	}


}

