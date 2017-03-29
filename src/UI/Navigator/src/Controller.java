import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import java.awt.*;

public class Controller {
    @FXML
    private Button logAsAdmin;
    @FXML
    private Scene adminScene;

    @FXML
    private void logAsAdminClicked() throws Exception{
        Parent root = (AnchorPane)FXMLLoader.load(getClass().getResource("AdminUI.fxml"));
        //adminStage.setTitle("Faulkner Hospital Navigator");

        primaryStage.setScene(new Scene(root, 1174, 722));
        primaryStage.setScene(adminStage);
        adminScene.show();

    }

}
