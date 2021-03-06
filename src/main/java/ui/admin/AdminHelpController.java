package ui.admin;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;

import java.net.URL;
import java.util.ResourceBundle;

public class AdminHelpController  implements Initializable
{
	@FXML
	private Button closeBtn;
	@FXML
	private TextFlow commandTextFlow;


	@Override
	public void initialize(URL location, ResourceBundle resources) {
		Text commands = new Text();
		commands.setTextAlignment(TextAlignment.CENTER);
		commands.setStyle("-fx-font-size: 16.0;");
		commands.setText("Press Back Space to Delete selected nodes\n" +
				"Press Ctrl + A to select all nodes\n" +
				"Press Ctrl + Open Bracket to zoom in\n" +
				"Press Ctrl + Close Bracket to zoom out\n" +
				"Press Shift + Right to move the view to the right\n" +
				"Press Shift + Left to move the view to the left\n" +
				"Press Shift + Up to raise the view\n" +
				"Press Shift + down to lower the view");

		commandTextFlow.getChildren().add(commands);



	}

	@FXML
	public void closeBtnClicked(){
		closeBtn.getScene().getWindow().hide();
	}


}

