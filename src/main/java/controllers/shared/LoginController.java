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

import java.util.ResourceBundle;

public class LoginController implements Initializable{

	@FXML private Label errorLbl;
	@FXML private Button cancelBtn;
	@FXML private TextField usernameField;
	@FXML private PasswordField passwordField;
	@FXML private Button loginBtn;
	@FXML private BorderPane parentBorderPane;

	Directory directory = ApplicationController.getDirectory();

	@Override
	public void initialize(URL location, ResourceBundle resources) {
//		logins = new LoginHandler();
//		logins.addAccount("admin", "password", true);

		directory.addUser("ADMIN", "password", "admin");
		directory.addUser("TEST", "password", "professional");

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
				try{
					cancelBtnClicked();
				}catch(IOException e1){

				}catch(InvocationTargetException e2){

				}
			}
		});

		this.cancelBtn.setFocusTraversable(false);
		this.loginBtn.setFocusTraversable(false);
		Platform.runLater( () -> usernameField.requestFocus());
	}


	@FXML
	public void loginBtnClicked() throws IOException, InvocationTargetException {
		byte success = LoginHandler.checkLogin(this.usernameField.getText(), this.passwordField.getText());
		if(success == 2) {
			Parent adminUI = (BorderPane) FXMLLoader.load(this.getClass().getResource("/AdminUI.fxml"));
			errorLbl.getScene().setRoot(adminUI);
		}
		else if (success == 1){
			Parent destUI = (BorderPane) FXMLLoader.load(this.getClass().getResource("/UserDestination.fxml"));
			errorLbl.getScene().setRoot(destUI);
		}
		else {
			this.errorLbl.setText("Incorrect Username or Password");
			this.usernameField.requestFocus();
		}
	}

	@FXML
	public void enterPressed(KeyEvent e){
		if ( e.getCode() == KeyCode.ENTER){
			try {
				this.loginBtnClicked();
			}catch(IOException e1){

			}catch(InvocationTargetException e2){

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
	public void cancelBtnClicked() throws IOException, InvocationTargetException {
		Parent destUI = (BorderPane) FXMLLoader.load(this.getClass().getResource("/UserDestination.fxml"));
		errorLbl.getScene().setRoot(destUI);
	}



}
