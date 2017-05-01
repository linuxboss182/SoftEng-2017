package controllers.user;


import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Button;


public class UserAboutPage
{
	@FXML
	private Button closeBtn;

	@FXML
	private void closeBtnClicked() {
		closeBtn.getScene().getWindow().hide();
	}



}
