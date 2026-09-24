package com.hospital.ui.views;

import com.hospital.model.Appointment;
import com.hospital.ui.AppState;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.time.LocalDate;

final class AppointmentsCreateDialog {
    private AppointmentsCreateDialog() {
    }

    static void show(AppState state) {
        Dialog<Void> d = new Dialog<>();
        d.setTitle("Create Appointment");
        d.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        ComboBox<String> pat = new ComboBox<>(FXCollections.observableArrayList(
                state.patients.stream().map(p -> p.getId() + " — " + p.getName()).toList()));
        pat.setPromptText("Select Patient");
        pat.setPrefWidth(280);
        ComboBox<String> doc = new ComboBox<>(FXCollections.observableArrayList(
                state.doctors.stream().map(x -> x.getId() + " — " + x.getName()).toList()));
        doc.setPromptText("Select Doctor");
        doc.setPrefWidth(280);
        DatePicker date = new DatePicker(LocalDate.now());
        date.setPrefWidth(280);
        ComboBox<String> time = new ComboBox<>(FXCollections.observableArrayList(
                "09:00", "09:30", "10:00", "10:30", "11:00", "11:30",
                "12:00", "12:30", "14:00", "14:30", "15:00", "15:30",
                "16:00", "16:30", "17:00", "17:30", "18:00", "18:30",
                "19:00", "19:30", "20:00"));
        time.setPromptText("Select time");
        time.setPrefWidth(280);
        TextField reason = new TextField();
        reason.setPromptText("Reason");
        reason.setPrefWidth(280);
        VBox box = new VBox(6, new Label("Patient"), pat, new Label("Doctor"), doc,
                new Label("Date"), date, new Label("Time"), time, new Label("Reason"), reason);
        box.setPadding(new Insets(16));
        d.getDialogPane().setContent(box);
        d.setResultConverter(b -> {
            if (b == ButtonType.OK) {
                try {
                    create(state, pat, doc, date, time, reason);
                } catch (Exception ex) {
                    AppointmentsDialogs.error(ex.getMessage());
                }
            }
            return null;
        });
        d.showAndWait();
    }

    private static void create(AppState state, ComboBox<String> pat, ComboBox<String> doc,
                               DatePicker date, ComboBox<String> time, TextField reason) {
        String pid = pat.getValue() != null ? pat.getValue().split(" — ")[0] : null;
        String did = doc.getValue() != null ? doc.getValue().split(" — ")[0] : null;
        if (pid == null || did == null || date.getValue() == null
                || time.getValue() == null || time.getValue().isBlank()
                || reason.getText().isBlank()) {
            throw new IllegalArgumentException("All fields are required");
        }
        java.time.LocalTime t = java.time.LocalTime.parse(time.getValue().trim());
        state.appointmentService.createAppointment(pid, did, date.getValue(), t, reason.getText().trim());
        state.refreshAppointments();
        AppointmentsDialogs.alert("Appointment created successfully.");
    }
}
