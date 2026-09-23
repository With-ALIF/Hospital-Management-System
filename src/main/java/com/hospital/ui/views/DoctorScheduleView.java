package com.hospital.ui.views;

import com.hospital.model.Appointment;
import com.hospital.model.Doctor;
import com.hospital.ui.AppState;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.time.LocalTime;

public class DoctorScheduleView {
    public static Pane build(AppState state) {
        VBox root = new VBox(16);
        root.getStyleClass().add("main-content");
        root.setPadding(new Insets(24));
        Label title = new Label("Doctor Schedule");
        title.getStyleClass().add("page-title");
        Label sub = new Label("Availability and daily timeline — conflicts highlighted");
        sub.getStyleClass().add("page-subtitle");
        VBox headText = new VBox(2, title, sub);
        ComboBox<String> docSel = new ComboBox<>(FXCollections.observableArrayList(
                state.doctors.stream()
                        .map(d -> d.getId() + " — " + d.getName() + " (" + d.getSpecialization() + ")")
                        .toList()));
        docSel.setPromptText("Select doctor");
        docSel.setPrefWidth(280);
        if (!state.doctors.isEmpty()) {
            Doctor first = state.doctors.get(0);
            docSel.setValue(first.getId() + " — " + first.getName() + " (" + first.getSpecialization() + ")");
        }
        HBox top = new HBox(12, headText, new Region(), docSel);
        top.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(top.getChildren().get(1), Priority.ALWAYS);
        VBox timeline = new VBox(0);
        timeline.getStyleClass().add("card");
        ScrollPane sp = new ScrollPane(timeline);
        sp.setFitToWidth(true);
        VBox.setVgrow(sp, Priority.ALWAYS);
        Runnable refresh = () -> fillTimeline(state, docSel, timeline);
        docSel.valueProperty().addListener((o, ov, nv) -> refresh.run());
        refresh.run();
        root.getChildren().addAll(top, sp);
        return root;
    }

    private static void fillTimeline(AppState state, ComboBox<String> docSel, VBox timeline) {
        timeline.getChildren().clear();
        String sel = docSel.getValue();
        if (sel == null) {
            timeline.getChildren().add(empty("Select a doctor", "Choose a doctor to view schedule."));
            return;
        }
        String did = sel.split(" — ")[0].trim();
        Doctor doc = state.doctors.stream().filter(d -> d.getId().equals(did)).findFirst().orElse(null);
        if (doc == null) return;
        timeline.getChildren().add(docHeader(doc));
        timeline.getChildren().add(new Separator());
        Label day = new Label("Today  •  " + java.time.LocalDate.now());
        day.setStyle("-fx-font-weight:600;-fx-text-fill:#E2E8F0;-fx-padding:10 16 6 16;-fx-font-size:12px;");
        timeline.getChildren().add(day);
        HBox legend = new HBox(12);
        legend.setPadding(new Insets(0, 16, 8, 16));
        legend.getChildren().addAll(
                DoctorScheduleParts.legendDot("#22C55E", "Available"),
                DoctorScheduleParts.legendDot("#6366F1", "Appointment"),
                DoctorScheduleParts.legendDot("#EF4444", "Conflict / Unavailable"));
        timeline.getChildren().add(legend);
        timeline.getChildren().add(new Separator());
        var appts = state.appointments.stream()
                .filter(a -> did.equals(a.getDoctorId()) && java.time.LocalDate.now().equals(a.getDate()))
                .sorted((a, b) -> a.getTime().compareTo(b.getTime())).toList();
        for (int hour = 8; hour <= 18; hour++) {
            for (int half = 0; half < 2; half++) {
                if (hour == 18 && half == 1) break;
                LocalTime t = LocalTime.of(hour, half * 30);
                timeline.getChildren().add(
                        DoctorScheduleParts.slotRow(doc, appts, hour, half * 30, t));
                Separator sep = new Separator();
                sep.setOpacity(0.4);
                timeline.getChildren().add(sep);
            }
        }
    }

    private static HBox docHeader(Doctor doc) {
        HBox docHead = new HBox(12);
        docHead.setPadding(new Insets(14, 16, 12, 16));
        docHead.setAlignment(Pos.CENTER_LEFT);
        Label name = new Label(doc.getName());
        name.setStyle("-fx-font-weight:700;-fx-font-size:13px;-fx-text-fill:#F1F5F9;");
        Label spec = new Label(doc.getSpecialization());
        spec.setStyle("-fx-text-fill:#94A3B8;-fx-font-size:11px;-fx-background-color:#312E81;-fx-padding:2 6;-fx-background-radius:4;");
        Region s = new Region();
        HBox.setHgrow(s, Priority.ALWAYS);
        Label avail = new Label(doc.getAvailable() ? "Available" : "On Leave");
        avail.getStyleClass().addAll("badge", doc.getAvailable() ? "badge-success" : "badge-neutral");
        docHead.getChildren().addAll(name, spec, s, avail);
        return docHead;
    }

    private static VBox empty(String a, String b) {
        VBox box = new VBox(4);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(28));
        Label t = new Label(a);
        t.getStyleClass().add("empty-state-title");
        Label s = new Label(b);
        s.getStyleClass().add("empty-state-sub");
        box.getChildren().addAll(t, s);
        return box;
    }
}
