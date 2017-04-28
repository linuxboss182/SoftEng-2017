package controllers.admin;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

import static main.ApplicationController.getDirectory;


public class AccountPopupController
		implements Initializable
{
	@FXML
	private Button doneBtn;
	@FXML
	private TableView accountTableView;


	@Override
	public void initialize(URL location, ResourceBundle resources) {
	}


	@FXML
	public void ondoneBtnClick(){
		doneBtn.getScene().getWindow().hide();
	}

}
