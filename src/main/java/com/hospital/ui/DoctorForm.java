package com.hospital.ui;

import com.hospital.exception.InvalidDataException;
import com.hospital.model.Doctor;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

public class DoctorForm {
    public static VBox build(AppState state) {
        VBox card = new VBox(12);
        card.setMinWidth(340);
        card.setMaxWidth(360);
        card.getStyleClass().add("card");
        Label title = new Label("Add New Doctor");
        title.getStyleClass().add("section-title");
        TextField idField = UIHelper.createStyledTextField("e.g. D-1004");
        idField.setText("D-" + (1001 + state.doctors.size()));
        TextField nameField = UIHelper.createStyledTextField("e.g. Dr. Salman Khan");
        TextField phoneField = UIHelper.createStyledTextField("e.g. 01700112233");
        ComboBox<String> genderBox = new ComboBox<>();
        genderBox.getItems().addAll("MALE", "FEMALE", "OTHER");
        genderBox.setValue("MALE");
        genderBox.getStyleClass().add("combo-box");
        genderBox.setMaxWidth(Double.MAX_VALUE);
        ComboBox<String> specBox = new ComboBox<>();
        specBox.getItems().addAll("Cardiology", "Neurology", "Pediatrics", "Orthopedics", "General Surgery", "Dermatology", "ENT");
        specBox.setValue("Cardiology");
        specBox.getStyleClass().add("combo-box");
        specBox.setMaxWidth(Double.MAX_VALUE);
        TextField slot1 = UIHelper.createStyledTextField("Duty Slot 1: e.g. 09:00-13:00");
        slot1.setText("09:00-13:00");
        TextField slot2 = UIHelper.createStyledTextField("Duty Slot 2 (Optional): e.g. 15:00-18:00");
        slot2.setText("15:00-18:00");
        CheckBox availBox = new CheckBox("Currently Available / On Duty");
        availBox.setSelected(true);
        availBox.getStyleClass().add("check-box");
        Button addBtn = new Button("Register Doctor");
        addBtn.getStyleClass().add("btn-primary");
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
                Doctor d = new Doctor(id, name, phone, genderBox.getValue(), specBox.getValue());
                d.setAvailable(availBox.isSelected());
                SlotParser.parseAndAdd(d, slot1.getText().trim());
                SlotParser.parseAndAdd(d, slot2.getText().trim());
                state.service.addDoctor(d);
                state.doctors.add(d);
                state.notifyChange();
                idField.setText("D-" + (1001 + state.doctors.size()));
                nameField.clear();
                phoneField.clear();
                UIHelper.showAlert(Alert.AlertType.INFORMATION, "Success", "Doctor registered successfully with duty schedule!");
            } catch (InvalidDataException ex) {
                UIHelper.showAlert(Alert.AlertType.ERROR, "Error", ex.getMessage());
            }
        });
        clearBtn.setOnAction(e -> {
            idField.setText("D-" + (1001 + state.doctors.size()));
            nameField.clear();
            phoneField.clear();
        });
        card.getChildren().addAll(title, UIHelper.createFormField("Doctor ID", idField),
                UIHelper.createFormField("Full Name", nameField), UIHelper.createFormField("Phone Number", phoneField),
                UIHelper.createFormField("Gender", genderBox), UIHelper.createFormField("Specialization", specBox),
                UIHelper.createFormField("Duty Hours Slot 1", slot1), UIHelper.createFormField("Duty Hours Slot 2", slot2),
                availBox, addBtn, clearBtn);
        return card;
    }
}
