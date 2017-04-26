package controllers.shared;

import entities.Directory;
import main.ApplicationController;
import java.util.HashMap;

public class LoginHandler
{
	Directory directory = ApplicationController.getDirectory();

	public LoginHandler(){}

	public void addAccount(String username, String password, Boolean isAdmin){
		HashMap<String, String> adminLogins = directory.getAdminUsers();
		HashMap <String, String> professionalLogins = directory.getProfessionalUsers();

		String uppercasedUsername = username.toUpperCase();
		if (isAdmin && !adminLogins.containsKey(uppercasedUsername) && !professionalLogins.containsKey(uppercasedUsername)){
			directory.addUser(uppercasedUsername, password, "admin");
		}
		else if (!isAdmin && !adminLogins.containsKey(uppercasedUsername) && !professionalLogins.containsKey(uppercasedUsername)) {
			directory.addUser(uppercasedUsername, password, "professional");
		}
	}

	public void removeAccount(String username){
		HashMap<String, String> adminLogins = directory.getAdminUsers();
		HashMap <String, String> professionalLogins = directory.getProfessionalUsers();

		String uppercasedUsername = username.toUpperCase();
		if (adminLogins.containsKey(uppercasedUsername)){
			adminLogins.remove(uppercasedUsername);
		}
		else if (professionalLogins.containsKey(uppercasedUsername)) {
			professionalLogins.remove(uppercasedUsername);
		}
	}

	public void changePassword(String username, String oldPassword, String newPassword){
		HashMap<String, String> adminLogins = directory.getAdminUsers();
		HashMap <String, String> professionalLogins = directory.getProfessionalUsers();

		String uppercasedUsername = username.toUpperCase();
		if(adminLogins.containsKey(uppercasedUsername) && Boolean.TRUE.equals(adminLogins.get(uppercasedUsername).equals(oldPassword))){
			adminLogins.put(uppercasedUsername, newPassword);
		}
		else if (professionalLogins.containsKey(uppercasedUsername) && Boolean.TRUE.equals(professionalLogins.get(uppercasedUsername).equals(oldPassword))){
			professionalLogins.put(uppercasedUsername, newPassword);
		}
		else{
			//throw an error or something idk
		}
	}

	public byte checkLogin(String username, String password){
		HashMap<String, String> adminLogins = directory.getAdminUsers();
		HashMap <String, String> professionalLogins = directory.getProfessionalUsers();

		String uppercasedUsername = username.toUpperCase();
		System.out.println(adminLogins.keySet());
		System.out.println(adminLogins.values());
		System.out.println(adminLogins.get(uppercasedUsername));
		System.out.println(adminLogins.isEmpty());
		System.out.println(adminLogins.containsKey("admin"));

		if(adminLogins.containsKey(uppercasedUsername) && Boolean.TRUE.equals(adminLogins.get(uppercasedUsername).equals(password))){
			System.out.println("ret 2");
			return 2;
		}
		else if (professionalLogins.containsKey(uppercasedUsername) && Boolean.TRUE.equals(professionalLogins.get(uppercasedUsername).equals(password))){
			System.out.println("ret 1");

			return 1;
		}
		else{
			System.out.println("ret 0");

			return 0;
		}
	}

	private enum LoginStatus {
		ADMIN, USER, FAILURE;
	}
}
