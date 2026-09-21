package com.hospital.ui;

import com.hospital.exception.InvalidDataException;
import com.hospital.model.Patient;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

public class PatientForm {
    public static VBox build(AppState state) {
        VBox card = new VBox(14);
        card.setMinWidth(320);
        card.setMaxWidth(340);
        card.getStyleClass().add("card");
        Label title = new Label("Register New Patient");
        title.getStyleClass().add("section-title");
        TextField idField = UIHelper.createStyledTextField("e.g. P-1004");
        idField.setText("P-" + (1001 + state.patients.size()));
        TextField nameField = UIHelper.createStyledTextField("e.g. Salim Ahmed");
        TextField phoneField = UIHelper.createStyledTextField("e.g. 01800112233");
        ComboBox<String> genderBox = new ComboBox<>();
        genderBox.getItems().addAll("MALE", "FEMALE", "OTHER");
        genderBox.setValue("MALE");
        genderBox.getStyleClass().add("combo-box");
        genderBox.setMaxWidth(Double.MAX_VALUE);
        ComboBox<String> bloodBox = new ComboBox<>();
        bloodBox.getItems().addAll("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-");
        bloodBox.setValue("O+");
        bloodBox.getStyleClass().add("combo-box");
        bloodBox.setMaxWidth(Double.MAX_VALUE);
        TextField emergencyField = UIHelper.createStyledTextField("e.g. 01900112233");
        Button addBtn = new Button("Register Patient");
        addBtn.getStyleClass().add("btn-success");
        addBtn.setMaxWidth(Double.MAX_VALUE);
        Button clearBtn = new Button("Clear Form");
        clearBtn.getStyleClass().add("button");
        clearBtn.setMaxWidth(Double.MAX_VALUE);
        addBtn.setOnAction(e -> {
            String id = idField.getText().trim(), name = nameField.getText().trim(), phone = phoneField.getText().trim();
            if (id.isEmpty() || name.isEmpty() || phone.isEmpty()) {
                UIHelper.showAlert(Alert.AlertType.ERROR, "Validation Error", "Please fill in all required fields.");
                return;
            }
            try {
                Patient p = new Patient(id, name, phone, genderBox.getValue(), bloodBox.getValue(), emergencyField.getText().trim());
                state.service.addPatient(p);
                state.patients.add(p);
                state.notifyChange();
                idField.setText("P-" + (1001 + state.patients.size()));
                nameField.clear();
                phoneField.clear();
                emergencyField.clear();
                UIHelper.showAlert(Alert.AlertType.INFORMATION, "Success", "Patient registered successfully!");
            } catch (InvalidDataException ex) {
                UIHelper.showAlert(Alert.AlertType.ERROR, "Error", ex.getMessage());
            }
        });
        clearBtn.setOnAction(e -> {
            idField.setText("P-" + (1001 + state.patients.size()));
            nameField.clear();
            phoneField.clear();
            emergencyField.clear();
        });
        card.getChildren().addAll(title, UIHelper.createFormField("Patient ID", idField),
                UIHelper.createFormField("Full Name", nameField), UIHelper.createFormField("Phone Number", phoneField),
                UIHelper.createFormField("Gender", genderBox), UIHelper.createFormField("Blood Group", bloodBox),
                UIHelper.createFormField("Emergency Contact", emergencyField), addBtn, clearBtn);
        return card;
    }
}
