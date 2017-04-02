package adminpanel;


import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public class LoginController
{

	@FXML
	public void loginClicked() throws IOException, InvocationTargetException {
		Parent adminUI = (BorderPane) FXMLLoader.load(getClass().getResource("/AdminUI.fxml"));
		System.out.print("Onto the Admin UI");
		Scene adminScene = new Scene(adminUI);
		Stage adminStage = new Stage();
		adminStage.setScene(adminScene);

		adminStage.showAndWait();
	}
}
