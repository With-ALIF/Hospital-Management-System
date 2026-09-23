package com.hospital.ui.views;

import com.hospital.model.EmergencyCase;
import com.hospital.ui.AppState;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.TextInputDialog;

import java.util.function.Consumer;

final class EmergencyDialogs {
    private EmergencyDialogs() {
    }

    static String patientName(AppState state, EmergencyCase ec) {
        return state.patients.stream()
                .filter(p -> p.getId().equals(ec.getPatientId()))
                .map(p -> p.getName())
                .findFirst()
                .orElse(ec.getPatientId());
    }

    static void promptId(String title, String header, Consumer<String> fn) {
        TextInputDialog dlg = new TextInputDialog();
        dlg.setTitle(title);
        dlg.setHeaderText(header);
        var r = dlg.showAndWait();
        r.ifPresent(v -> {
            if (!v.isBlank()) fn.accept(v.trim());
        });
    }

    static void info(String m) {
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
