package com.hospital.ui;

import javafx.scene.control.*;
import javafx.scene.layout.VBox;

public class UIHelper {
    public static VBox createFormField(String labelText, Control input) {
        VBox box = new VBox(4);
        Label label = new Label(labelText);
        label.getStyleClass().add("section-title");
        box.getChildren().addAll(label, input);
        return box;
    }

    public static TextField createStyledTextField(String placeholder) {
        TextField tf = new TextField();
        tf.setPromptText(placeholder);
        tf.getStyleClass().add("text-field");
        return tf;
    }

    public static void styleButton(Button button, String cssClass) {
        button.getStyleClass().add(cssClass);
    }

    public static Label createBadge(String text, String cssClass) {
        Label badge = new Label(text);
        badge.getStyleClass().addAll("badge-pill", cssClass);
        return badge;
    }

    public static void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
