package com.hospital.ui;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.layout.HBox;

public class PatientView {
    public static Node build(AppState state) {
        HBox layout = new HBox(20);
        layout.setPadding(new Insets(20));
        layout.getChildren().addAll(PatientForm.build(state), PatientTable.build(state));
        return layout;
    }
}
