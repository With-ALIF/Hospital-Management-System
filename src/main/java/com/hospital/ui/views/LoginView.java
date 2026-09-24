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
    private static final String DARK_FIELD =
            "-fx-background-color:#0F2537;-fx-text-fill:#FFFFFF;"
            + "-fx-prompt-text-fill:#7A93A6;-fx-border-color:#2A4A5E;"
            + "-fx-border-radius:8;-fx-background-radius:8;"
            + "-fx-padding:9 12;-fx-font-size:13px;";
    private static final String DARK_FIELD_FOCUSED =
            "-fx-background-color:#0F2537;-fx-text-fill:#FFFFFF;"
            + "-fx-prompt-text-fill:#7A93A6;-fx-border-color:#0B5C75;"
            + "-fx-border-width:2;-fx-border-radius:8;-fx-background-radius:8;"
            + "-fx-padding:9 12;-fx-font-size:13px;";

    public static VBox build(AppState state, Runnable onSuccess) {
        TextField username = new TextField();
        username.setPromptText("Username");
        username.setStyle(DARK_FIELD);
        username.focusedProperty().addListener((o, oldV, newV) ->
                username.setStyle(newV ? DARK_FIELD_FOCUSED : DARK_FIELD));
        PasswordField password = new PasswordField();
        password.setPromptText("Password");
        password.setStyle(DARK_FIELD);
        password.focusedProperty().addListener((o, oldV, newV) ->
                password.setStyle(newV ? DARK_FIELD_FOCUSED : DARK_FIELD));
        TextField plain = new TextField();
        plain.setPromptText("Password");
        plain.setStyle(DARK_FIELD);
        plain.focusedProperty().addListener((o, oldV, newV) ->
                plain.setStyle(newV ? DARK_FIELD_FOCUSED : DARK_FIELD));
        plain.setVisible(false);
        plain.setManaged(false);
        plain.textProperty().bindBidirectional(password.textProperty());
        Label eye = new Label("Show");
        eye.setStyle("-fx-text-fill:#7FBFD0;-fx-font-size:12px;-fx-cursor:hand;"
                + "-fx-padding:0 8px;");
        eye.setOnMouseClicked(e -> togglePassword(password, plain, eye));
        StackPane passStack = new StackPane(password, plain);
        HBox passRow = new HBox(passStack, eye);
        passRow.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(passStack, Priority.ALWAYS);
        Label error = new Label();
        error.setStyle("-fx-text-fill:#F87171;-fx-font-size:12px;-fx-font-weight:600;");
        error.setWrapText(true);
        Button login = new Button("Log in");
        login.setStyle("-fx-background-color:#0B5C75;-fx-text-fill:white;"
                + "-fx-font-size:14px;-fx-font-weight:700;-fx-padding:11 32;"
                + "-fx-background-radius:8;-fx-cursor:hand;-fx-max-width:Infinity;");
        login.setMaxWidth(Double.MAX_VALUE);
        login.setOnMouseEntered(e -> login.setStyle(
                "-fx-background-color:#094E64;-fx-text-fill:white;"
                + "-fx-font-size:14px;-fx-font-weight:700;-fx-padding:11 32;"
                + "-fx-background-radius:8;-fx-cursor:hand;-fx-max-width:Infinity;"));
        login.setOnMouseExited(e -> login.setStyle(
                "-fx-background-color:#0B5C75;-fx-text-fill:white;"
                + "-fx-font-size:14px;-fx-font-weight:700;-fx-padding:11 32;"
                + "-fx-background-radius:8;-fx-cursor:hand;-fx-max-width:Infinity;"));
        Label forgot = new Label("Forgot password");
        forgot.setStyle("-fx-text-fill:#7FBFD0;-fx-font-size:12px;-fx-cursor:hand;");
        forgot.setOnMouseClicked(e -> error.setText(
                "Contact administrator to reset your password."));
        login.setOnAction(e -> {
            error.setText("");
            try {
                state.authenticationService.login(username.getText(), password.getText());
                onSuccess.run();
            } catch (RuntimeException ex) {
                error.setText(ex.getMessage());
                if (!plain.isVisible()) togglePassword(password, plain, eye);
            }
        });
        password.setOnAction(e -> login.fire());
        plain.setOnAction(e -> login.fire());
        Label title = new Label("Hospital management");
        title.setStyle("-fx-font-size:22px;-fx-font-weight:800;-fx-text-fill:#FFFFFF;");
        title.setAlignment(Pos.CENTER);
        Label subtitle = new Label("System");
        subtitle.setStyle("-fx-font-size:13px;-fx-text-fill:#7FBFD0;-fx-font-weight:600;");
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
        card.setStyle("-fx-background-color:#162E45;-fx-border-color:#2A4A5E;"
                + "-fx-background-radius:12;-fx-border-radius:12;");
        HBox outer = new HBox(card);
        outer.setAlignment(Pos.CENTER);
        outer.setPadding(new Insets(40));
        HBox.setHgrow(form, Priority.ALWAYS);
        VBox root = new VBox(outer);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color:#10263B;"
                + "-fx-font-family:\"IBM Plex Sans\",\"Segoe UI\",Arial,sans-serif;");
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
        l.setStyle("-fx-font-size:12px;-fx-text-fill:#CBD5E1;-fx-font-weight:600;");
        return l;
    }
}
