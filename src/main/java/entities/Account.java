package entities;

public class Account
{
	private String username;
	private String password;
	private String permission;

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public String getPermissions() {
		return permission;
	}

	Account(String username, String password, String permission){
		this.username = username;
		this.password = password;
		this.permission = permission;
	}

}
