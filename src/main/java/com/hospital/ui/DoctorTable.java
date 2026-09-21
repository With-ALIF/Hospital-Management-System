package com.hospital.ui;

import com.hospital.model.Doctor;
import java.util.List;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class DoctorTable {
    public static VBox build(AppState state) {
        VBox tableCard = new VBox(14);
        HBox.setHgrow(tableCard, Priority.ALWAYS);
        tableCard.getStyleClass().add("card");
        HBox topBar = new HBox(12);
        topBar.setAlignment(Pos.CENTER_LEFT);
        Label tableTitle = new Label("Doctors Directory");
        tableTitle.getStyleClass().add("section-title");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        TextField search = UIHelper.createStyledTextField("Search by name or spec...");
        search.setPrefWidth(220);
        topBar.getChildren().addAll(tableTitle, spacer, search);
        TableView<Doctor> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        VBox.setVgrow(table, Priority.ALWAYS);
        TableColumn<Doctor, String> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getId()));
        TableColumn<Doctor, String> nameCol = new TableColumn<>("Doctor Name");
        nameCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getName()));
        TableColumn<Doctor, String> phoneCol = new TableColumn<>("Phone");
        phoneCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getPhone()));
        TableColumn<Doctor, String> specCol = new TableColumn<>("Specialization");
        specCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getSpecialization()));
        TableColumn<Doctor, String> schCol = new TableColumn<>("Duty Schedule");
        schCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDutyScheduleString()));
        TableColumn<Doctor, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getAvailable() ? "Available" : "Busy"));
        statusCol.setCellFactory(col -> new TableCell<Doctor, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); setGraphic(null); }
                else {
                    Label badge = new Label(item);
                    if ("Available".equalsIgnoreCase(item)) {
                        badge.getStyleClass().addAll("badge-pill", "badge-ok");
                    } else {
                        badge.getStyleClass().addAll("badge-pill", "badge-danger");
                    }
                    setGraphic(badge); setText(null);
                }
            }
        });
        table.getColumns().addAll(List.of(idCol, nameCol, phoneCol, specCol, schCol, statusCol));
        FilteredList<Doctor> filtered = new FilteredList<>(state.doctors, p -> true);
        search.textProperty().addListener((obs, oldVal, newVal) -> {
            filtered.setPredicate(d -> {
                if (newVal == null || newVal.isBlank()) return true;
                String lower = newVal.toLowerCase();
                return d.getName().toLowerCase().contains(lower) || d.getSpecialization().toLowerCase().contains(lower) || d.getId().toLowerCase().contains(lower);
            });
        });
        table.setItems(filtered);
        HBox actionsRow = new HBox(12);
        actionsRow.setAlignment(Pos.CENTER_RIGHT);
        Button toggleBtn = new Button("Toggle Availability");
        toggleBtn.getStyleClass().add("btn-accent");
        toggleBtn.setOnAction(e -> {
            Doctor selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) {
                UIHelper.showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a doctor from the table first.");
                return;
            }
            selected.setAvailable(!selected.getAvailable());
            table.refresh();
            state.notifyChange();
        });
        actionsRow.getChildren().add(toggleBtn);
        tableCard.getChildren().addAll(topBar, table, actionsRow);
        return tableCard;
    }
}
