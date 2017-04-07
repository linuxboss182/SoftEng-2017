package controllers;

import entities.Professional;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

import static main.ApplicationController.directory;

/**
 * Created by s7sal on 4/4/2017.
 */
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

	private EditorController editorController;
	private ChoiceBox cb;

	@Override
	public void initialize(URL location, ResourceBundle resources) {

	}
	public void setChoiceBox(ChoiceBox cb) {
		this.cb = cb;
	}

	public void setEditorController(EditorController editorController){
		this.editorController = editorController;
	}
	@FXML
	public void onAddProBtnClicked() {
		Professional newPro = new Professional(this.givenNameField.getText(), this.surnameField.getText(), this.titleField.getText());
		directory.addProfessional(newPro);
		//this.editorController.populateChoiceBox();


	}

}
