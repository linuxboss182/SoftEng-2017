package controllers.shared;


import controllers.user.UserState;
import entities.Account;
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
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;

import javafx.scene.input.KeyEvent;
import main.ApplicationController;

import javafx.scene.input.KeyEvent;
import main.TimeoutTimer;

import java.awt.*;
import java.beans.EventHandler;
import java.io.IOException;
import java.net.URL;

import java.util.Map;
import java.util.ResourceBundle;
import java.util.TimerTask;

public class LoginController implements Initializable{

	@FXML private Label errorLbl;
	@FXML private Button cancelBtn;
	@FXML private TextField usernameField;
	@FXML private PasswordField passwordField;
	@FXML private Button loginBtn;
	@FXML private BorderPane parentBorderPane;

	private Directory directory = ApplicationController.getDirectory();
	private TimeoutTimer timer = TimeoutTimer.getTimeoutTimer();

	@Override
	public void initialize(URL location, ResourceBundle resources) {
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

		timer.emptyTasks();
		this.initGlobalFilter();
		TimeoutTimer.getTimeoutTimer().registerTask(() -> {
			setState(directory.getCaretaker().getState());
		});
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
		Account thisAccount = directory.getAccount(username);
		if(thisAccount == null){
			return LoginStatus.FAILURE;
		}

		// Safe because the empty string is not a valid password
		if (thisAccount.getPassword().equals(password)) {
			switch (thisAccount.getPermissions().toUpperCase()) {
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

	/**
	 * Initializes the global filter that will reset the timer whenever an action is performed.
	 */
	protected void initGlobalFilter() {
		this.parentBorderPane.addEventFilter(MouseEvent.ANY, e-> {
			timer.resetTimer();
		});
		this.parentBorderPane.addEventFilter(KeyEvent.ANY, e-> {
			timer.resetTimer();
		});
	}

	protected TimerTask getTimerTask() {
		return new TimerTask()
		{
			public void run() {
				setState(directory.getCaretaker().getState());
			}
		};
	}

	// place inside controller
	public void setState(UserState state) {
		parentBorderPane.getScene().setRoot(state.getRoot());
		this.directory.logOut();
	}
}
