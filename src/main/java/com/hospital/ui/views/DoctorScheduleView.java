package com.hospital.ui.views;

import com.hospital.enums.Permission;
import com.hospital.model.Appointment;
import com.hospital.model.Doctor;
import com.hospital.model.TimeSlot;
import com.hospital.ui.AppState;
import com.hospital.ui.SlotParser;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.time.LocalTime;
import java.util.ArrayList;

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
        root.getChildren().addAll(top, manageSection(state, docSel, refresh), sp);
        return root;
    }

    private static Node manageSection(AppState state, ComboBox<String> docSel, Runnable refreshTimeline) {
        boolean canManage = state.permissionService.hasPermission(Permission.MANAGE_SCHEDULE);
        if (!canManage) {
            Label note = new Label("View only — only admin can manage schedules.");
            note.setStyle("-fx-font-size:12px;-fx-text-fill:#94A3B8;-fx-font-style:italic;");
            return note;
        }

        Label heading = new Label("Manage schedule");
        heading.getStyleClass().add("section-title");
        Label adminBadge = new Label("Admin only");
        adminBadge.getStyleClass().addAll("badge", "badge-warning");

        CheckBox availableBox = new CheckBox("Available for duty");
        availableBox.setStyle("-fx-text-fill:#F1F5F9;-fx-font-size:13px;");
        ObservableList<TimeSlot> workingSlots = FXCollections.observableArrayList();
        VBox slotsBox = new VBox(6);
        TextField newSlot = new TextField();
        newSlot.setPromptText("HH:MM-HH:MM, e.g. 09:00-13:00");
        newSlot.setPrefWidth(200);
        Button addSlot = new Button("Add slot");
        addSlot.getStyleClass().addAll("btn-secondary", "btn-small");
        Label status = new Label();
        status.setWrapText(true);

        Runnable renderSlots = () -> {
            slotsBox.getChildren().clear();
            if (workingSlots.isEmpty()) {
                Label empty = new Label("No fixed slots — flexible / all hours.");
                empty.setStyle("-fx-font-size:12px;-fx-text-fill:#94A3B8;");
                slotsBox.getChildren().add(empty);
                return;
            }
            for (TimeSlot slot : workingSlots) {
                Label chip = new Label(slot.toString());
                chip.setStyle("-fx-font-size:12px;-fx-text-fill:#E2E8F0;"
                        + "-fx-background-color:#312E81;-fx-padding:4 10;-fx-background-radius:12;");
                Button remove = new Button("×");
                remove.getStyleClass().addAll("btn-ghost", "btn-small");
                remove.setOnAction(e -> workingSlots.remove(slot));
                HBox row = new HBox(8, chip, remove);
                row.setAlignment(Pos.CENTER_LEFT);
                slotsBox.getChildren().add(row);
            }
        };

        Runnable loadDoctor = () -> {
            Doctor doc = selectedDoctor(state, docSel.getValue());
            availableBox.setSelected(doc != null && doc.getAvailable());
            workingSlots.setAll(doc != null ? doc.getDutySlots() : java.util.List.of());
            renderSlots.run();
            status.setText("");
        };
        docSel.valueProperty().addListener((o, ov, nv) -> loadDoctor.run());
        loadDoctor.run();

        addSlot.setOnAction(e -> {
            if (newSlot.getText() == null || newSlot.getText().isBlank()) {
                setStatus(status, false, "Enter a slot like 09:00-13:00.");
                return;
            }
            try {
                String[] parts = newSlot.getText().trim().replace("→", "-").split("-");
                if (parts.length != 2) throw new IllegalArgumentException("Use HH:MM-HH:MM.");
                TimeSlot slot = new TimeSlot(
                        LocalTime.parse(parts[0].trim(), SlotParser.TIME_FORMATTER),
                        LocalTime.parse(parts[1].trim(), SlotParser.TIME_FORMATTER));
                if (workingSlots.contains(slot)) {
                    setStatus(status, false, "Slot already exists.");
                    return;
                }
                workingSlots.add(slot);
                newSlot.clear();
                renderSlots.run();
                status.setText("");
            } catch (Exception ex) {
                setStatus(status, false, "Invalid slot: " + ex.getMessage());
            }
        });

        Button save = new Button("Save schedule");
        save.getStyleClass().addAll("btn-primary", "btn-small");
        save.setOnAction(e -> {
            Doctor doc = selectedDoctor(state, docSel.getValue());
            if (doc == null) {
                setStatus(status, false, "Select a doctor first.");
                return;
            }
            try {
                state.doctorService.updateSchedule(
                        doc.getId(), availableBox.isSelected(), new ArrayList<>(workingSlots));
                state.refreshFromService();
                refreshTimeline.run();
                loadDoctor.run();
                setStatus(status, true, "Schedule saved for " + doc.getName() + ".");
            } catch (RuntimeException ex) {
                setStatus(status, false, ex.getMessage());
            }
        });

        HBox headRow = new HBox(8, heading, adminBadge);
        headRow.setAlignment(Pos.CENTER_LEFT);
        VBox card = new VBox(10, headRow, availableBox,
                dutySlotsLabel(), slotsBox,
                new HBox(8, newSlot, addSlot), new HBox(8, save, status));
        card.getStyleClass().add("card");
        card.setPadding(new Insets(14, 16, 14, 16));
        return card;
    }

    private static Label dutySlotsLabel() {
        Label l = new Label("Duty slots:");
        l.setStyle("-fx-font-size:12px;-fx-text-fill:#F1F5F9;-fx-font-weight:600;");
        return l;
    }

    private static Doctor selectedDoctor(AppState state, String sel) {
        if (sel == null || sel.isBlank()) return null;
        String did = sel.split(" — ")[0].trim();
        try {
            return state.doctorService.findDoctorById(did).orElse(null);
        } catch (Exception ignored) {
            return state.doctors.stream().filter(d -> d.getId().equals(did)).findFirst().orElse(null);
        }
    }

    private static void setStatus(Label status, boolean ok, String msg) {
        status.setStyle(ok ? "-fx-text-fill:#4ADE80;-fx-font-size:12px;"
                : "-fx-text-fill:#F87171;-fx-font-size:12px;");
        status.setText(msg);
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
