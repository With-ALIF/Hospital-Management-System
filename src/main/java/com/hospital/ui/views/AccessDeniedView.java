package com.hospital.ui.views;

import com.hospital.exception.UnauthorizedAccessException;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class AccessDeniedView {
    public static VBox build(String message) {
        Label icon = new Label("!");
        icon.setStyle("-fx-font-size:40px;-fx-font-weight:700;-fx-text-fill:#F87171;"
                + "-fx-background-color:#1E293B;-fx-background-radius:50%;"
                + "-fx-padding:18 22;-fx-alignment:center;");
        Label title = new Label("Access Denied");
        title.setStyle("-fx-font-size:20px;-fx-font-weight:700;-fx-text-fill:#F1F5F9;");
        Label sub = new Label(message == null || message.isBlank()
                ? "You don't have permission to perform this action."
                : message);
        sub.setStyle("-fx-font-size:13px;-fx-text-fill:#94A3B8;");
        sub.setWrapText(true);
        sub.setMaxWidth(420);
        sub.setAlignment(Pos.CENTER);
        VBox box = new VBox(14, icon, title, sub);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(60, 24, 24, 24));
        VBox root = new VBox(box);
        root.setAlignment(Pos.CENTER);
        root.getStyleClass().add("main-content");
        return root;
    }

    public static VBox forException(RuntimeException ex) {
        if (ex instanceof UnauthorizedAccessException) {
            return build(ex.getMessage());
        }
        return build(null);
    }
}
