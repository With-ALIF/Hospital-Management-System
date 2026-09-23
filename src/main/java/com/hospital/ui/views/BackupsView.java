package com.hospital.ui.views;

import com.hospital.ui.AppState;
import com.hospital.ui.components.StatCard;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.util.List;

public class BackupsView {
    public static Node build(AppState state) {
        VBox root = new VBox(16);
        root.getStyleClass().add("main-content");
        root.setPadding(new Insets(24));
        Label title = new Label("Backups");
        title.getStyleClass().add("page-title");
        Label sub = new Label("Create, restore and manage JSON data snapshots");
        sub.getStyleClass().add("page-subtitle");

        List<String> backups = state.backupService.listBackups();
        HBox kpis = new HBox(16);
        kpis.getChildren().addAll(
                StatCard.create("Backups", String.valueOf(backups.size()),
                        "Stored snapshots", null),
                StatCard.create("Data Dir", state.backupService.getDataDir().toString(),
                        "Source", null),
                StatCard.create("Backup Dir", state.backupService.getBackupRoot().toString(),
                        "Target", null),
                StatCard.create("Status", "Ready", "Manual + auto", "kpi-sub-success")
        );
        for (var n : kpis.getChildren()) HBox.setHgrow((Region) n, Priority.ALWAYS);

        HBox tools = new HBox(10);
        Button create = new Button("Create Backup");
        create.getStyleClass().addAll("btn-primary", "btn-small");
        create.setOnAction(e -> {
            String name = state.backupService.createBackup();
            show(Alert.AlertType.INFORMATION, "Backup Created", name);
            state.notifyChange();
        });
        Button restore = new Button("Restore Selected");
        restore.getStyleClass().addAll("btn-secondary", "btn-small");
        Button delete = new Button("Delete Selected");
        delete.getStyleClass().addAll("btn-danger", "btn-small");

        ListView<String> list = new ListView<>();
        list.getItems().setAll(backups);
        list.setPrefHeight(360);
        list.getStyleClass().add("card");

        restore.setOnAction(e -> {
            String sel = list.getSelectionModel().getSelectedItem();
            if (sel == null) {
                show(Alert.AlertType.WARNING, "No Selection", "Select a backup first.");
                return;
            }
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                    "Restore backup '" + sel + "'? Existing data/*.json will be overwritten.",
                    ButtonType.OK, ButtonType.CANCEL);
            confirm.setHeaderText("Confirm Restore");
            if (confirm.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
                try {
                    state.backupService.confirmAndRestore(sel);
                    show(Alert.AlertType.INFORMATION, "Restore Complete", sel);
                    state.refreshFromService();
                } catch (Exception ex) {
                    show(Alert.AlertType.ERROR, "Restore Failed", ex.getMessage());
                }
            }
        });
        delete.setOnAction(e -> {
            String sel = list.getSelectionModel().getSelectedItem();
            if (sel == null) {
                show(Alert.AlertType.WARNING, "No Selection", "Select a backup first.");
                return;
            }
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                    "Delete backup '" + sel + "'?", ButtonType.OK, ButtonType.CANCEL);
            if (confirm.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
                state.backupService.deleteBackup(sel);
                list.getItems().setAll(state.backupService.listBackups());
            }
        });

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        tools.getChildren().addAll(create, restore, delete, spacer);

        root.getChildren().addAll(new VBox(2, title, sub), kpis, tools, list);
        VBox.setVgrow(list, Priority.ALWAYS);
        return root;
    }

    private static void show(Alert.AlertType type, String header, String content) {
        Alert a = new Alert(type, content, ButtonType.OK);
        a.setHeaderText(header);
        a.showAndWait();
    }
}
