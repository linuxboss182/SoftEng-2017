package userpanel;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;

import java.awt.*;

public class Controller {
    @FXML
    private Button logAsAdmin;

    @FXML
    private void logAsAdminClicked() throws Exception{
//        Parent root = (AnchorPane)FXMLLoader.load(getClass().getResource("../resources/AdminUI.fxml"));
//        adminScene.setTitle("Faulkner Hospital Navigator");
//        primaryStage.setScene(new Scene(root, 1174, 722));
//        primaryStage.setScene(adminStage);
//        adminScene.show();

        Parent root;
        root = FXMLLoader.load(getClass().getResource("../resources/AdminUI.fxml"));
        Stage stage = new Stage();
        stage.setTitle("My New Stage Title");
        stage.setScene(new Scene(root, 1174, 722));
        stage.show();

    }
}
