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

public class OperationsView {
    public static Node build(AppState state) {
        VBox root = new VBox(16);
        root.getStyleClass().add("main-content");
        root.setPadding(new Insets(24));
        Label title = new Label("Operations");
        title.getStyleClass().add("page-title");
        Label sub = new Label("Operation rooms, surgery schedule and status flow");
        sub.getStyleClass().add("page-subtitle");
        HBox tools = new HBox(10);
        TextField search = new TextField();
        search.setPromptText("Search operations by ID, patient, surgeon, room...");
        search.getStyleClass().add("header-search");
        search.setPrefWidth(340);
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Button refresh = new Button("Refresh");
        refresh.getStyleClass().addAll("btn-secondary", "btn-small");
        refresh.setOnAction(e -> state.notifyChange());
        TableView<com.hospital.model.Operation> table = table();
        table.setItems(FXCollections.observableArrayList(state.operationService.getAllOperations()));
        search.textProperty().addListener((o, a, b) ->
                table.setItems(FXCollections.observableArrayList(state.operationService.search(b))));
        tools.getChildren().addAll(search, spacer, refresh);
        root.getChildren().addAll(new VBox(2, title, sub), kpis(state), tools, roomStrip(state), table);
        VBox.setVgrow(table, Priority.ALWAYS);
        return root;
    }

    private static HBox kpis(AppState state) {
        int today = state.operationService.getToday().size();
        long freeRooms = state.operationService.getAvailableRooms().size();
        long active = state.operationService.getAllOperations().stream()
                .filter(o -> o.getStatus() != com.hospital.enums.OperationStatus.COMPLETED
                        && o.getStatus() != com.hospital.enums.OperationStatus.CANCELLED).count();
        long emergency = state.operationService.getAllOperations().stream()
                .filter(o -> o.getPriority() == com.hospital.enums.OperationPriority.EMERGENCY).count();
        HBox box = new HBox(16);
        box.getChildren().addAll(
                StatCard.create("Today's Surgeries", String.valueOf(today), "Scheduled", null),
                StatCard.create("Free Rooms", String.valueOf(freeRooms), "Available", "kpi-sub-success"),
                StatCard.create("Active Ops", String.valueOf(active), "In pipeline",
                        active > 0 ? "kpi-sub-warning" : null),
                StatCard.create("Emergency Ops", String.valueOf(emergency), "Priority",
                        emergency > 0 ? "kpi-sub-danger" : null));
        for (var n : box.getChildren()) HBox.setHgrow((Region) n, Priority.ALWAYS);
        return box;
    }

    private static VBox roomStrip(AppState state) {
        HBox row = new HBox(10);
        Label lbl = new Label("Rooms");
        lbl.setStyle("-fx-font-size:11px;-fx-font-weight:700;-fx-text-fill:#818CF8;");
        row.getChildren().add(lbl);
        for (var r : state.operationService.getAllRooms()) {
            Label chip = new Label(r.getName() + " · " + r.getStatus());
            chip.getStyleClass().addAll("badge",
                    r.getStatus() == com.hospital.enums.RoomStatus.AVAILABLE
                            ? "badge-success" : "badge-warning");
            row.getChildren().add(chip);
        }
        VBox box = new VBox(row);
        box.setPadding(new Insets(0, 0, 4, 0));
        return box;
    }

    private static TableView<com.hospital.model.Operation> table() {
        TableView<com.hospital.model.Operation> t = new TableView<>();
        t.setPlaceholder(com.hospital.ui.components.EmptyState.create(
                "No operations", "Schedule a surgery to get started"));
        col(t, "ID", "id", 100);
        col(t, "Patient", "patientId", 110);
        col(t, "Surgeon", "surgeonId", 110);
        col(t, "Room", "roomId", 90);
        col(t, "Date", "date", 110);
        col(t, "Start", "startTime", 80);
        col(t, "End", "endTime", 80);
        col(t, "Type", "operationType", 140);
        col(t, "Priority", "priority", 100);
        TableColumn<com.hospital.model.Operation, String> st = new TableColumn<>("Status");
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
