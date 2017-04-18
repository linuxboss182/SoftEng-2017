package controllers.admin;

import entities.Professional;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

import static main.ApplicationController.getDirectory;


public class AddProfessionalController implements Initializable
{

	@FXML
	private Button addProBtn;
	@FXML
	private Button cancelBtn;
	@FXML
	private TextField givenNameField;
	@FXML
	private TextField surnameField;
	@FXML
	private TextField titleField;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
	}

	@FXML
	public void onAddProBtnClicked() {
		getDirectory().addNewProfessional(this.givenNameField.getText(), this.surnameField.getText(), this.titleField.getText());
		addProBtn.getScene().getWindow().hide();
	}

	@FXML
	public void onCancelBtnClick(){
		cancelBtn.getScene().getWindow().hide();
	}


}
