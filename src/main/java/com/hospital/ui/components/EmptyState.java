package com.hospital.ui.components;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.geometry.Insets;

public class EmptyState {
    public static VBox create(String title, String subtitle) {
        VBox box = new VBox(6);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(32, 16, 32, 16));
        Label t = new Label(title);
        t.getStyleClass().add("empty-state-title");
        Label s = new Label(subtitle);
        s.getStyleClass().add("empty-state-sub");
        s.setWrapText(true);
        box.getChildren().addAll(t, s);
        return box;
    }
}
