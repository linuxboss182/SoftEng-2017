package ui.user;


import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import memento.TimeoutTimer;

import java.net.URL;
import java.util.ResourceBundle;


public class UserAboutPage implements Initializable
{
	@FXML
	private Button closeBtn;
	@FXML
	private AnchorPane backToPathClicked;
	protected TimeoutTimer timer = TimeoutTimer.getTimeoutTimer();

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		backToPathClicked.addEventFilter(MouseEvent.ANY, e-> {
			timer.resetTimer();
		});
		backToPathClicked.addEventFilter(KeyEvent.ANY, e ->{
			timer.resetTimer();
		});
		TimeoutTimer.getTimeoutTimer().registerTask(() -> {
			backToPathClicked.getScene().getWindow().hide();

		});
	}

	@FXML
	private void closeBtnClicked() {
		closeBtn.getScene().getWindow().hide();
	}

}
