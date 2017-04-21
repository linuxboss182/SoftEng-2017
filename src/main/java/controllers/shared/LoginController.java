package controllers.shared;


import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

public class LoginController {

	private LoginHandler logins;

	@FXML
	private Label errorLbl;
	@FXML
	private Button cancelBtn;
	@FXML
	private TextField usernameField;
	@FXML
	private PasswordField passwordField;
	@FXML
	private Button loginBtn;


	public void attachHandler(LoginHandler logins){
		this.logins = logins;
	}

	@FXML
	public void loginBtnClicked() throws IOException, InvocationTargetException {
		boolean success = logins.checkLogin(this.usernameField.getText(), this.passwordField.getText());
//		boolean success = adminLogins.get(this.passwordField.getText()).equals(this.usernameField.getText());
		if(success) {
			Parent adminUI = (BorderPane) FXMLLoader.load(this.getClass().getResource("/AdminUI.fxml"));
			errorLbl.getScene().setRoot(adminUI);
		} else {
			this.errorLbl.setText("Incorrect Username or Password");
			// They didn't login successfully so they should probably be punished in some way
		}
	}

	@FXML
	public void cancelBtnClicked() throws IOException, InvocationTargetException {
		Parent destUI = (BorderPane) FXMLLoader.load(this.getClass().getResource("/UserDestination.fxml"));
		errorLbl.getScene().setRoot(destUI);
	}



}
