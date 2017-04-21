package controllers.admin;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.net.URL;
import java.util.ResourceBundle;

import static main.ApplicationController.getDirectory;

public class AdminHelpController  implements Initializable
{
	@FXML
	private Button closeBtn;
	@FXML
	private TextFlow commandTextFlow;


	@Override
	public void initialize(URL location, ResourceBundle resources) {
		Text commands = new Text();
		commands.setText("Press Back Space for Deleting selected nodes\n" +
				"Press Ctrl + A for selecting all nodes\n" +
				"Press Ctrl + Open Bracket for zoom in\n" +
				"Press Ctrl + Close Bracket for zoom out\n" +
				"Press Shift + Right to move the view to the right\n" +
				"Press Shift + Left to move the view to the left\n" +
				"Press Shift + Up to move the view to the up\n" +
				"Press Shift + down to move the view to the down");

		commandTextFlow.getChildren().add(commands);
	}

	@FXML
	public void closeBtnClicked(){
		closeBtn.getScene().getWindow().hide();
	}


}

