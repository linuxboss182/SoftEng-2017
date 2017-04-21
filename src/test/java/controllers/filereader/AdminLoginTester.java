package controllers.filereader;

import controllers.shared.LoginController;
import controllers.shared.LoginHandler;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by Walt on 4/21/2017.
 */
public class AdminLoginTester
{
	TextField usernameField;
	PasswordField passwordField;

	@Test
	public void basicLogin(){
		LoginHandler login = new LoginHandler();
		login.addAdmin("Frank", "12345");
		Assert.assertTrue(login.checkLogin("Frank", "12345"));
	}

	@Test
	public void basicFailedLogin(){
		LoginHandler login = new LoginHandler();
		login.addAdmin("Frank", "12345");
		Assert.assertFalse(login.checkLogin("Fronk", "12345"));
	}

	@Test
	public void testCaseSensitive(){
		LoginHandler login = new LoginHandler();
		login.addAdmin("Frank", "12345");
		Assert.assertTrue(login.checkLogin("frank", "12345"));
	}
}
