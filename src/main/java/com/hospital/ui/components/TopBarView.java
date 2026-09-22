package com.hospital.ui.components;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class TopBarView {
    private final HBox root;
    private final TextField search;
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
        Label date = new Label(LocalDate.now().format(DateTimeFormatter.ofPattern("EEE, MMM d, yyyy")));
        date.setStyle("-fx-text-fill:#94A3B8;-fx-font-size:12px;");
        Label bell = new Label("◷");
        bell.getStyleClass().add("header-icon");
        bell.setStyle("-fx-font-size:16px;-fx-text-fill:#A5B4FC;-fx-padding:6 8; -fx-background-color:#312E81; -fx-background-radius:8;");
        Region div = new Region();
        div.setStyle("-fx-background-color:#312E81;-fx-min-width:1;-fx-max-width:1;-fx-min-height:24;");
        VBox admin = new VBox(1);
        Label name = new Label("Admin");
        name.setStyle("-fx-font-size:12.5px;-fx-font-weight:600;-fx-text-fill:#F1F5F9;");
        Label role = new Label("Administrator");
        role.setStyle("-fx-font-size:11px;-fx-text-fill:#94A3B8;");
        admin.getChildren().addAll(name, role);
        Label avatar = new Label("A");
        avatar.getStyleClass().add("header-avatar");
        HBox adminBox = new HBox(10, admin, avatar);
        adminBox.setAlignment(Pos.CENTER_LEFT);
        root.getChildren().addAll(search, spacer, date, bell, div, adminBox);
    }
    public HBox getRoot() { return root; }
    public String getSearchText() { return search.getText(); }
}
