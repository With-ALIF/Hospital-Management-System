package com.hospital.ui;

import com.hospital.model.*;    
import java.util.List;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class AppointmentTable {
    public static VBox build(AppState state) {
        VBox tableCard = new VBox(14);
        HBox.setHgrow(tableCard, Priority.ALWAYS);
        tableCard.getStyleClass().add("card");
        HBox topBar = new HBox(12);
        topBar.setAlignment(Pos.CENTER_LEFT);
        Label title = new Label("Appointments Directory");
        title.getStyleClass().add("section-title");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        TextField search = UIHelper.createStyledTextField("Search patient, doctor, reason...");
        search.setPrefWidth(240);
        topBar.getChildren().addAll(title, spacer, search);
        TableView<Appointment> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        VBox.setVgrow(table, Priority.ALWAYS);
        TableColumn<Appointment, String> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getId()));
        TableColumn<Appointment, String> patientCol = new TableColumn<>("Patient");
        patientCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getPatient() != null ? c.getValue().getPatient().getName() + " (" + c.getValue().getPatient().getId() + ")" : ""));
        TableColumn<Appointment, String> docCol = new TableColumn<>("Doctor");
        docCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDoctor() != null ? c.getValue().getDoctor().getName() + " (" + c.getValue().getDoctor().getSpecialization() + ")" : ""));
        TableColumn<Appointment, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getFormattedDate()));
        TableColumn<Appointment, String> timeCol = new TableColumn<>("Time");
        timeCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getFormattedTime()));
        TableColumn<Appointment, String> reasonCol = new TableColumn<>("Reason");
        reasonCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getReason()));
        TableColumn<Appointment, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getStatusName()));
        statusCol.setCellFactory(col -> new TableCell<Appointment, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); setGraphic(null); }
                else {
                    Label badge = new Label(item);
                    badge.setStyle(StatusBadgeHelper.getStatusBadgeStyle(item));
                    setGraphic(badge); setText(null);
                }
            }
        });
        table.getColumns().addAll(List.of(idCol, patientCol, docCol, dateCol, timeCol, reasonCol, statusCol));
        FilteredList<Appointment> filtered = new FilteredList<>(state.appointments, p -> true);
        search.textProperty().addListener((obs, o, n) -> filtered.setPredicate(apt -> {
            if (n == null || n.isBlank()) return true;
            String l = n.toLowerCase();
            return apt.getId().toLowerCase().contains(l) || apt.getPatientName().toLowerCase().contains(l) || apt.getDoctorName().toLowerCase().contains(l) || (apt.getReason() != null && apt.getReason().toLowerCase().contains(l)) || apt.getStatus().name().toLowerCase().contains(l);
        }));
        table.setItems(filtered);
        HBox actionsRow = new HBox(12);
        actionsRow.setAlignment(Pos.CENTER_RIGHT);
        Label updateStatusLbl = new Label("Update Status:");
        updateStatusLbl.getStyleClass().add("section-title");
        ComboBox<AppointmentStatus> changeStatusBox = new ComboBox<>(FXCollections.observableArrayList(AppointmentStatus.values()));
        changeStatusBox.setValue(AppointmentStatus.CONFIRMED);
        changeStatusBox.getStyleClass().add("combo-box");
        Button updateStatusBtn = new Button("Update Status");
        updateStatusBtn.getStyleClass().add("btn-accent");
        updateStatusBtn.setOnAction(e -> {
            Appointment sel = table.getSelectionModel().getSelectedItem();
            if (sel == null) {
                UIHelper.showAlert(Alert.AlertType.WARNING, "No Selection", "Please select an appointment from the table first.");
                return;
            }
            AppointmentStatus st = changeStatusBox.getValue();
            state.service.updateAppointmentStatus(sel.getId(), st);
            table.refresh();
            state.notifyChange();
            UIHelper.showAlert(Alert.AlertType.INFORMATION, "Status Updated", "Appointment " + sel.getId() + " marked as " + st.name() + ".");
        });
        actionsRow.getChildren().addAll(updateStatusLbl, changeStatusBox, updateStatusBtn);
        tableCard.getChildren().addAll(topBar, table, actionsRow);
        return tableCard;
    }
}
