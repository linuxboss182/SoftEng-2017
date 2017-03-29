package UserPanel;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class Window extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = (BorderPane)FXMLLoader.load(getClass().getResource("../Resources/FinalUI.fxml"));
        primaryStage.setTitle("Faulkner Hospital Navigator");
        primaryStage.setScene(new Scene(root, 1174, 722));
        primaryStage.show();
    }

}
