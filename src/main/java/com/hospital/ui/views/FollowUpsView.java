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
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class FollowUpsView {
    public static Node build(AppState state) {
        VBox root = new VBox(16);
        root.getStyleClass().add("main-content");
        root.setPadding(new Insets(24));
        Label title = new Label("Follow-Ups");
        title.getStyleClass().add("page-title");
        Label sub = new Label("Post-discharge and treatment follow-up tracking");
        sub.getStyleClass().add("page-subtitle");

        boolean canManage = state.permissionService.hasPermission(Permission.MANAGE_FOLLOW_UPS);

        HBox kpis = new HBox(16);
        TableView<com.hospital.model.FollowUp> table = table();
        TextField search = new TextField();
        search.setPromptText("Search follow-ups by ID, patient, doctor, reason...");
        search.getStyleClass().add("header-search");
        search.setPrefWidth(340);

        Runnable refresh = () -> {
            refreshKpis(state, kpis);
            String q = search.getText();
            table.setItems(FXCollections.observableArrayList(
                    q == null || q.isBlank()
                            ? state.followUpService.getAll()
                            : state.followUpService.search(q)));
        };
        refresh.run();

        HBox tools = new HBox(10);
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        tools.getChildren().addAll(search, spacer);
        if (canManage) {
            Button schedule = new Button("+ Schedule Follow-Up");
            schedule.getStyleClass().addAll("btn-primary", "btn-small");
            schedule.setOnAction(e -> FollowUpDialog.show(state, refresh::run));
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
                        state.followUpService.search(b))));

        root.getChildren().addAll(new VBox(2, title, sub), kpis, tools, table);
        if (canManage) {
            root.getChildren().add(statusBar(state, table, refresh));
        } else {
            Label note = new Label("View only — only admin and doctors can manage follow-ups.");
            note.setStyle("-fx-font-size:12px;-fx-text-fill:#94A3B8;-fx-font-style:italic;");
            root.getChildren().add(note);
        }
        VBox.setVgrow(table, Priority.ALWAYS);
        return root;
    }

    private static HBox statusBar(AppState state,
                                  TableView<com.hospital.model.FollowUp> table,
                                  Runnable refresh) {
        Button complete = new Button("Complete");
        complete.getStyleClass().addAll("btn-primary", "btn-small");
        Button missed = new Button("Mark missed");
        missed.getStyleClass().addAll("btn-secondary", "btn-small");
        Button cancel = new Button("Cancel");
        cancel.getStyleClass().addAll("btn-danger", "btn-small");
        complete.setOnAction(e -> transition(state, table, refresh, "complete"));
        missed.setOnAction(e -> transition(state, table, refresh, "missed"));
        cancel.setOnAction(e -> transition(state, table, refresh, "cancel"));
        Label hint = new Label("Select a row to update its status.");
        hint.setStyle("-fx-font-size:11px;-fx-text-fill:#64748B;");
        HBox bar = new HBox(8, complete, missed, cancel, hint);
        bar.setAlignment(Pos.CENTER_LEFT);
        return bar;
    }

    private static void transition(AppState state,
                                   TableView<com.hospital.model.FollowUp> table,
                                   Runnable refresh, String action) {
        var sel = table.getSelectionModel().getSelectedItem();
        if (sel == null) {
            error("Select a follow-up row first.");
            return;
        }
        try {
            switch (action) {
                case "complete" -> state.followUpService.complete(sel.getId());
                case "missed" -> state.followUpService.markMissed(sel.getId());
                default -> state.followUpService.cancel(sel.getId());
            }
            state.notifyChange();
            refresh.run();
            info("Follow-up " + sel.getId() + " updated (" + action + ").");
        } catch (RuntimeException ex) {
            error(ex.getMessage());
        }
    }

    private static void refreshKpis(AppState state, HBox kpis) {
        int upcoming = state.followUpService.getUpcoming().size();
        int today = state.followUpService.getToday().size();
        int total = state.followUpService.getCount();
        long missed = state.followUpService.getAll().stream()
                .filter(f -> f.getStatus() == com.hospital.enums.FollowUpStatus.MISSED).count();
        kpis.getChildren().setAll(
                StatCard.create("Upcoming", String.valueOf(upcoming),
                        "Scheduled future", null),
                StatCard.create("Today", String.valueOf(today),
                        "Due now", today > 0 ? "kpi-sub-warning" : "kpi-sub-success"),
                StatCard.create("Total", String.valueOf(total), "All time", null),
                StatCard.create("Missed", String.valueOf(missed),
                        "Needs action", missed > 0 ? "kpi-sub-danger" : "kpi-sub-success"));
        for (var n : kpis.getChildren()) HBox.setHgrow((Region) n, Priority.ALWAYS);
    }

    @SuppressWarnings("unchecked")
    private static TableView<com.hospital.model.FollowUp> table() {
        TableView<com.hospital.model.FollowUp> t = new TableView<>();
        t.setPlaceholder(com.hospital.ui.components.EmptyState.create(
                "No follow-ups", "Schedule a follow-up after discharge or visit"));
        col(t, "ID", "id", 100);
        col(t, "Patient", "patientId", 110);
        col(t, "Doctor", "doctorId", 110);
        col(t, "Date", "followUpDate", 120);
        col(t, "Reason", "reason", 200);
        col(t, "Instructions", "instructions", 220);
        TableColumn<com.hospital.model.FollowUp, String> st = new TableColumn<>("Status");
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
        a.setTitle("Follow-Ups");
        a.setContentText(msg);
        a.showAndWait();
    }

    private static void error(String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle("Follow-Ups");
        a.setContentText(msg);
        a.showAndWait();
    }
}
