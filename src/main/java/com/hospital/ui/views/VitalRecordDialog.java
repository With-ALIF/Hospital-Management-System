package com.hospital.ui.views;

import com.hospital.model.Patient;
import com.hospital.model.VitalRecord;
import com.hospital.ui.AppState;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.List;

final class VitalRecordDialog {
    private VitalRecordDialog() {
    }

    static void show(AppState state, Runnable onSaved) {
        Dialog<Void> d = new Dialog<>();
        d.setTitle("Record Vitals");
        d.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        ComboBox<String> patientBox = new ComboBox<>(FXCollections.observableArrayList(patientOptions(state)));
        patientBox.setPromptText("Select patient");
        patientBox.setMaxWidth(Double.MAX_VALUE);

        CheckBox newPatient = new CheckBox("New patient (not in list)");
        TextField newName = new TextField();
        newName.setPromptText("Full name, e.g. Rahim Uddin");
        TextField newPhone = new TextField();
        newPhone.setPromptText("Phone, e.g. 01700000000");
        VBox newPatientBox = new VBox(6, new Label("Full name *"), newName,
                new Label("Phone *"), newPhone);
        newPatientBox.setVisible(false);
        newPatientBox.setManaged(false);
        newPatient.selectedProperty().addListener((o, oldV, val) -> {
            newPatientBox.setVisible(val);
            newPatientBox.setManaged(val);
            patientBox.setDisable(val);
        });

        TextField temp = numField("°C, e.g. 37.0");
        TextField hr = numField("bpm, e.g. 80");
        TextField sys = numField("SYS, e.g. 120");
        TextField dia = numField("DIA, e.g. 80");
        TextField spo2 = numField("%, e.g. 98");
        TextField rr = numField("/min, e.g. 16");
        TextField by = new TextField();
        by.setPromptText("Recorded by, e.g. STF-0001");
        TextField notes = new TextField();
        notes.setPromptText("Notes (optional)");

        HBox bpRow = new HBox(8, sys, dia);
        HBox.setHgrow(sys, Priority.ALWAYS);
        HBox.setHgrow(dia, Priority.ALWAYS);
        HBox vitalsRow1 = new HBox(8, fieldBox("Temp *", temp), fieldBox("Heart rate *", hr));
        HBox vitalsRow2 = new HBox(8, fieldBox("SpO2 *", spo2), fieldBox("Resp. rate *", rr));
        for (HBox row : List.of(vitalsRow1, vitalsRow2)) {
            row.getChildren().forEach(n -> HBox.setHgrow(n, Priority.ALWAYS));
        }

        VBox box = new VBox(8,
                new Label("Patient"), patientBox, newPatient, newPatientBox,
                vitalsRow1, vitalsRow2,
                new Label("Blood pressure *"), bpRow,
                new Label("Recorded by"), by,
                new Label("Notes"), notes);
        box.setPadding(new Insets(16));
        d.getDialogPane().setContent(box);
        d.setResultConverter(b -> {
            if (b == ButtonType.OK) {
                try {
                    VitalRecord created = save(state, patientBox.getValue(),
                            newPatient.isSelected(), newName.getText(), newPhone.getText(),
                            temp.getText(), hr.getText(), sys.getText(), dia.getText(),
                            spo2.getText(), rr.getText(), by.getText(), notes.getText());
                    if (onSaved != null) onSaved.run();
                    Alert ok = new Alert(Alert.AlertType.INFORMATION);
                    ok.setTitle("Vitals recorded");
                    ok.setContentText("Saved " + created.getId() + " — status: "
                            + created.getOverallStatus() + ".");
                    ok.showAndWait();
                } catch (Exception ex) {
                    Alert err = new Alert(Alert.AlertType.ERROR);
                    err.setTitle("Cannot save vitals");
                    err.setContentText(ex.getMessage());
                    err.showAndWait();
                }
            }
            return null;
        });
        d.showAndWait();
    }

    private static TextField numField(String prompt) {
        TextField f = new TextField();
        f.setPromptText(prompt);
        f.getStyleClass().add("tnum");
        return f;
    }

    private static VBox fieldBox(String label, TextField field) {
        VBox v = new VBox(4, new Label(label), field);
        HBox.setHgrow(v, Priority.ALWAYS);
        return v;
    }

    private static List<String> patientOptions(AppState state) {
        List<Patient> all;
        try {
            all = state.patientService.getAllPatients();
        } catch (Exception ex) {
            all = state.patients;
        }
        if (all == null) return List.of();
        return all.stream()
                .map(p -> p.getId() + " — " + p.getName())
                .sorted()
                .toList();
    }

    private static VitalRecord save(AppState state, String patientOption, boolean isNew,
                                    String newName, String newPhone,
                                    String tempT, String hrT, String sysT, String diaT,
                                    String spo2T, String rrT, String by, String notes) {
        String patientId;
        if (isNew) {
            String name = newName == null ? "" : newName.trim();
            String phone = newPhone == null ? "" : newPhone.trim();
            if (name.isEmpty()) throw new IllegalArgumentException("New patient name is required.");
            if (phone.isEmpty()) throw new IllegalArgumentException("New patient phone is required.");
            String id = state.patientService.nextPatientId();
            Patient p = new Patient(id, name, phone, "Other", "", phone, 0, "");
            state.patientService.registerPatient(p);
            state.refreshFromService();
            patientId = id;
        } else {
            if (patientOption == null || patientOption.isBlank())
                throw new IllegalArgumentException("Please select a patient or tick 'New patient'.");
            patientId = patientOption.split(" — ")[0].trim();
        }

        double temp = parseDouble(tempT, "Temperature");
        int hr = parseInt(hrT, "Heart rate");
        int sys = parseInt(sysT, "Blood pressure SYS");
        int dia = parseInt(diaT, "Blood pressure DIA");
        int spo2 = parseInt(spo2T, "SpO2");
        int rr = parseInt(rrT, "Respiratory rate");
        String bp = sys + "/" + dia;
        String recorder = by == null || by.isBlank() ? "SYSTEM" : by.trim();

        VitalRecord v = state.vitalMonitoringService.record(
                patientId, temp, hr, bp, spo2, rr, recorder);
        if (notes != null && !notes.isBlank()) {
            v.setNotes(notes.trim());
        }
        return v;
    }

    private static double parseDouble(String text, String label) {
        if (text == null || text.isBlank()) throw new IllegalArgumentException(label + " is required.");
        try {
            return Double.parseDouble(text.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(label + " must be a number.");
        }
    }

    private static int parseInt(String text, String label) {
        if (text == null || text.isBlank()) throw new IllegalArgumentException(label + " is required.");
        try {
            return Integer.parseInt(text.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(label + " must be a whole number.");
        }
    }
}
