package controllers;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class SMSController implements Initializable
{

	@FXML
	private Button cancelBtn;
	@FXML
	private Button SendBtn;
	@FXML
	private TextField phoneNumField;
	@FXML
	private Label errorLabel;

	String text;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		errorLabel.setVisible(false);
	}

	@FXML
	public void onSendBtnClicked() {
		try {
			String accountSid = "ACf281d4ea0146ff8ba3b28d0451ef534c"; // Your Account SID from www.twilio.com/user/account

			String authToken = "6ce6450f4fdf3ce3318433197eead0cf"; // Your Auth Token from www.twilio.com/user/account

			Twilio.init(accountSid, authToken);

			System.out.println("phoneNumField = " + phoneNumField.getText());
			System.out.println("this.text = " + this.text);

			Message message = Message.creator(
					new PhoneNumber(phoneNumField.getText()),  // To number
					new PhoneNumber("+15086199280"),  // From number
					this.text                    // SMS body
			).create();

			System.out.println(message.getSid());
			SendBtn.getScene().getWindow().hide();
		} catch(Exception e){
			errorLabel.setVisible(true);
			this.errorLabel.setText("Error Sending SMS");
		}
	}

	@FXML
	public void onCancelBtnClick(){
		cancelBtn.getScene().getWindow().hide();
	}

	public void setText(String text) {
		System.out.println("text = " + text);
		this.text = text;
	}
}
