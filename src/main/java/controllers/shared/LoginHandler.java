package controllers.shared;

import entities.Directory;
import main.ApplicationController;
import java.util.HashMap;

public class LoginHandler
{
	private Directory directory = ApplicationController.getDirectory();

	public LoginHandler(){}

	public void addAccount(String username, String password, Boolean isAdmin){
		HashMap<String, String> logins = directory.getUsers();

		String uppercasedUsername = username.toUpperCase();
		if (isAdmin && !logins.containsKey(uppercasedUsername) && !logins.containsKey(uppercasedUsername)){
			directory.addUser(uppercasedUsername, password, "admin");
		}
		else if (!isAdmin && !logins.containsKey(uppercasedUsername) && !logins.containsKey(uppercasedUsername)) {
			directory.addUser(uppercasedUsername, password, "professional");
		}
	}

	public void removeAccount(String username){
		HashMap<String, String> logins = directory.getUsers();

		String uppercasedUsername = username.toUpperCase();
		if (logins.containsKey(uppercasedUsername)){
			logins.remove(uppercasedUsername);
		}
	}

	public void changePassword(String username, String oldPassword, String newPassword){
		HashMap<String, String> logins = directory.getUsers();

		String uppercasedUsername = username.toUpperCase();
		if(logins.containsKey(uppercasedUsername) && Boolean.TRUE.equals(logins.get(uppercasedUsername).equals(oldPassword))){
			logins.put(uppercasedUsername, newPassword);
		}
	}

	public static byte checkLogin(String username, String password){
		HashMap<String, String> logins = ApplicationController.getDirectory().getUsers();

		String uppercasedUsername = username.toUpperCase();

		if(logins.containsKey(uppercasedUsername) && Boolean.TRUE.equals(logins.get(uppercasedUsername).equals(password))){
			if(ApplicationController.getDirectory().getPermissions(uppercasedUsername).equals("admin")) {
				return 2;
			}else{
				ApplicationController.getDirectory().professionalLogin();
				return 1;
			}
		}
		else{
			return 0;
		}
	}

	private enum LoginStatus {
		ADMIN, USER, FAILURE;
	}
}
