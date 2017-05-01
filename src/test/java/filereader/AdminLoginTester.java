//package controllers.filereader;
//
//import controllers.shared.LoginController;
//import controllers.shared.LoginHandler;
//import javafx.scene.control.PasswordField;
//import javafx.scene.control.TextField;
//import org.junit.Assert;
//import org.junit.Test;
//
//import java.io.IOException;
//import java.lang.reflect.InvocationTargetException;
//
///**
// * Created by Walt on 4/21/2017.
// */
//public class AdminLoginTester
//{
//	TextField usernameField;
//	PasswordField passwordField;
//
//	@Test
//	public void basicLogin(){
//		LoginHandler login = new LoginHandler();
//		login.addAccount("Frank", "12345", true);
//		Assert.assertEquals(2, login.checkLogin("Frank", "12345"));
//	}
//
//	@Test
//	public void basicFailedLogin(){
//		LoginHandler login = new LoginHandler();
//		login.addAccount("Frank", "12345", true);
//		Assert.assertEquals(0, login.checkLogin("Fronk", "12345"));
//	}
//
//	@Test
//	public void testCaseSensitive(){
//		LoginHandler login = new LoginHandler();
//		login.addAccount("Frank", "12345", true);
//		Assert.assertEquals(2, login.checkLogin("frank", "12345"));
//	}
//
//	@Test
//	public void professionalLogin(){
//		LoginHandler login = new LoginHandler();
//		login.addAccount("Frank", "12345", false);
//		Assert.assertEquals(1, login.checkLogin("Frank", "12345"));
//	}
//
//	@Test
//	public void basicFailedProLogin(){
//		LoginHandler login = new LoginHandler();
//		login.addAccount("Frank", "12345", false);
//		Assert.assertEquals(0, login.checkLogin("Fronk", "12345"));
//	}
//
//	@Test
//	public void removeAdmin(){
//		LoginHandler login = new LoginHandler();
//		login.addAccount("Frank", "12345", true);
//		login.removeAccount("Frank");
//		Assert.assertEquals(0, login.checkLogin("Frank", "12345"));
//	}
//
//	@Test
//	public void removeProfessional(){
//		LoginHandler login = new LoginHandler();
//		login.addAccount("Frank", "12345", false);
//		login.removeAccount("Frank");
//		Assert.assertEquals(0, login.checkLogin("Frank", "12345"));
//	}
//
//	@Test
//	public void doubleCountAdmin(){
//		LoginHandler login = new LoginHandler();
//		login.addAccount("Frank", "12345", true);
//		login.addAccount("Frank", "54321", true);
//		Assert.assertEquals(2, login.checkLogin("Frank", "12345"));
//	}
//
//	@Test
//	public void doubleCountPro(){
//		LoginHandler login = new LoginHandler();
//		login.addAccount("Frank", "12345", false);
//		login.addAccount("Frank", "54321", false);
//		Assert.assertEquals(1, login.checkLogin("Frank", "12345"));
//	}
//
//	@Test
//	public void doubleCountBoth(){
//		LoginHandler login = new LoginHandler();
//		login.addAccount("Frank", "12345", true);
//		login.addAccount("Frank", "12345", false);
//		Assert.assertEquals(2, login.checkLogin("Frank", "12345"));
//	}
//
//	@Test
//	public void changeAdminPassword(){
//		LoginHandler login = new LoginHandler();
//		login.addAccount("Frank", "12345", true);
//		login.changePassword("Frank", "12345", "54321");
//		Assert.assertEquals(2, login.checkLogin("Frank", "54321"));
//	}
//
//	@Test
//	public void changeProPassword(){
//		LoginHandler login = new LoginHandler();
//		login.addAccount("Frank", "12345", false);
//		login.changePassword("Frank", "12345", "54321");
//		Assert.assertEquals(1, login.checkLogin("Frank", "54321"));
//	}
//}
