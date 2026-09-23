package com.hospital.ui.views;

import com.hospital.model.Appointment;
import com.hospital.ui.AppState;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.time.LocalDate;

final class AppointmentsDialogs {
    private AppointmentsDialogs() {
    }

    static void changeStatus(TableView<Appointment> table, AppState state, String target) {
        AppointmentsStatusDialogs.changeStatus(table, state, target);
    }

    static void rescheduleDialog(TableView<Appointment> table, AppState state) {
        AppointmentsStatusDialogs.reschedule(table, state);
    }

    static void showCreateDialog(AppState state) {
        AppointmentsCreateDialog.show(state);
    }

    static void show(Appointment a) {
        Alert al = new Alert(Alert.AlertType.INFORMATION);
        al.setTitle("Appointment " + a.getId());
        al.setHeaderText(a.getPatientName() + " → " + a.getDoctorName());
        al.setContentText("Date: " + a.getDate() + "\nTime: " + a.getFormattedTime()
                + "\nStatus: " + a.getStatus() + "\nReason: " + a.getReason()
                + "\nCreated: " + (a.getCreatedAt() != null
                        ? a.getCreatedAt().toString().substring(0, 16) : "—"));
        al.showAndWait();
    }

    static void alert(String m) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setHeaderText(null);
        a.setContentText(m);
        a.showAndWait();
    }

    static void error(String m) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setHeaderText(null);
        a.setContentText(m);
        a.showAndWait();
    }
}
