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

public class StaffShiftsView {
    public static Node build(AppState state) {
        VBox root = new VBox(16);
        root.getStyleClass().add("main-content");
        root.setPadding(new Insets(24));
        Label title = new Label("Staff Shifts");
        title.getStyleClass().add("page-title");
        Label sub = new Label("Morning, evening and night rosters with overlap detection");
        sub.getStyleClass().add("page-subtitle");

        int today = state.staffShiftService.getToday().size();
        int total = state.staffShiftService.getCount();
        long active = state.staffShiftService.getAll().stream()
                .filter(s -> s.getStatus() == com.hospital.enums.ShiftStatus.ACTIVE
                        || s.getStatus() == com.hospital.enums.ShiftStatus.ASSIGNED).count();

        HBox kpis = new HBox(16);
        kpis.getChildren().addAll(
                StatCard.create("Today's Shifts", String.valueOf(today),
                        "Scheduled", null),
                StatCard.create("Total Shifts", String.valueOf(total), "All time", null),
                StatCard.create("Active / Assigned", String.valueOf(active),
                        "In roster", active > 0 ? "kpi-sub-success" : null),
                StatCard.create("Staff",
                        String.valueOf(state.staffService.getAllStaff().size()),
                        "Registered", null)
        );
        for (var n : kpis.getChildren()) HBox.setHgrow((Region) n, Priority.ALWAYS);

        HBox tools = new HBox(10);
        TextField search = new TextField();
        search.setPromptText("Search shifts by staff, department, type...");
        search.getStyleClass().add("header-search");
        search.setPrefWidth(340);
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Button refresh = new Button("Refresh");
        refresh.getStyleClass().addAll("btn-secondary", "btn-small");
        refresh.setOnAction(e -> state.notifyChange());

        TableView<com.hospital.model.StaffShift> table = table();
        table.setItems(FXCollections.observableArrayList(state.staffShiftService.getAll()));
        search.textProperty().addListener((o, a, b) ->
                table.setItems(FXCollections.observableArrayList(
                        state.staffShiftService.search(b))));
        tools.getChildren().addAll(search, spacer, refresh);

        root.getChildren().addAll(new VBox(2, title, sub), kpis, tools, table);
        VBox.setVgrow(table, Priority.ALWAYS);
        return root;
    }

    @SuppressWarnings("unchecked")
    private static TableView<com.hospital.model.StaffShift> table() {
        TableView<com.hospital.model.StaffShift> t = new TableView<>();
        t.setPlaceholder(com.hospital.ui.components.EmptyState.create(
                "No shifts", "Assign a shift to staff members"));
        col(t, "ID", "id", 100);
        col(t, "Staff", "staffId", 100);
        col(t, "Name", "staffName", 150);
        col(t, "Date", "date", 120);
        col(t, "Type", "shiftType", 100);
        col(t, "Start", "startTime", 80);
        col(t, "End", "endTime", 80);
        col(t, "Department", "department", 130);
        TableColumn<com.hospital.model.StaffShift, String> st = new TableColumn<>("Status");
        st.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(
                d.getValue().getStatus() == null ? "" : d.getValue().getStatus().name()));
        st.setCellFactory(c -> new javafx.scene.control.TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty || item == null ? null : BadgeFactory.status(item));
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
