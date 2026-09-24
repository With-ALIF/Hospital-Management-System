package com.hospital.ui.views;

import com.hospital.enums.OperationPriority;
import com.hospital.model.Operation;
import com.hospital.ui.AppState;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

final class OperationDialog {
    private OperationDialog() {
    }

    static void show(AppState state, Runnable onSaved) {
        Dialog<Void> d = new Dialog<>();
        d.setTitle("Schedule Operation");
        d.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        ComboBox<String> patient = combo(patientOptions(state), "Select patient");
        ComboBox<String> surgeon = combo(doctorOptions(state), "Select surgeon");
        ComboBox<String> assistant = combo(doctorOptions(state), "Assistant (optional)");
        ComboBox<String> room = combo(roomOptions(state), "Select room");
        DatePicker date = new DatePicker(LocalDate.now().plusDays(1));
        ComboBox<String> start = combo(timeSlots(), "Start time");
        ComboBox<String> end = combo(timeSlots(), "End time");
        TextField type = new TextField();
        type.setPromptText("Type, e.g. Appendectomy");
        ComboBox<OperationPriority> priority = new ComboBox<>(FXCollections.observableArrayList(
                OperationPriority.NORMAL, OperationPriority.URGENT, OperationPriority.EMERGENCY));
        priority.setValue(OperationPriority.NORMAL);
        priority.setMaxWidth(Double.MAX_VALUE);

        VBox box = new VBox(6,
                new Label("Patient *"), patient,
                new Label("Surgeon *"), surgeon,
                new Label("Assistant doctor"), assistant,
                new Label("Room *"), room,
                new Label("Date *"), date,
                new Label("Start *"), start,
                new Label("End *"), end,
                new Label("Operation type *"), type,
                new Label("Priority"), priority);
        box.setPadding(new Insets(16));
        d.getDialogPane().setContent(box);
        d.setResultConverter(b -> {
            if (b == ButtonType.OK) {
                try {
                    Operation op = save(state, idOf(patient.getValue()), idOf(surgeon.getValue()),
                            idOfOrNull(assistant.getValue()), idOf(room.getValue()),
                            date.getValue(), start.getValue(), end.getValue(),
                            type.getText(), priority.getValue());
                    if (onSaved != null) onSaved.run();
                    info("Operation " + op.getId() + " scheduled for "
                            + op.getDate() + " in room " + op.getRoomId() + ".");
                } catch (Exception ex) {
                    error(ex.getMessage());
                }
            }
            return null;
        });
        d.showAndWait();
    }

    private static Operation save(AppState state, String patientId, String surgeonId,
                                  String assistantId, String roomId, LocalDate date,
                                  String startT, String endT, String type,
                                  OperationPriority priority) {
        if (patientId == null) throw new IllegalArgumentException("Please select a patient.");
        if (surgeonId == null) throw new IllegalArgumentException("Please select a surgeon.");
        if (roomId == null) throw new IllegalArgumentException("Please select a room.");
        if (date == null) throw new IllegalArgumentException("Please choose a date.");
        if (date.isBefore(LocalDate.now()))
            throw new IllegalArgumentException("Date cannot be in the past.");
        if (startT == null || startT.isBlank()) throw new IllegalArgumentException("Select start time.");
        if (endT == null || endT.isBlank()) throw new IllegalArgumentException("Select end time.");
        if (type == null || type.isBlank()) throw new IllegalArgumentException("Operation type is required.");
        LocalTime start = LocalTime.parse(startT.trim());
        LocalTime end = LocalTime.parse(endT.trim());
        if (!start.isBefore(end)) throw new IllegalArgumentException("Start must be before end.");
        return state.operationService.schedule(patientId, surgeonId, assistantId,
                roomId, date, start, end, type.trim(),
                priority != null ? priority : OperationPriority.NORMAL);
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

    private static List<String> roomOptions(AppState state) {
        try {
            return state.operationService.getAllRooms().stream()
                    .map(r -> r.getId() + " — " + r.getName()).sorted().toList();
        } catch (Exception ex) {
            return List.of();
        }
    }

    private static List<String> timeSlots() {
        return List.of("08:00", "08:30", "09:00", "09:30", "10:00", "10:30",
                "11:00", "11:30", "12:00", "12:30", "13:00", "13:30",
                "14:00", "14:30", "15:00", "15:30", "16:00", "16:30",
                "17:00", "17:30", "18:00");
    }

    private static String idOf(String option) {
        if (option == null || option.isBlank()) return null;
        return option.split(" — ")[0].trim();
    }

    private static String idOfOrNull(String option) {
        return idOf(option);
    }

    private static void info(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle("Operation scheduled");
        a.setContentText(msg);
        a.showAndWait();
    }

    private static void error(String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle("Cannot schedule operation");
        a.setContentText(msg);
        a.showAndWait();
    }
}
