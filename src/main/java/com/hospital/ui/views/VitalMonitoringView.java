package com.hospital.ui.views;

import com.hospital.ui.AppState;
import com.hospital.ui.components.BadgeFactory;
import com.hospital.ui.components.StatCard;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class VitalMonitoringView {
    public static Node build(AppState state) {
        VBox root = new VBox(16);
        root.getStyleClass().add("main-content");
        root.setPadding(new Insets(24));
        Label title = new Label("Vital Monitoring");
        title.getStyleClass().add("page-title");
        Label sub = new Label("Threshold-based vital tracking with warning and critical alerts");
        sub.getStyleClass().add("page-subtitle");

        int total = state.vitalMonitoringService.getCount();
        int critical = state.vitalMonitoringService.getCritical().size();
        int warning = state.vitalMonitoringService.getWarnings().size();

        HBox kpis = new HBox(16);
        kpis.getChildren().addAll(
                StatCard.create("Vital Records", String.valueOf(total),
                        "All time", null),
                StatCard.create("Critical Alerts", String.valueOf(critical),
                        "Immediate attention", critical > 0 ? "kpi-sub-danger" : "kpi-sub-success"),
                StatCard.create("Warnings", String.valueOf(warning),
                        "Monitor closely", warning > 0 ? "kpi-sub-warning" : "kpi-sub-success"),
                StatCard.create("Monitored Patients",
                        String.valueOf(state.vitalMonitoringService.getAll().stream()
                                .map(com.hospital.model.VitalRecord::getPatientId)
                                .filter(java.util.Objects::nonNull).distinct().count()),
                        "Unique", null)
        );
        for (var n : kpis.getChildren()) HBox.setHgrow((Region) n, Priority.ALWAYS);

        HBox tools = new HBox(10);
        TextField search = new TextField();
        search.setPromptText("Search vitals by ID, patient, recorder, status...");
        search.getStyleClass().add("header-search");
        search.setPrefWidth(340);
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Button refresh = new Button("Refresh");
        refresh.getStyleClass().addAll("btn-secondary", "btn-small");
        refresh.setOnAction(e -> state.notifyChange());

        TableView<com.hospital.model.VitalRecord> table = table();
        table.setItems(FXCollections.observableArrayList(
                state.vitalMonitoringService.getAll()));
        search.textProperty().addListener((o, a, b) ->
                table.setItems(FXCollections.observableArrayList(
                        state.vitalMonitoringService.search(b))));
        tools.getChildren().addAll(search, spacer, refresh);

        root.getChildren().addAll(new VBox(2, title, sub), kpis, tools, table);
        VBox.setVgrow(table, Priority.ALWAYS);
        return root;
    }

    @SuppressWarnings("unchecked")
    private static TableView<com.hospital.model.VitalRecord> table() {
        TableView<com.hospital.model.VitalRecord> t = new TableView<>();
        t.setPlaceholder(com.hospital.ui.components.EmptyState.create(
                "No vital records", "Record vitals for a patient to start monitoring"));
        col(t, "ID", "id", 100);
        col(t, "Patient", "patientId", 110);
        col(t, "Time", "recordedAt", 150);
        col(t, "Temp °C", "temperature", 80);
        col(t, "HR", "heartRate", 70);
        col(t, "BP", "bloodPressure", 90);
        col(t, "SpO2", "spo2", 70);
        col(t, "RR", "respiratoryRate", 70);
        col(t, "By", "recordedBy", 100);
        TableColumn<com.hospital.model.VitalRecord, String> st = new TableColumn<>("Status");
        st.setCellValueFactory(new PropertyValueFactory<>("overallStatus"));
        st.setCellFactory(c -> new javafx.scene.control.TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    Label l = new Label(item);
                    l.getStyleClass().addAll("badge",
                            "CRITICAL".equals(item) ? "badge-critical"
                                    : "WARNING".equals(item) ? "badge-warning" : "badge-success");
                    setGraphic(l);
                }
            }
        });
        st.setPrefWidth(120);
        t.getColumns().add(st);
        return t;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static void col(TableView table, String n, String p, double w) {
        TableColumn c = new TableColumn(n);
        c.setCellValueFactory(new PropertyValueFactory(p));
        c.setPrefWidth(w);
        table.getColumns().add(c);
    }
}
