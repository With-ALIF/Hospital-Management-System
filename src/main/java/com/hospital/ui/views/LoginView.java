package com.hospital.ui.views;

import com.hospital.ui.AppState;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class LoginView {
    public static VBox build(AppState state, Runnable onSuccess) {
        TextField username = new TextField();
        username.setPromptText("Username");
        username.getStyleClass().add("text-field");
        PasswordField password = new PasswordField();
        password.setPromptText("Password");
        password.getStyleClass().add("text-field");
        TextField plain = new TextField();
        plain.setPromptText("Password");
        plain.getStyleClass().add("text-field");
        plain.setVisible(false);
        plain.setManaged(false);
        plain.textProperty().bindBidirectional(password.textProperty());
        Label eye = new Label("Show");
        eye.setStyle("-fx-text-fill:#818CF8;-fx-font-size:12px;-fx-cursor:hand;"
                + "-fx-padding:0 8px;");
        eye.setOnMouseClicked(e -> togglePassword(password, plain, eye));
        StackPane passStack = new StackPane(password, plain);
        HBox passRow = new HBox(passStack, eye);
        passRow.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(passStack, Priority.ALWAYS);
        Label error = new Label();
        error.setStyle("-fx-text-fill:#F87171;-fx-font-size:12px;");
        error.setWrapText(true);
        Button login = new Button("Log in");
        login.getStyleClass().addAll("btn-primary");
        login.setMaxWidth(Double.MAX_VALUE);
        Label forgot = new Label("Forgot Password");
        forgot.setStyle("-fx-text-fill:#818CF8;-fx-font-size:12px;-fx-cursor:hand;");
        forgot.setOnMouseClicked(e -> error.setText(
                "Contact administrator to reset your password."));
        login.setOnAction(e -> {
            error.setText("");
            try {
                state.authenticationService.login(username.getText(), password.getText());
                onSuccess.run();
            } catch (RuntimeException ex) {
                error.setStyle("-fx-text-fill:#F87171;-fx-font-size:12px;");
                error.setText(ex.getMessage());
                if (!plain.isVisible()) togglePassword(password, plain, eye);
            }
        });
        password.setOnAction(e -> login.fire());
        plain.setOnAction(e -> login.fire());
        Label title = new Label("HOSPITAL MANAGEMENT");
        title.setStyle("-fx-font-size:22px;-fx-font-weight:700;-fx-text-fill:#F1F5F9;");
        title.setAlignment(Pos.CENTER);
        Label subtitle = new Label("SYSTEM");
        subtitle.setStyle("-fx-font-size:13px;-fx-text-fill:#818CF8;"
                + "-fx-letter-spacing:3px;-fx-font-weight:600;");
        subtitle.setAlignment(Pos.CENTER);
        VBox form = new VBox(6, fieldLabel("Username"), username,
                fieldLabel("Password"), passRow, error, login);
        form.setMaxWidth(340);
        HBox formRow = new HBox(form);
        formRow.setAlignment(Pos.CENTER);
        VBox card = new VBox(14, title, subtitle, new Region(), formRow, forgot);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(40, 32, 40, 32));
        card.setMaxWidth(420);
        card.getStyleClass().add("card");
        HBox outer = new HBox(card);
        outer.setAlignment(Pos.CENTER);
        outer.setPadding(new Insets(40));
        HBox.setHgrow(form, Priority.ALWAYS);
        VBox root = new VBox(outer);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color:#0F172A;");
        VBox.setVgrow(outer, Priority.ALWAYS);
        return root;
    }

    private static void togglePassword(PasswordField hidden, TextField shown, Label eye) {
        boolean show = hidden.isVisible();
        hidden.setVisible(!show);
        hidden.setManaged(!show);
        shown.setVisible(show);
        shown.setManaged(show);
        eye.setText(show ? "Hide" : "Show");
    }

    private static Label fieldLabel(String text) {
        Label l = new Label(text);
        l.setStyle("-fx-font-size:12px;-fx-text-fill:#94A3B8;-fx-font-weight:600;");
        return l;
    }
}
