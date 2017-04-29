package entities;

public class Account
{
	private String username;
	private String password;
	private String permission;

	public String getName() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public String getPermissions() {
		return permission;
	}

	Account(String name, String password, String permission){
		this.username = name;
		this.password = password;
		this.permission = permission;
	}

}
