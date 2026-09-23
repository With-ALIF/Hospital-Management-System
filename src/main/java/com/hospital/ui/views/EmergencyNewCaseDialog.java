package com.hospital.ui.views;

import com.hospital.enums.EmergencyLevel;
import com.hospital.ui.AppState;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

final class EmergencyNewCaseDialog {
    private EmergencyNewCaseDialog() {
    }

    static void show(AppState state) {
        Dialog<Void> d = new Dialog<>();
        d.setTitle("New Emergency Case");
        d.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        ComboBox<String> patient = new ComboBox<>(FXCollections.observableArrayList(
                state.patients.stream().map(p -> p.getId() + " — " + p.getName()).toList()));
        patient.setPromptText("Select Patient");
        patient.setPrefWidth(300);
        ComboBox<EmergencyLevel> level = new ComboBox<>(FXCollections.observableArrayList(
                EmergencyLevel.LOW, EmergencyLevel.MODERATE,
                EmergencyLevel.SERIOUS, EmergencyLevel.CRITICAL));
        level.setValue(EmergencyLevel.MODERATE);
        level.setPrefWidth(300);
        TextField desc = new TextField();
        desc.setPromptText("Description e.g. chest pain");
        desc.setPrefWidth(300);
        VBox box = new VBox(6, new Label("Patient"), patient, new Label("Priority"), level,
                new Label("Description"), desc);
        box.setPadding(new Insets(16));
        d.getDialogPane().setContent(box);
        d.setResultConverter(b -> {
            if (b == ButtonType.OK) {
                try {
                    create(state, patient, level, desc);
                } catch (Exception ex) {
                    EmergencyDialogs.error(ex.getMessage());
                }
            }
            return null;
        });
        d.showAndWait();
    }

    private static void create(AppState state, ComboBox<String> patient,
                               ComboBox<EmergencyLevel> level, TextField desc) {
        String pid = patient.getValue() != null ? patient.getValue().split(" — ")[0] : null;
        if (pid == null) {
            throw new IllegalArgumentException("Select a patient");
        }
        if (desc.getText().isBlank()) {
            throw new IllegalArgumentException("Description is required");
        }
        var c = state.emergencyService.addEmergencyCase(pid, level.getValue(), desc.getText().trim());
        state.refreshEmergency();
        EmergencyDialogs.info("Case " + c.getId() + " added to the triage queue.");
    }
}
