package controllers.shared;


import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import javafx.scene.input.KeyEvent;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;

import java.util.ResourceBundle;

public class LoginController implements Initializable{

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
	@FXML
	private BorderPane parentBorderPane;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
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



	@Override
	public void initialize(URL location, ResourceBundle resources) {
		logins.addAccount("Frank", "12345", true);
	}


	@FXML
	public void loginBtnClicked() throws IOException, InvocationTargetException {
		byte success = logins.checkLogin(this.usernameField.getText(), this.passwordField.getText());
//		boolean success = adminLogins.get(this.passwordField.getText()).equals(this.usernameField.getText());
		if(success == 2) {
			Parent adminUI = (BorderPane) FXMLLoader.load(this.getClass().getResource("/AdminUI.fxml"));
			errorLbl.getScene().setRoot(adminUI);
		}
		else if (success == 1){
			//TODO: Figure out what to do with logged-in professionals
		}
		else {

			this.errorLbl.setText("Incorrect Username or Password");
			// They didn't login successfully so they should probably be punished in some way
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
