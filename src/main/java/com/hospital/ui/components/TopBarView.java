package com.hospital.ui.components;

import com.hospital.model.UserAccount;
import com.hospital.service.SessionManager;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class TopBarView {
    private final HBox root;
    private final TextField search;
    private final Label name;
    private final Label role;
    private final Label avatar;
    private Runnable onLogout;
    private Runnable onProfile;

    public TopBarView(Runnable onSearch) {
        root = new HBox(16);
        root.getStyleClass().add("top-header");
        root.setAlignment(Pos.CENTER_LEFT);
        root.setPrefHeight(56);
        root.setMinHeight(56);
        search = new TextField();
        search.setPromptText("Search patients, doctors, appointments...");
        search.getStyleClass().add("header-search");
        search.setPrefWidth(360);
        HBox.setHgrow(search, Priority.ALWAYS);
        if (onSearch != null) search.setOnAction(e -> onSearch.run());
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Label date = new Label(LocalDate.now()
                .format(DateTimeFormatter.ofPattern("EEE, MMM d, yyyy")));
        date.setStyle("-fx-text-fill:#94A3B8;-fx-font-size:12px;");
        Button logout = new Button("Logout");
        logout.getStyleClass().addAll("btn-secondary", "btn-small");
        logout.setOnAction(e -> {
            if (onLogout != null) onLogout.run();
        });
        Button profile = new Button("Profile");
        profile.getStyleClass().addAll("btn-ghost", "btn-small");
        profile.setOnAction(e -> {
            if (onProfile != null) onProfile.run();
        });
        Region div = new Region();
        div.setStyle("-fx-background-color:#312E81;-fx-min-width:1;"
                + "-fx-max-width:1;-fx-min-height:24;");
        VBox userBox = new VBox(1);
        name = new Label("—");
        name.setStyle("-fx-font-size:12.5px;-fx-font-weight:600;-fx-text-fill:#F1F5F9;");
        role = new Label("");
        role.setStyle("-fx-font-size:11px;-fx-text-fill:#94A3B8;");
        userBox.getChildren().addAll(name, role);
        avatar = new Label("?");
        avatar.getStyleClass().add("header-avatar");
        avatar.setOnMouseClicked(e -> {
            if (onProfile != null) onProfile.run();
        });
        HBox userArea = new HBox(10, userBox, avatar, profile, logout);
        userArea.setAlignment(Pos.CENTER_LEFT);
        refreshUser();
        root.getChildren().addAll(search, spacer, date, div, userArea);
    }

    public void refreshUser() {
        var user = SessionManager.getInstance().getCurrentUser();
        if (user == null) {
            name.setText("—");
            role.setText("");
            avatar.setText("?");
            return;
        }
        name.setText("Welcome, " + user.getDisplayName());
        role.setText("Role: " + user.getRole());
        String initial = user.getDisplayName() == null || user.getDisplayName().isBlank()
                ? "?" : user.getDisplayName().substring(0, 1).toUpperCase();
        avatar.setText(initial);
    }

    public void setOnLogout(Runnable onLogout) { this.onLogout = onLogout; }
    public void setOnProfile(Runnable onProfile) { this.onProfile = onProfile; }
    public HBox getRoot() { return root; }
    public String getSearchText() { return search.getText(); }
}
