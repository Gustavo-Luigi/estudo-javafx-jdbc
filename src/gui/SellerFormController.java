package gui;

import db.DbException;
import gui.listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Constraints;
import gui.util.Utils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.util.Callback;
import model.entities.Department;
import model.entities.Seller;
import model.exceptions.ValidationException;
import model.services.DepartmentService;
import model.services.SellerService;

import java.net.URL;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

public class SellerFormController implements Initializable {

    private Seller seller;
    private SellerService service;
    private DepartmentService departmentService;
    private List<DataChangeListener> dataChangeListeners = new ArrayList<>();
    private ObservableList<Department> obsList;

    @FXML
    private TextField txtId;
    @FXML
    private TextField txtName;
    @FXML
    private TextField txtEmail;
    @FXML
    private DatePicker dpBirthDate;
    @FXML
    private TextField txtBaseSalary;
    @FXML
    private ComboBox<Department> comboBoxDepartment;
    @FXML
    private Label labelErrorName;
    @FXML
    private Label labelErrorEmail;
    @FXML
    private Label labelErrorBirthDate;
    @FXML
    private Label labelErrorBaseSalary;
    @FXML
    private Button btnSave;
    @FXML
    private Button btnCancel;

    @FXML
    public void onBtnSaveAction(ActionEvent event) {
        if(seller == null) {
            throw new IllegalStateException("Entity was null");
        }
        if(service == null) {
            throw new IllegalStateException("Entity was null");
        }
        try {
            seller = getFormData();
            service.saveOrUpdate(seller);
            notifyDataChangeListeners();
            Utils.currentStage(event).close();
        } catch (DbException e) {
            Alerts.showAlert("Error saving object", null, e.getMessage(), Alert.AlertType.ERROR);
        } catch (ValidationException e) {
            setErrorMessages(e.getErrors());
        }

    }



    private Seller getFormData() {
        Seller seller = new Seller();

        ValidationException exception = new ValidationException("Validation Error");

        seller.setId(Utils.tryParseToInt(txtId.getText()));

        if (txtName.getText() == null || txtName.getText().trim().equals("")) {
            exception.addError("Name", "Field can't be empty");
        }
        seller.setName(txtName.getText());

        if (txtEmail.getText() == null || txtEmail.getText().trim().equals("")) {
            exception.addError("Email", "Field can't be empty");
        }
        seller.setEmail(txtEmail.getText());

        if (dpBirthDate.getValue() == null) {
            exception.addError("Birth Date", "Field can't be empty");
        }
        Instant instant = Instant.from(dpBirthDate.getValue().atStartOfDay(ZoneId.systemDefault()));
        seller.setBirthDate(Date.from(instant));

        if (txtBaseSalary.getText() == null || txtBaseSalary.getText().trim().equals("")) {
            exception.addError("Base Salary", "Field can't be empty");
        }
        seller.setBaseSalary(Utils.tryParseToDouble(txtBaseSalary.getText()));

        seller.setDepartment(comboBoxDepartment.getValue());

        if (exception.getErrors().size() > 0) {
            throw exception;
        }

        return seller;
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
        Constraints.setTextFieldMaxLength(txtName, 50);
        Constraints.setTextFieldDouble(txtBaseSalary);
        Constraints.setTextFieldMaxLength(txtEmail, 30);
        Utils.formatDatePicker(dpBirthDate, "dd/MM/yyyy");
        initializeComboBoxDepartment();
    }

    public void updateFormData() {
        if (seller == null) {
            throw new IllegalStateException("Seller is null");
        }
        txtId.setText(String.valueOf(seller.getId()));
        txtName.setText(seller.getName());
        txtEmail.setText(seller.getEmail());
        Locale.setDefault(Locale.US);
        txtBaseSalary.setText(String.format("%.2f" , seller.getBaseSalary()));
        if (seller.getBirthDate() != null) {
            dpBirthDate.setValue(LocalDate.ofInstant(seller.getBirthDate().toInstant(), ZoneId.systemDefault()));
        }
        if (seller.getDepartment() == null) {
            comboBoxDepartment.getSelectionModel().selectFirst();
        } else {
            comboBoxDepartment.setValue(seller.getDepartment());
        }
    }

    public void loadAssociatedObjects() {
        List<Department> list = departmentService.findAll();
        obsList = FXCollections.observableArrayList(list);
        comboBoxDepartment.setItems(obsList);
    }

    public void setServices(SellerService service, DepartmentService departmentService) {
        this.departmentService = departmentService;
        this.service = service;
    }

    public void subscribeDataChangeListener(DataChangeListener listener) {
        dataChangeListeners.add(listener);
    }

    private void notifyDataChangeListeners() {
        for(DataChangeListener listener : dataChangeListeners) {
            listener.onDataChanged();
        }
    }

    private void setErrorMessages(Map<String, String> errors) {
        Set<String> fields = errors.keySet();

        if (fields.contains("Name")) {
            labelErrorName.setText(errors.get("Name"));
        } else {
            labelErrorName.setText(errors.get(""));
        }
        if (fields.contains("Email")) {
            labelErrorName.setText(errors.get("Email"));
        } else {
            labelErrorName.setText(errors.get(""));
        }
        if (fields.contains("Base Salary")) {
            labelErrorName.setText(errors.get("Base Salary"));
        } else {
            labelErrorName.setText(errors.get(""));
        }
        if (fields.contains("Birth Date")) {
            labelErrorName.setText(errors.get("Birth Date"));
        } else {
            labelErrorName.setText(errors.get(""));
        }
    }

    private void initializeComboBoxDepartment() {
        Callback<ListView<Department>, ListCell<Department>> factory = lv -> new ListCell<Department>() {
            @Override
            protected void updateItem(Department item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? "" : item.getName());
            }
        };
        comboBoxDepartment.setCellFactory(factory);
        comboBoxDepartment.setButtonCell(factory.call(null));
    }

    public void setSeller(Seller seller) {
        this.seller = seller;
    }
}
