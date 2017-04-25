package controllers.shared;

import main.database.DatabaseWrapper;

import java.util.HashMap;

/**
 * Created by Walt on 4/21/2017.
 */
public class LoginHandler
{
	private HashMap<String, String> adminLogins;
	private HashMap <String, String> professionalLogins;

	public LoginHandler(HashMap<String, String> adminMap){
		this.adminLogins = adminMap;
		this.professionalLogins = new HashMap();
	}

	public void addAccount(String username, String password, Boolean isAdmin){
		String uppercasedUsername = username.toUpperCase();
		if (isAdmin && !adminLogins.containsKey(uppercasedUsername) && !professionalLogins.containsKey(uppercasedUsername)){
			this.adminLogins.put(uppercasedUsername, password);
			DatabaseWrapper.getInstance().getDirectory().addUser(uppercasedUsername, password, "admin");
			System.out.println("I... did it?");
		}
		else if (!isAdmin && !adminLogins.containsKey(uppercasedUsername) && !professionalLogins.containsKey(uppercasedUsername)) {
			this.professionalLogins.put(uppercasedUsername, password);
			DatabaseWrapper.getInstance().getDirectory().addUser(uppercasedUsername, password, "professional");
		}
	}

	public void removeAccount(String username){
		String uppercasedUsername = username.toUpperCase();
		if (adminLogins.containsKey(uppercasedUsername)){
			adminLogins.remove(uppercasedUsername);
		}
		else if (professionalLogins.containsKey(uppercasedUsername)) {
			professionalLogins.remove(uppercasedUsername);
		}
	}

	public void changePassword(String username, String oldPassword, String newPassword){
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
		String uppercasedUsername = username.toUpperCase();
		System.out.println(adminLogins.keySet());
		System.out.println(adminLogins.values());
		System.out.println(adminLogins.get(uppercasedUsername));
		System.out.println(adminLogins.isEmpty());
		System.out.println(adminLogins.containsKey("admin"));
		if(adminLogins.containsKey(uppercasedUsername) && Boolean.TRUE.equals(adminLogins.get(uppercasedUsername).equals(password))){
			return 2;
		}
		else if (professionalLogins.containsKey(uppercasedUsername) && Boolean.TRUE.equals(professionalLogins.get(uppercasedUsername).equals(password))){
			return 1;
		}
		else{
			return 0;
		}
	}

	private enum LoginStatus {
		ADMIN, USER, FAILURE;
	}
}
