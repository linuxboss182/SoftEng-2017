package ui.user;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;

import java.net.URL;
import java.util.ResourceBundle;

public class UserHelpController  implements Initializable
{
	@FXML
	private Button closeBtn;
	@FXML
	private TextFlow userHelpTextFlow;


	@Override
	public void initialize(URL location, ResourceBundle resources) {
		Text userHelp = new Text();
//		userHelp.setWrappingWidth(700);
		userHelp.setTextAlignment(TextAlignment.RIGHT);
		userHelp.setStyle("-fx-font-size: 16.0;");
		userHelp.setText("Press the three line icon on the top left\n" +
				"Press the text field to start finding location\n" +
				"Press \"Change starting location\" for new starting location\n" +
				"Press \"Get Directions\" to start navigating\n" +
				"Press \"Bathroom\" to find nearest bathroom\n" +
				"Press \"About\" to learn about this application\n" +
				"Pressing on circles on the map will choose that as your location\n" +
				"Using the scroll wheel on your mouse or the slider will zoom in and out");

		userHelpTextFlow.getChildren().add(userHelp);


	}

	@FXML
	public void closeBtnClicked(){
		closeBtn.getScene().getWindow().hide();
	}


}

