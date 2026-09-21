package com.hospital.ui;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.layout.HBox;

public class DoctorView {
    public static Node build(AppState state) {
        HBox layout = new HBox(20);
        layout.setPadding(new Insets(20));
        layout.getChildren().addAll(DoctorForm.build(state), DoctorTable.build(state));
        return layout;
    }
}
