package com.hospital.ui;

import com.hospital.model.Patient;
import java.util.List;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class PatientTable {
    public static VBox build(AppState state) {
        VBox tableCard = new VBox(14);
        HBox.setHgrow(tableCard, Priority.ALWAYS);
        tableCard.getStyleClass().add("card");
        HBox topBar = new HBox(12);
        topBar.setAlignment(Pos.CENTER_LEFT);
        Label tableTitle = new Label("Patients Directory");
        tableTitle.getStyleClass().add("section-title");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        TextField search = UIHelper.createStyledTextField("Search by name or blood group...");
        search.setPrefWidth(220);
        topBar.getChildren().addAll(tableTitle, spacer, search);
        TableView<Patient> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        VBox.setVgrow(table, Priority.ALWAYS);
        TableColumn<Patient, String> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getId()));
        TableColumn<Patient, String> nameCol = new TableColumn<>("Patient Name");
        nameCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getName()));
        TableColumn<Patient, String> phoneCol = new TableColumn<>("Phone");
        phoneCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getPhone()));
        TableColumn<Patient, String> genderCol = new TableColumn<>("Gender");
        genderCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getGender()));
        TableColumn<Patient, String> bloodCol = new TableColumn<>("Blood Group");
        bloodCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getBloodGroup()));
        TableColumn<Patient, String> emCol = new TableColumn<>("Emergency Contact");
        emCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getEmergencyContact()));
        table.getColumns().addAll(List.of(idCol, nameCol, phoneCol, genderCol, bloodCol, emCol));
        FilteredList<Patient> filtered = new FilteredList<>(state.patients, p -> true);
        search.textProperty().addListener((obs, oldVal, newVal) -> {
            filtered.setPredicate(p -> {
                if (newVal == null || newVal.isBlank()) return true;
                String lower = newVal.toLowerCase();
                return p.getName().toLowerCase().contains(lower) || p.getBloodGroup().toLowerCase().contains(lower) || p.getId().toLowerCase().contains(lower);
            });
        });
        table.setItems(filtered);
        tableCard.getChildren().addAll(topBar, table);
        return tableCard;
    }
}
