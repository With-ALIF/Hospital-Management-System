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

import java.util.List;

public class BloodBankView {
    public static Node build(AppState state) {
        VBox root = new VBox(16);
        root.getStyleClass().add("main-content");
        root.setPadding(new Insets(24));
        Label title = new Label("Blood Bank");
        title.getStyleClass().add("page-title");
        Label sub = new Label("Donors, units, reservations and emergency requests");
        sub.getStyleClass().add("page-subtitle");

        long available = state.bloodBankService.getAllUnits().stream()
                .filter(u -> u.getStatus() == com.hospital.enums.BloodUnitStatus.AVAILABLE).count();
        long reserved = state.bloodBankService.getAllUnits().stream()
                .filter(u -> u.getStatus() == com.hospital.enums.BloodUnitStatus.RESERVED).count();
        long expired = state.bloodBankService.getAllUnits().stream()
                .filter(u -> u.getStatus() == com.hospital.enums.BloodUnitStatus.EXPIRED).count();
        long lowGroups = state.bloodBankService.lowStockGroups(3).size();

        HBox kpis = new HBox(16);
        kpis.getChildren().addAll(
                StatCard.create("Available Units", String.valueOf(available),
                        "Ready to issue", "kpi-sub-success"),
                StatCard.create("Reserved", String.valueOf(reserved), "Held", null),
                StatCard.create("Expired", String.valueOf(expired),
                        "Do not issue", expired > 0 ? "kpi-sub-danger" : null),
                StatCard.create("Low Stock Groups", String.valueOf(lowGroups),
                        "Below threshold", lowGroups > 0 ? "kpi-sub-warning" : "kpi-sub-success")
        );
        for (var n : kpis.getChildren()) HBox.setHgrow((Region) n, Priority.ALWAYS);

        HBox tools = new HBox(10);
        TextField search = new TextField();
        search.setPromptText("Search blood units by ID, donor, group...");
        search.getStyleClass().add("header-search");
        search.setPrefWidth(320);
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Button refresh = new Button("Refresh");
        refresh.getStyleClass().addAll("btn-secondary", "btn-small");
        refresh.setOnAction(e -> state.notifyChange());

        TableView<com.hospital.model.BloodUnit> table = unitTable();
        table.setItems(FXCollections.observableArrayList(
                state.bloodBankService.getAllUnits()));
        search.textProperty().addListener((o, a, b) ->
                table.setItems(FXCollections.observableArrayList(
                        state.bloodBankService.search(b))));
        tools.getChildren().addAll(search, spacer, refresh);

        root.getChildren().addAll(new VBox(2, title, sub), kpis, tools, table);
        VBox.setVgrow(table, Priority.ALWAYS);
        return root;
    }

    @SuppressWarnings("unchecked")
    private static TableView<com.hospital.model.BloodUnit> unitTable() {
        TableView<com.hospital.model.BloodUnit> table = new TableView<>();
        table.setPlaceholder(com.hospital.ui.components.EmptyState.create(
                "No blood units", "Register a donor and add a donation to get started"));
        col(table, "Unit ID", "id", 110);
        col(table, "Blood Group", "bloodGroup", 110);
        col(table, "Rh", "rhType", 70);
        col(table, "Collected", "collectionDate", 110);
        col(table, "Expires", "expiryDate", 110);
        col(table, "Donor", "donorId", 110);
        col(table, "Location", "storageLocation", 120);
        TableColumn<com.hospital.model.BloodUnit, String> status = new TableColumn<>("Status");
        status.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(
                d.getValue().getStatus() == null ? "" : d.getValue().getStatus().name()));
        status.setCellFactory(c -> new javafx.scene.control.TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    setGraphic(BadgeFactory.status(item));
                }
            }
        });
        status.setPrefWidth(110);
        table.getColumns().add(status);
        return table;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static void col(TableView table, String name, String prop, double w) {
        TableColumn c = new TableColumn(name);
        c.setCellValueFactory(new PropertyValueFactory(prop));
        c.setPrefWidth(w);
        table.getColumns().add(c);
    }
}
