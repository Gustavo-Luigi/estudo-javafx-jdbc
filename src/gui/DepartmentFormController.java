package gui;

import db.DbException;
import gui.util.Alerts;
import gui.util.Constraints;
import gui.util.Utils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.entities.Department;
import model.services.DepartmentService;

import java.net.URL;
import java.util.ResourceBundle;

public class DepartmentFormController implements Initializable {

    private Department department;
    private DepartmentService service;

    @FXML
    private TextField txtId;
    @FXML
    private TextField txtName;
    @FXML
    private Label labelErrorName;
    @FXML
    private Button btnSave;
    @FXML
    private Button btnCancel;

    @FXML
    public void onBtnSaveAction(ActionEvent event) {
        if(department == null) {
            throw new IllegalStateException("Entity was null");
        }
        if(service == null) {
            throw new IllegalStateException("Entity was null");
        }
        try {
            department = getFormData();
            service.saveOrUpdate(department);
            Utils.currentStage(event).close();
        } catch (DbException e) {
            Alerts.showAlert("Error saving object", null, e.getMessage(), Alert.AlertType.ERROR);
        }

    }

    private Department getFormData() {
        Department department = new Department();
        department.setId(Utils.tryParseToInt(txtId.getText()));
        department.setName(txtName.getText());

        return department;
    }

    @FXML
    public void onBtnCancelAction(ActionEvent event) {
        Utils.currentStage(event).close();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeNodes();
    }

    private void initializeNodes() {
        Constraints.setTextFieldInteger(txtId);
        Constraints.setTextFieldMaxLength(txtName, 30);
    }

    public void updateFormData() {
        if (department == null) {
            throw new IllegalStateException("Department is null");
        }
        txtId.setText(String.valueOf(department.getId()));
        txtName.setText(department.getName());
    }

    public void setService(DepartmentService service) {
        this.service = service;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }
}
