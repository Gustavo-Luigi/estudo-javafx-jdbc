package gui;

import db.DbException;
import gui.listeners.DataChangeListener;
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
import model.entities.Seller;
import model.exceptions.ValidationException;
import model.services.SellerService;

import java.net.URL;
import java.util.*;

public class SellerFormController implements Initializable {

    private Seller seller;
    private SellerService service;
    private List<DataChangeListener> dataChangeListeners = new ArrayList<>();

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
        Constraints.setTextFieldMaxLength(txtName, 30);
    }

    public void updateFormData() {
        if (seller == null) {
            throw new IllegalStateException("Seller is null");
        }
        txtId.setText(String.valueOf(seller.getId()));
        txtName.setText(seller.getName());
    }

    public void setService(SellerService service) {
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
        }
    }

    public void setSeller(Seller seller) {
        this.seller = seller;
    }
}
