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

public class EquipmentView {
    public static Node build(AppState state) {
        VBox root = new VBox(16);
        root.getStyleClass().add("main-content");
        root.setPadding(new Insets(24));
        Label title = new Label("Equipment");
        title.getStyleClass().add("page-title");
        Label sub = new Label("Inventory, assignment and maintenance tracking");
        sub.getStyleClass().add("page-subtitle");

        var all = state.equipmentService.getAll();
        long available = all.stream()
                .filter(e -> e.getStatus() == com.hospital.enums.EquipmentStatus.AVAILABLE).count();
        long inUse = all.stream()
                .filter(e -> e.getStatus() == com.hospital.enums.EquipmentStatus.IN_USE).count();
        long maint = all.stream()
                .filter(e -> e.getStatus() == com.hospital.enums.EquipmentStatus.MAINTENANCE).count();
        long damaged = all.stream()
                .filter(e -> e.getStatus() == com.hospital.enums.EquipmentStatus.DAMAGED).count();

        HBox kpis = new HBox(16);
        kpis.getChildren().addAll(
                StatCard.create("Total Items", String.valueOf(all.size()), "Catalog", null),
                StatCard.create("Available", String.valueOf(available),
                        "Assignable", "kpi-sub-success"),
                StatCard.create("In Use", String.valueOf(inUse),
                        "Assigned", inUse > 0 ? "kpi-sub-warning" : null),
                StatCard.create("Maintenance / Damaged",
                        String.valueOf(maint + damaged),
                        maint + damaged > 0 ? "Needs attention" : "All good",
                        maint + damaged > 0 ? "kpi-sub-danger" : "kpi-sub-success")
        );
        for (var n : kpis.getChildren()) HBox.setHgrow((Region) n, Priority.ALWAYS);

        HBox tools = new HBox(10);
        TextField search = new TextField();
        search.setPromptText("Search equipment by ID, name, department...");
        search.getStyleClass().add("header-search");
        search.setPrefWidth(340);
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Button refresh = new Button("Refresh");
        refresh.getStyleClass().addAll("btn-secondary", "btn-small");
        refresh.setOnAction(e -> state.notifyChange());

        TableView<com.hospital.model.Equipment> table = table();
        table.setItems(FXCollections.observableArrayList(all));
        search.textProperty().addListener((o, a, b) ->
                table.setItems(FXCollections.observableArrayList(
                        state.equipmentService.search(b))));
        tools.getChildren().addAll(search, spacer, refresh);

        root.getChildren().addAll(new VBox(2, title, sub), kpis, tools, table);
        VBox.setVgrow(table, Priority.ALWAYS);
        return root;
    }

    @SuppressWarnings("unchecked")
    private static TableView<com.hospital.model.Equipment> table() {
        TableView<com.hospital.model.Equipment> t = new TableView<>();
        t.setPlaceholder(com.hospital.ui.components.EmptyState.create(
                "No equipment", "Add medical equipment to the catalog"));
        col(t, "ID", "id", 100);
        col(t, "Name", "name", 180);
        col(t, "Category", "category", 120);
        col(t, "Department", "department", 130);
        col(t, "Qty", "quantity", 70);
        col(t, "Available", "availableQuantity", 90);
        col(t, "Condition", "condition", 90);
        col(t, "Next Maint.", "nextMaintenance", 120);
        TableColumn<com.hospital.model.Equipment, String> st = new TableColumn<>("Status");
        st.setCellValueFactory(new PropertyValueFactory<>("status"));
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
