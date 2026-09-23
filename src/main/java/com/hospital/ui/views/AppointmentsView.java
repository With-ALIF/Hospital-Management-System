package com.hospital.ui.views;

import com.hospital.model.Appointment;
import com.hospital.ui.AppState;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class AppointmentsView {
    public static Pane build(AppState state) {
        VBox root = new VBox(16);
        root.getStyleClass().add("main-content");
        root.setPadding(new Insets(24));
        Label title = new Label("Appointments");
        title.getStyleClass().add("page-title");
        Label sub = new Label("Schedule and track patient consultations");
        sub.getStyleClass().add("page-subtitle");
        VBox headText = new VBox(2, title, sub);
        TextField search = new TextField();
        search.setPromptText("Search");
        search.setPrefWidth(170);
        search.getStyleClass().add("header-search");
        DatePicker dateFilter = new DatePicker();
        dateFilter.setPromptText("Date");
        dateFilter.setPrefWidth(138);
        ComboBox<String> doctorFilter = new ComboBox<>(FXCollections.observableArrayList(
                state.doctors.stream().map(d -> d.getId() + " — " + d.getName()).toList()));
        doctorFilter.setPromptText("Doctor");
        doctorFilter.setPrefWidth(160);
        ComboBox<String> statusFilter = new ComboBox<>(FXCollections.observableArrayList(
                "ALL", "SCHEDULED", "CONFIRMED", "COMPLETED", "CANCELLED"));
        statusFilter.setValue("ALL");
        statusFilter.setPrefWidth(132);
        Button newBtn = new Button("+ New Appointment");
        newBtn.getStyleClass().addAll("btn-primary");
        HBox filters = new HBox(8, search, dateFilter, doctorFilter, statusFilter);
        filters.setAlignment(Pos.CENTER_LEFT);
        HBox header = new HBox(12, headText, new Region(), filters, newBtn);
        header.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(header.getChildren().get(1), Priority.ALWAYS);
        TableView<Appointment> table =
                AppointmentsTable.buildTable(state, search, dateFilter, doctorFilter, statusFilter);
        HBox quick = AppointmentsTable.buildQuickActions(table, state);
        newBtn.setOnAction(e -> AppointmentsCreateDialog.show(state));
        VBox.setVgrow(table, Priority.ALWAYS);
        root.getChildren().addAll(header, table, quick);
        return root;
    }
}
