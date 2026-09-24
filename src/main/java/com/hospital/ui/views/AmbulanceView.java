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

public class AmbulanceView {
    public static Node build(AppState state) {
        VBox root = new VBox(16);
        root.getStyleClass().add("main-content");
        root.setPadding(new Insets(24));
        Label title = new Label("Ambulance");
        title.getStyleClass().add("page-title");
        Label sub = new Label("Fleet, drivers, trips and emergency dispatch");
        sub.getStyleClass().add("page-subtitle");

        long available = state.ambulanceService.getAvailable().size();
        long onTrip = state.ambulanceService.getAllAmbulances().stream()
                .filter(a -> a.getStatus() == com.hospital.enums.AmbulanceStatus.ON_TRIP).count();
        long activeTrips = state.ambulanceService.getActiveTrips().size();
        long maintenance = state.ambulanceService.getAllAmbulances().stream()
                .filter(a -> a.getStatus() == com.hospital.enums.AmbulanceStatus.MAINTENANCE).count();

        HBox kpis = new HBox(16);
        kpis.getChildren().addAll(
                StatCard.create("Available", String.valueOf(available),
                        "Ready for dispatch", "kpi-sub-success"),
                StatCard.create("On Trip", String.valueOf(onTrip), "In service", null),
                StatCard.create("Active Trips", String.valueOf(activeTrips),
                        "Ongoing", activeTrips > 0 ? "kpi-sub-warning" : null),
                StatCard.create("Maintenance", String.valueOf(maintenance),
                        "Unavailable", maintenance > 0 ? "kpi-sub-warning" : "kpi-sub-success")
        );
        for (var n : kpis.getChildren()) HBox.setHgrow((Region) n, Priority.ALWAYS);

        HBox tools = new HBox(10);
        TextField search = new TextField();
        search.setPromptText("Search by vehicle, driver, status...");
        search.getStyleClass().add("header-search");
        search.setPrefWidth(320);
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Button refresh = new Button("Refresh");
        refresh.getStyleClass().addAll("btn-secondary", "btn-small");
        refresh.setOnAction(e -> state.notifyChange());

        TableView<com.hospital.model.Ambulance> table = table();
        table.setItems(FXCollections.observableArrayList(
                state.ambulanceService.getAllAmbulances()));
        search.textProperty().addListener((o, a, b) ->
                table.setItems(FXCollections.observableArrayList(
                        state.ambulanceService.search(b))));
        tools.getChildren().addAll(search, spacer, refresh);

        root.getChildren().addAll(new VBox(2, title, sub), kpis, tools, table);
        VBox.setVgrow(table, Priority.ALWAYS);
        return root;
    }

    @SuppressWarnings("unchecked")
    private static TableView<com.hospital.model.Ambulance> table() {
        TableView<com.hospital.model.Ambulance> t = new TableView<>();
        t.setPlaceholder(com.hospital.ui.components.EmptyState.create(
                "No ambulances", "Add an ambulance to start dispatch"));
        col(t, "ID", "id", 100);
        col(t, "Vehicle", "vehicleNumber", 120);
        col(t, "Driver", "driverName", 140);
        col(t, "Phone", "driverPhone", 130);
        col(t, "Type", "type", 100);
        col(t, "Location", "currentLocation", 120);
        TableColumn<com.hospital.model.Ambulance, String> st = new TableColumn<>("Status");
        st.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(
                d.getValue().getStatus() == null ? "" : d.getValue().getStatus().name()));
        st.setCellFactory(c -> new javafx.scene.control.TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty || item == null ? null : BadgeFactory.status(item));
            }
        });
        st.setPrefWidth(130);
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
