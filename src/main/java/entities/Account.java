package entities;

import java.util.HashMap;

public class Account
{
	private String name;
	private String password;
	private String permission;

	public String getName() {
		return name;
	}

	public String getPassword() {
		return password;
	}

	public String getPermissions() {
		return permission;
	}

	Account(String name, String password, String permission){
		this.name = name;
		this.password = password;
		this.permission = permission;
	}

}
