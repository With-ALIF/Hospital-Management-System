package com.hospital.ui.views;

import com.hospital.model.Appointment;
import com.hospital.model.Doctor;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.time.LocalTime;
import java.util.List;

final class DoctorScheduleParts {
    private DoctorScheduleParts() {
    }

    static HBox legendDot(String color, String label) {
        Region dot = new Region();
        dot.setStyle("-fx-background-color:" + color
                + ";-fx-min-width:8;-fx-min-height:8;-fx-max-width:8;-fx-max-height:8;-fx-background-radius:4;");
        Label l = new Label(label);
        l.setStyle("-fx-font-size:11px;-fx-text-fill:#94A3B8;");
        HBox b = new HBox(6, dot, l);
        b.setAlignment(Pos.CENTER_LEFT);
        return b;
    }

    static HBox slotRow(Doctor doc, List<Appointment> appts, int hour, int minute, LocalTime t) {
        HBox row = new HBox(12);
        row.setPadding(new Insets(9, 16, 9, 16));
        row.setAlignment(Pos.CENTER_LEFT);
        Label time = new Label(String.format("%02d:%02d", hour, minute));
        time.setPrefWidth(56);
        time.setStyle("-fx-font-weight:600;-fx-font-size:11.5px;-fx-font-variant-numeric:tabular-nums;-fx-text-fill:#CBD5E1;");
        var apptList = appts.stream().filter(a -> a.getTime() != null && a.getTime().equals(t)).toList();
        if (apptList.size() > 1) {
            row.setStyle("-fx-background-color:rgba(239,68,68,0.15);-fx-border-color:#7F1D1D;-fx-border-width:0 0 0 3;");
            Label info = new Label("Conflict — " + apptList.size() + " appointments at " + time.getText());
            info.setStyle("-fx-text-fill:#FCA5A5;-fx-font-weight:600;-fx-font-size:11.5px;");
            row.getChildren().addAll(time, info);
        } else if (!apptList.isEmpty()) {
            fillAppointment(row, time, apptList.get(0));
        } else {
            fillAvailable(row, time, doc, t);
        }
        return row;
    }

    private static void fillAppointment(HBox row, Label time, Appointment a) {
        boolean cancelled = a.getStatus() != null && "CANCELLED".equals(a.getStatus().name());
        row.setStyle(cancelled
                ? "-fx-background-color:#1E1B4B;-fx-opacity:0.7;"
                : "-fx-background-color:rgba(99,102,241,0.18);-fx-border-color:#4338CA;-fx-border-width:0 0 0 3;");
        Label pat = new Label(a.getPatientName());
        pat.setStyle("-fx-font-weight:500;-fx-font-size:12px;-fx-text-fill:#F1F5F9;");
        Label reason = new Label(a.getReason() != null ? a.getReason() : "");
        reason.setStyle("-fx-text-fill:#94A3B8;-fx-font-size:11px;");
        Label st = new Label(a.getStatus() != null ? a.getStatus().name() : "");
        st.getStyleClass().addAll("badge", badgeFor(a.getStatus() != null ? a.getStatus().name() : ""));
        Region sp2 = new Region();
        HBox.setHgrow(sp2, Priority.ALWAYS);
        row.getChildren().addAll(time, pat, reason, sp2, st);
    }

    private static void fillAvailable(HBox row, Label time, Doctor doc, LocalTime t) {
        boolean availSlot = doc.isAvailableAt(t) && doc.getAvailable();
        Label st = new Label(availSlot ? "Available" : "Unavailable");
        st.getStyleClass().addAll("badge", availSlot ? "badge-success" : "badge-neutral");
        Region sp2 = new Region();
        HBox.setHgrow(sp2, Priority.ALWAYS);
        row.getChildren().addAll(time, sp2, st);
        if (!availSlot) row.setOpacity(0.55);
    }

    static String badgeFor(String s) {
        if (s == null) return "badge-neutral";
        return switch (s.toUpperCase()) {
            case "SCHEDULED" -> "badge-info";
            case "CONFIRMED", "COMPLETED" -> "badge-success";
            case "CANCELLED" -> "badge-danger";
            default -> "badge-neutral";
        };
    }
}
