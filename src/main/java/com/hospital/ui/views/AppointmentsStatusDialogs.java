package com.hospital.ui.views;

import com.hospital.model.Appointment;
import com.hospital.ui.AppState;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.time.LocalDate;

final class AppointmentsStatusDialogs {
    private AppointmentsStatusDialogs() {
    }

    static void changeStatus(TableView<Appointment> table, AppState state, String target) {
        Appointment a = table.getSelectionModel().getSelectedItem();
        if (a == null) {
            AppointmentsDialogs.alert("Select an appointment first.");
            return;
        }
        try {
            switch (target) {
                case "CONFIRMED" -> state.appointmentService.confirmAppointment(a.getId());
                case "COMPLETED" -> state.appointmentService.completeAppointment(a.getId());
                case "CANCELLED" -> state.appointmentService.cancelAppointment(a.getId());
                default -> { }
            }
            state.refreshAppointments();
            AppointmentsDialogs.alert(target + " ✓  " + a.getId());
        } catch (Exception ex) {
            AppointmentsDialogs.error(ex.getMessage());
        }
    }

    static void reschedule(TableView<Appointment> table, AppState state) {
        Appointment a = table.getSelectionModel().getSelectedItem();
        if (a == null) {
            AppointmentsDialogs.alert("Select an appointment to reschedule.");
            return;
        }
        Dialog<Void> d = new Dialog<>();
        d.setTitle("Reschedule " + a.getId());
        d.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        DatePicker date = new DatePicker(a.getDate() != null ? a.getDate() : LocalDate.now());
        TextField time = new TextField(a.getTime() != null
                ? a.getTime().toString().substring(0, 5) : "10:30");
        time.setPromptText("HH:MM");
        VBox box = new VBox(8, new Label("New Date"), date, new Label("New Time"), time);
        box.setPadding(new Insets(16));
        d.getDialogPane().setContent(box);
        d.setResultConverter(b -> {
            if (b == ButtonType.OK) {
                try {
                    java.time.LocalTime t = java.time.LocalTime.parse(time.getText().trim());
                    state.appointmentService.rescheduleAppointment(a.getId(), date.getValue(), t);
                    state.refreshAppointments();
                    AppointmentsDialogs.alert("Rescheduled ✓");
                } catch (Exception ex) {
                    AppointmentsDialogs.error(ex.getMessage());
                }
            }
            return null;
        });
        d.showAndWait();
    }
}
