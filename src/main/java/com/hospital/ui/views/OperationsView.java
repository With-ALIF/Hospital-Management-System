package com.hospital.ui.views;

import com.hospital.enums.Permission;
import com.hospital.ui.AppState;
import com.hospital.ui.components.BadgeFactory;
import com.hospital.ui.components.StatCard;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
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

        boolean canManage = state.permissionService.hasPermission(Permission.MANAGE_OPERATION);

        HBox kpis = new HBox(16);
        VBox roomHolder = new VBox();
        TableView<com.hospital.model.Operation> table = table();
        TextField search = new TextField();
        search.setPromptText("Search operations by ID, patient, surgeon, room...");
        search.getStyleClass().add("header-search");
        search.setPrefWidth(340);

        Runnable refresh = () -> {
            refreshKpis(state, kpis);
            roomHolder.getChildren().setAll(roomStrip(state));
            String q = search.getText();
            table.setItems(FXCollections.observableArrayList(
                    q == null || q.isBlank()
                            ? state.operationService.getAllOperations()
                            : state.operationService.search(q)));
        };
        refresh.run();

        HBox tools = new HBox(10);
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        tools.getChildren().add(search);
        tools.getChildren().add(spacer);
        if (canManage) {
            Button schedule = new Button("+ Schedule Operation");
            schedule.getStyleClass().addAll("btn-primary", "btn-small");
            schedule.setOnAction(e -> OperationDialog.show(state, refresh::run));
            tools.getChildren().add(schedule);
        }
        Button refreshBtn = new Button("Refresh");
        refreshBtn.getStyleClass().addAll("btn-secondary", "btn-small");
        refreshBtn.setOnAction(e -> {
            state.notifyChange();
            refresh.run();
        });
        tools.getChildren().add(refreshBtn);

        search.textProperty().addListener((o, a, b) ->
                table.setItems(FXCollections.observableArrayList(
                        state.operationService.search(b))));

        root.getChildren().addAll(new VBox(2, title, sub), kpis, tools, roomHolder, table);
        if (canManage) {
            root.getChildren().add(statusBar(state, table, refresh));
        } else {
            Label note = new Label("View only — only admin and doctors can manage operations.");
            note.setStyle("-fx-font-size:12px;-fx-text-fill:#94A3B8;-fx-font-style:italic;");
            root.getChildren().add(note);
        }
        VBox.setVgrow(table, Priority.ALWAYS);
        return root;
    }

    private static HBox statusBar(AppState state,
                                  TableView<com.hospital.model.Operation> table,
                                  Runnable refresh) {
        Button start = new Button("Start");
        start.getStyleClass().addAll("btn-primary", "btn-small");
        Button complete = new Button("Complete");
        complete.getStyleClass().addAll("btn-secondary", "btn-small");
        Button cancel = new Button("Cancel");
        cancel.getStyleClass().addAll("btn-danger", "btn-small");
        start.setOnAction(e -> transition(state, table, refresh, "start"));
        complete.setOnAction(e -> transition(state, table, refresh, "complete"));
        cancel.setOnAction(e -> transition(state, table, refresh, "cancel"));
        Label hint = new Label("Select a row, or enter an operation ID.");
        hint.setStyle("-fx-font-size:11px;-fx-text-fill:#64748B;");
        Button byId = new Button("By ID…");
        byId.getStyleClass().addAll("btn-ghost", "btn-small");
        byId.setOnAction(e -> {
            TextInputDialog in = new TextInputDialog();
            in.setTitle("Operation status");
            in.setHeaderText("Enter operation ID (e.g. OPR-0001):");
            in.showAndWait().ifPresent(id -> {
                if (!id.isBlank()) transitionById(state, id.trim(), refresh, "complete");
            });
        });
        HBox bar = new HBox(8, start, complete, cancel, byId, hint);
        bar.setAlignment(Pos.CENTER_LEFT);
        return bar;
    }

    private static void transition(AppState state,
                                   TableView<com.hospital.model.Operation> table,
                                   Runnable refresh, String action) {
        var sel = table.getSelectionModel().getSelectedItem();
        if (sel == null) {
            error("Select an operation row first.");
            return;
        }
        transitionById(state, sel.getId(), refresh, action);
    }

    private static void transitionById(AppState state, String id, Runnable refresh, String action) {
        try {
            switch (action) {
                case "start" -> state.operationService.start(id);
                case "complete" -> state.operationService.complete(id);
                default -> state.operationService.cancel(id);
            }
            state.notifyChange();
            refresh.run();
            info("Operation " + id + " updated (" + action + ").");
        } catch (RuntimeException ex) {
            error(ex.getMessage());
        }
    }

    private static void refreshKpis(AppState state, HBox kpis) {
        int today = state.operationService.getToday().size();
        long freeRooms = state.operationService.getAvailableRooms().size();
        long active = state.operationService.getAllOperations().stream()
                .filter(o -> o.getStatus() != com.hospital.enums.OperationStatus.COMPLETED
                        && o.getStatus() != com.hospital.enums.OperationStatus.CANCELLED).count();
        long emergency = state.operationService.getAllOperations().stream()
                .filter(o -> o.getPriority() == com.hospital.enums.OperationPriority.EMERGENCY).count();
        kpis.getChildren().setAll(
                StatCard.create("Today's surgeries", String.valueOf(today), "Scheduled", null),
                StatCard.create("Free rooms", String.valueOf(freeRooms), "Available", "kpi-sub-success"),
                StatCard.create("Active ops", String.valueOf(active), "In pipeline",
                        active > 0 ? "kpi-sub-warning" : null),
                StatCard.create("Emergency ops", String.valueOf(emergency), "Priority",
                        emergency > 0 ? "kpi-sub-danger" : null));
        for (var n : kpis.getChildren()) HBox.setHgrow((Region) n, Priority.ALWAYS);
    }

    private static VBox roomStrip(AppState state) {
        HBox row = new HBox(10);
        row.setAlignment(Pos.CENTER_LEFT);
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

    private static void info(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle("Operations");
        a.setContentText(msg);
        a.showAndWait();
    }

    private static void error(String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle("Operations");
        a.setContentText(msg);
        a.showAndWait();
    }
}
