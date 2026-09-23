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

public class FollowUpsView {
    public static Node build(AppState state) {
        VBox root = new VBox(16);
        root.getStyleClass().add("main-content");
        root.setPadding(new Insets(24));
        Label title = new Label("Follow-Ups");
        title.getStyleClass().add("page-title");
        Label sub = new Label("Post-discharge and treatment follow-up tracking");
        sub.getStyleClass().add("page-subtitle");

        int upcoming = state.followUpService.getUpcoming().size();
        int today = state.followUpService.getToday().size();
        int total = state.followUpService.getCount();
        long missed = state.followUpService.getAll().stream()
                .filter(f -> f.getStatus() == com.hospital.enums.FollowUpStatus.MISSED).count();

        HBox kpis = new HBox(16);
        kpis.getChildren().addAll(
                StatCard.create("Upcoming", String.valueOf(upcoming),
                        "Scheduled future", null),
                StatCard.create("Today", String.valueOf(today),
                        "Due now", today > 0 ? "kpi-sub-warning" : "kpi-sub-success"),
                StatCard.create("Total", String.valueOf(total), "All time", null),
                StatCard.create("Missed", String.valueOf(missed),
                        "Needs action", missed > 0 ? "kpi-sub-danger" : "kpi-sub-success")
        );
        for (var n : kpis.getChildren()) HBox.setHgrow((Region) n, Priority.ALWAYS);

        HBox tools = new HBox(10);
        TextField search = new TextField();
        search.setPromptText("Search follow-ups by ID, patient, doctor, reason...");
        search.getStyleClass().add("header-search");
        search.setPrefWidth(340);
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Button refresh = new Button("Refresh");
        refresh.getStyleClass().addAll("btn-secondary", "btn-small");
        refresh.setOnAction(e -> state.notifyChange());

        TableView<com.hospital.model.FollowUp> table = table();
        table.setItems(FXCollections.observableArrayList(state.followUpService.getAll()));
        search.textProperty().addListener((o, a, b) ->
                table.setItems(FXCollections.observableArrayList(
                        state.followUpService.search(b))));
        tools.getChildren().addAll(search, spacer, refresh);

        root.getChildren().addAll(new VBox(2, title, sub), kpis, tools, table);
        VBox.setVgrow(table, Priority.ALWAYS);
        return root;
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
