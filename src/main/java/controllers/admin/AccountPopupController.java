package controllers.admin;

import entities.Account;
import entities.Professional;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.util.Callback;

import java.net.URL;
import java.util.ResourceBundle;

import static main.ApplicationController.getDirectory;


public class AccountPopupController
		implements Initializable
{
	@FXML private Button doneBtn;
	@FXML private TableView<Account> accountTableView;
	@FXML private TableColumn usernameCol;
	@FXML private TableColumn passwordCol;
	@FXML private TableColumn permissionsCol;


	@Override
	public void initialize(URL location, ResourceBundle resources) {
		this.accountTableView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Account>() {
			@Override
			public void changed(ObservableValue<? extends Account> observable, Account oldValue, Account newValue) {
//				accountTableView = newValue;
			}
		});

		populateTableView();
	}

	public void populateTableView(){
		usernameCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Account, String>, ObservableValue<String>>() {
			@Override
			public ObservableValue<String> call(TableColumn.CellDataFeatures<Account, String> cdf) {
				return new SimpleStringProperty(cdf.getValue().getUsername());
			}
		});

		passwordCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Account, String>, ObservableValue<String>>() {
			@Override
			public ObservableValue<String> call(TableColumn.CellDataFeatures<Account, String> cdf) {
				return new SimpleStringProperty(cdf.getValue().getPassword());
			}
		});

		permissionsCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Account, String>, ObservableValue<String>>() {
			@Override
			public ObservableValue<String> call(TableColumn.CellDataFeatures<Account, String> cdf) {
				return new SimpleStringProperty(cdf.getValue().getPermissions());
			}
		});

		accountTableView.getSortOrder().add(usernameCol);
		accountTableView.getSortOrder().add(passwordCol);
		accountTableView.getSortOrder().add(permissionsCol);

		accountTableView.getItems().setAll(getDirectory().getAccounts().values());
	}

	@FXML
	public void ondoneBtnClick(){
		doneBtn.getScene().getWindow().hide();
	}

}
