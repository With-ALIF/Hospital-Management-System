package com.hospital.ui.views;

import com.hospital.model.FollowUp;
import com.hospital.ui.AppState;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;

import java.time.LocalDate;
import java.util.List;

final class FollowUpDialog {
    private FollowUpDialog() {
    }

    static void show(AppState state, Runnable onSaved) {
        Dialog<Void> d = new Dialog<>();
        d.setTitle("Schedule Follow-Up");
        d.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        ComboBox<String> patient = combo(patientOptions(state), "Select patient");
        ComboBox<String> doctor = combo(doctorOptions(state), "Select doctor (optional)");
        DatePicker date = new DatePicker(LocalDate.now().plusDays(7));
        date.setMaxWidth(Double.MAX_VALUE);
        TextArea reason = new TextArea();
        reason.setPromptText("Reason, e.g. Suture removal check");
        reason.setPrefRowCount(2);
        reason.setWrapText(true);
        TextArea instructions = new TextArea();
        instructions.setPromptText("Instructions, e.g. Bring previous reports");
        instructions.setPrefRowCount(2);
        instructions.setWrapText(true);

        VBox box = new VBox(6,
                new Label("Patient *"), patient,
                new Label("Doctor"), doctor,
                new Label("Date *"), date,
                new Label("Reason *"), reason,
                new Label("Instructions"), instructions);
        box.setPadding(new Insets(16));
        d.getDialogPane().setContent(box);
        d.setResultConverter(b -> {
            if (b == ButtonType.OK) {
                try {
                    String pid = idOf(patient.getValue());
                    if (pid == null) throw new IllegalArgumentException("Please select a patient.");
                    FollowUp f = state.followUpService.schedule(
                            pid,
                            idOf(doctor.getValue()),
                            date.getValue(),
                            textOf(reason.getText(), "Reason"),
                            instructions.getText() == null ? "" : instructions.getText().trim());
                    if (onSaved != null) onSaved.run();
                    info("Follow-up " + f.getId() + " scheduled for " + f.getFollowUpDate() + ".");
                } catch (Exception ex) {
                    error(ex.getMessage());
                }
            }
            return null;
        });
        d.showAndWait();
    }

    private static String textOf(String text, String label) {
        if (text == null || text.isBlank()) throw new IllegalArgumentException(label + " is required.");
        return text.trim();
    }

    private static ComboBox<String> combo(List<String> items, String prompt) {
        ComboBox<String> c = new ComboBox<>(FXCollections.observableArrayList(items));
        c.setPromptText(prompt);
        c.setMaxWidth(Double.MAX_VALUE);
        return c;
    }

    private static List<String> patientOptions(AppState state) {
        try {
            return state.patientService.getAllPatients().stream()
                    .map(p -> p.getId() + " — " + p.getName()).sorted().toList();
        } catch (Exception ex) {
            return state.patients.stream()
                    .map(p -> p.getId() + " — " + p.getName()).sorted().toList();
        }
    }

    private static List<String> doctorOptions(AppState state) {
        try {
            return state.doctorService.getAllDoctors().stream()
                    .map(x -> x.getId() + " — " + x.getName()).sorted().toList();
        } catch (Exception ex) {
            return state.doctors.stream()
                    .map(x -> x.getId() + " — " + x.getName()).sorted().toList();
        }
    }

    private static String idOf(String option) {
        if (option == null || option.isBlank()) return null;
        return option.split(" — ")[0].trim();
    }

    private static void info(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle("Follow-up scheduled");
        a.setContentText(msg);
        a.showAndWait();
    }

    private static void error(String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle("Cannot schedule follow-up");
        a.setContentText(msg);
        a.showAndWait();
    }
}
