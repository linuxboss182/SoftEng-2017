package entities;

import main.ApplicationController;

public class Account
{
	public String username;
	public String password;
	public String permission;

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public String getPermissions() {
		return permission;
	}

	public void setUsername(String newName) {
		ApplicationController.getDirectory().updateKey(newName, username);
		this.username = newName;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setPermission(String permission) {
		this.permission = permission;
	}

	Account(String username, String password, String permission){
		this.username = username;
		this.password = password;
		this.permission = permission;
	}

}
