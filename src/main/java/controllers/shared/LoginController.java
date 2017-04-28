package controllers.shared;


import entities.Directory;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;

import javafx.scene.input.KeyEvent;
import main.ApplicationController;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;

import java.util.HashMap;
import java.util.ResourceBundle;

public class LoginController implements Initializable{

	@FXML private Label errorLbl;
	@FXML private Button cancelBtn;
	@FXML private TextField usernameField;
	@FXML private PasswordField passwordField;
	@FXML private Button loginBtn;
	@FXML private BorderPane parentBorderPane;

	private Directory directory = ApplicationController.getDirectory();

	@Override
	public void initialize(URL location, ResourceBundle resources) {
//		logins = new LoginHandler();
//		logins.addAccount("admin", "password", true);

		directory.addUser("admin", "password", "admin");
		directory.addUser("test", "password", "professional");

//		Set<String> passwordSet = directory.getPassHashes();
//		Set<String> permissionSet = directory.getPermissions();

//		String[] users = userSet.toArray(new String[userSet.size()]);
//		String[] passwords = userSet.toArray(new String[passwordSet.size()]);
//		String[] permissions = userSet.toArray(new String[permissionSet.size()]);

//		for (int n = 0; n < users.length; n++){
//			System.out.println("YOOOOOO");
//			logins.addAccount(users[n], passwords[n], permissions[n].equals("admin"));
//		}



//		logins.addAccount("admin", "password", true);
//		logins.addAccount("admin2", "admin, too?", true);


		parentBorderPane.setOnKeyPressed(e -> {
			if(e.getCode() == KeyCode.ESCAPE){
				try {
					cancelBtnClicked();
				} catch (IOException ex) {
					System.err.println("IOException while cancelling login attempt");
					throw new RuntimeException(ex);
				}
			}
		});

		this.cancelBtn.setFocusTraversable(false);
		this.loginBtn.setFocusTraversable(false);
		Platform.runLater( () -> usernameField.requestFocus());
	}


	@FXML
	public void loginBtnClicked() throws IOException {
		LoginStatus status = checkLogin(this.usernameField.getText(), this.passwordField.getText());
		switch (status) {
			case ADMIN:
				// directory.logIn(); // Admins start viewing the user screen
				Parent adminUI = FXMLLoader.load(this.getClass().getResource("/AdminUI.fxml"));
				errorLbl.getScene().setRoot(adminUI);
				break;
			case PROFESSIONAL:
				directory.logIn();
				Parent destUI = FXMLLoader.load(this.getClass().getResource("/UserDestination.fxml"));
				errorLbl.getScene().setRoot(destUI);
				break;
			default:
				this.errorLbl.setText("Incorrect Username or Password");
				this.usernameField.requestFocus();
		}
	}

	@FXML
	public void enterPressed(KeyEvent e){
		if ( e.getCode() == KeyCode.ENTER){
			try {
				this.loginBtnClicked();
			} catch (IOException ex) {
				System.err.println("IOException during login attempt");
				throw new RuntimeException(ex);
			}
		}
	}

	@FXML
	public void enterPressed1(KeyEvent e){
		if ( e.getCode() == KeyCode.ENTER){
			this.passwordField.requestFocus();
		}
	}

	@FXML
	public void cancelBtnClicked() throws IOException {
		Parent destUI = (BorderPane) FXMLLoader.load(this.getClass().getResource("/UserDestination.fxml"));
		errorLbl.getScene().setRoot(destUI);
	}



	/**
	 * Check if a given username and password form a valid log-in ID
	 *
	 * @param username The username to test
	 * @param password The password to test
	 * @return 2 for admins, 1 for professionals, or 0 for failed logins
	 */
	public LoginStatus checkLogin(String username, String password) {
		HashMap<String, String> logins = directory.getUsers();


		// Branches:
		// contains key and password is value and is admin
		// contains key and password is value and not admin
		// does not contain key or does not contain value


		;

		// Safe because the empty string is not a valid password
		if (logins.getOrDefault(username, "").equals(password)) {
			switch (directory.getPermissions(username).toUpperCase()) {
				case "ADMIN":
					return LoginStatus.ADMIN;
				case "PROFESSIONAL":
					return LoginStatus.PROFESSIONAL;
				default:
					return LoginStatus.FAILURE;
			}
		} else {
			return LoginStatus.FAILURE;
		}
	}

	public enum LoginStatus {
		ADMIN, PROFESSIONAL, FAILURE;
	}
}
