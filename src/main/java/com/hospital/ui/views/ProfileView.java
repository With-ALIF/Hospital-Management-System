package com.hospital.ui.views;

import com.hospital.model.UserAccount;
import com.hospital.ui.AppState;
import com.hospital.ui.components.StatCard;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class ProfileView {
    public static VBox build(AppState state) {
        UserAccount user = state.authenticationService.getCurrent();
        VBox root = new VBox(20);
        root.getStyleClass().add("main-content");
        root.setPadding(new Insets(24));
        Label title = new Label("My Profile");
        title.getStyleClass().add("page-title");
        Label sub = new Label("Account details and security");
        sub.getStyleClass().add("page-subtitle");
        root.getChildren().add(new VBox(2, title, sub));
        if (user == null) {
            root.getChildren().add(new Label("Not logged in."));
            return root;
        }
        HBox cards = new HBox(16,
                StatCard.create("Name", user.getDisplayName(), user.getUsername(), null),
                StatCard.create("Role", String.valueOf(user.getRole()),
                        String.valueOf(user.getStatus()), null),
                StatCard.create("Email", nullToDash(user.getEmail()),
                        nullToDash(user.getPhone()), null),
                StatCard.create("Last Login",
                        user.getLastLogin() == null ? "-" : user.getLastLogin().toString(),
                        user.getId(), null));
        for (var n : cards.getChildren()) HBox.setHgrow((Region) n, Priority.ALWAYS);
        root.getChildren().addAll(cards, detailsCard(state, user), passwordCard(state));
        return root;
    }

    private static HBox detailsCard(AppState state, UserAccount user) {
        TextField email = new TextField(user.getEmail() == null ? "" : user.getEmail());
        email.setPromptText("Email");
        TextField phone = new TextField(user.getPhone() == null ? "" : user.getPhone());
        phone.setPromptText("Phone");
        Label status = statusLabel();
        Button save = new Button("Save Profile");
        save.getStyleClass().addAll("btn-primary", "btn-small");
        save.setOnAction(e -> {
            try {
                state.authenticationService.changeOwnProfile(
                        email.getText().trim(), phone.getText().trim());
                setText(status, true, "Saved.");
            } catch (RuntimeException ex) {
                setText(status, false, ex.getMessage());
            }
        });
        HBox row = new HBox(12, email, phone, save, status);
        row.setAlignment(Pos.CENTER_LEFT);
        return wrap("Contact Info", row);
    }

    private static HBox passwordCard(AppState state) {
        PasswordField current = new PasswordField();
        current.setPromptText("Current Password");
        PasswordField next = new PasswordField();
        next.setPromptText("New Password");
        PasswordField confirm = new PasswordField();
        confirm.setPromptText("Confirm New Password");
        Label status = statusLabel();
        Button change = new Button("Change Password");
        change.getStyleClass().addAll("btn-secondary", "btn-small");
        change.setOnAction(e -> {
            try {
                if (!next.getText().equals(confirm.getText())) {
                    throw new IllegalStateException("Passwords do not match.");
                }
                state.authenticationService.changeOwnPassword(current.getText(), next.getText());
                current.clear();
                next.clear();
                confirm.clear();
                setText(status, true, "Password changed.");
            } catch (RuntimeException ex) {
                setText(status, false, ex.getMessage());
            }
        });
        return wrap("Change Password", new VBox(8, current, next, confirm, change, status));
    }

    static HBox wrap(String title, Node body) {
        Label h = new Label(title);
        h.getStyleClass().add("section-title");
        h.setPadding(new Insets(14, 16, 0, 16));
        VBox inner = new VBox(body);
        inner.setPadding(new Insets(8, 16, 16, 16));
        VBox c = new VBox(4, h, inner);
        c.getStyleClass().add("card");
        return new HBox(c);
    }

    static Label statusLabel() {
        Label l = new Label();
        l.setWrapText(true);
        return l;
    }

    static void setText(Label l, boolean ok, String msg) {
        l.setStyle(ok ? "-fx-text-fill:#4ADE80;-fx-font-size:12px;"
                : "-fx-text-fill:#F87171;-fx-font-size:12px;");
        l.setText(msg == null ? "Error" : msg);
    }

    static String nullToDash(String v) { return v == null || v.isBlank() ? "-" : v; }
}
