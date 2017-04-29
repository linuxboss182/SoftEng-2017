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

	public void setUsername(String username) {
		this.username = username;
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
