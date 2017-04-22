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
		login.addAccount("Frank", "12345", true);
		Assert.assertEquals(2, login.checkLogin("Frank", "12345"));
	}

	@Test
	public void basicFailedLogin(){
		LoginHandler login = new LoginHandler();
		login.addAccount("Frank", "12345", true);
		Assert.assertEquals(0, login.checkLogin("Fronk", "12345"));
	}

	@Test
	public void testCaseSensitive(){
		LoginHandler login = new LoginHandler();
		login.addAccount("Frank", "12345", true);
		Assert.assertEquals(2, login.checkLogin("frank", "12345"));
	}

	@Test
	public void professionalLogin(){
		LoginHandler login = new LoginHandler();
		login.addAccount("Frank", "12345", false);
		Assert.assertEquals(1, login.checkLogin("Frank", "12345"));
	}

	@Test
	public void basicFailedProLogin(){
		LoginHandler login = new LoginHandler();
		login.addAccount("Frank", "12345", false);
		Assert.assertEquals(0, login.checkLogin("Fronk", "12345"));
	}
}
