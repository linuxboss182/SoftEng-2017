package controllers.shared;

import java.util.HashMap;

/**
 * Created by Walt on 4/21/2017.
 */
public class LoginHandler
{
	private HashMap<String, String> adminLogins;

	public LoginHandler(){
		this.adminLogins = new HashMap<>();
	}

	public void addAdmin(String username, String password){
		String uppercasedUsername = username.toUpperCase();
		this.adminLogins.put(password, uppercasedUsername);
	}

	public boolean checkLogin(String username, String password){
		String uppercasedUsername = username.toUpperCase();
		return (Boolean.TRUE.equals(adminLogins.get(password).equals(uppercasedUsername)));
	}
}
