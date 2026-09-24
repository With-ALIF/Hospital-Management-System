package com.hospital.ui.views;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class LoginRequiredView {
    public static VBox build(String pageName, Runnable onLogin, Runnable onBack) {
        Label icon = new Label("🔒");
        icon.setStyle("-fx-font-size:40px;-fx-alignment:center;");
        Label title = new Label("Login required");
        title.setStyle("-fx-font-size:22px;-fx-font-weight:700;-fx-text-fill:#10263B;");
        String page = pageName == null || pageName.isBlank() ? "this page" : pageName;
        Label sub = new Label("Please sign in to access " + page
                + ". You will return here after a successful login.");
        sub.setStyle("-fx-font-size:13.5px;-fx-text-fill:#46647A;");
        sub.setWrapText(true);
        sub.setMaxWidth(400);
        sub.setAlignment(Pos.CENTER);

        Button login = new Button("Log in");
        login.setStyle("-fx-background-color:#0B5C75;-fx-text-fill:white;"
                + "-fx-font-size:14px;-fx-font-weight:700;-fx-padding:11 32;"
                + "-fx-background-radius:8;-fx-cursor:hand;");
        login.setOnAction(e -> onLogin.run());

        Button back = new Button("Back to public dashboard");
        back.setStyle("-fx-background-color:transparent;-fx-text-fill:#0B5C75;"
                + "-fx-font-size:13px;-fx-font-weight:600;-fx-padding:8 16;"
                + "-fx-cursor:hand;");
        back.setOnAction(e -> onBack.run());

        VBox card = new VBox(14, icon, title, sub, login, back);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(36, 32, 36, 32));
        card.setMaxWidth(460);
        card.getStyleClass().add("card");
        card.setStyle("-fx-background-color:#FFFFFF;-fx-border-color:#D7DEE4;"
                + "-fx-background-radius:12;-fx-border-radius:12;");

        VBox outer = new VBox(card);
        outer.setAlignment(Pos.CENTER);
        outer.setPadding(new Insets(50));
        outer.setStyle("-fx-background-color:#E9EEF2;");
        VBox root = new VBox(outer);
        root.setAlignment(Pos.CENTER);
        VBox.setVgrow(outer, javafx.scene.layout.Priority.ALWAYS);
        return root;
    }

    public static VBox build(Runnable onLogin, Runnable onBack) {
        return build(null, onLogin, onBack);
    }
}
