package com.hospital.ui.views;

import com.hospital.model.Appointment;
import com.hospital.ui.AppState;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;

final class AppointmentsTable {
    private AppointmentsTable() {
    }

    static TableView<Appointment> buildTable(AppState state, TextField search,
            DatePicker dateFilter, ComboBox<String> doctorFilter, ComboBox<String> statusFilter) {
        TableView<Appointment> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        table.getColumns().addAll(
                col("Date", 94, a -> a.getDate() != null ? a.getDate().toString() : "—"),
                col("Time", 68, Appointment::getFormattedTime),
                col("Patient", 122, Appointment::getPatientName),
                col("Doctor", 122, Appointment::getDoctorName),
                col("Reason", 150, a -> a.getReason() != null ? a.getReason() : "—"),
                AppointmentsColumns.statusCol(), AppointmentsColumns.actionCol());
        var filtered = new javafx.collections.transformation.FilteredList<>(state.appointments, p -> true);
        Runnable refilter = () -> filtered.setPredicate(
                a -> matches(a, search, dateFilter, doctorFilter, statusFilter));
        search.textProperty().addListener((o, ov, nv) -> refilter.run());
        dateFilter.valueProperty().addListener((o, ov, nv) -> refilter.run());
        doctorFilter.valueProperty().addListener((o, ov, nv) -> refilter.run());
        statusFilter.valueProperty().addListener((o, ov, nv) -> refilter.run());
        table.setItems(filtered);
        table.setPlaceholder(new Label("No appointments found."));
        return table;
    }

    static boolean matches(Appointment a, TextField search, DatePicker dateFilter,
                           ComboBox<String> doctorFilter, ComboBox<String> statusFilter) {
        String q = search.getText();
        if (q != null && !q.isBlank() && !a.getPatientName().toLowerCase().contains(q.toLowerCase())
                && !a.getDoctorName().toLowerCase().contains(q.toLowerCase())
                && !a.getReason().toLowerCase().contains(q.toLowerCase())) return false;
        if (dateFilter.getValue() != null && !dateFilter.getValue().equals(a.getDate())) return false;
        String df = doctorFilter.getValue();
        if (df != null && !a.getDoctorId().equals(df.split(" — ")[0])) return false;
        String st = statusFilter.getValue();
        return st == null || "ALL".equals(st)
                || st.equalsIgnoreCase(a.getStatus() != null ? a.getStatus().name() : "");
    }

    static HBox buildQuickActions(TableView<Appointment> table, AppState state) {
        HBox quick = new HBox(8);
        quick.setAlignment(Pos.CENTER_LEFT);
        Button resched = new Button("Reschedule");
        resched.getStyleClass().addAll("btn-secondary", "btn-small");
        resched.setOnAction(e -> AppointmentsStatusDialogs.reschedule(table, state));
        Button confirm = new Button("Confirm");
        confirm.getStyleClass().addAll("btn-secondary", "btn-small");
        Button complete = new Button("Complete");
        complete.getStyleClass().addAll("btn-secondary", "btn-small");
        Button cancel = new Button("Cancel");
        cancel.getStyleClass().addAll("btn-danger", "btn-small");
        Label hint = new Label("Select a row to change status, or reschedule via View.");
        hint.setStyle("-fx-text-fill:#64748B;-fx-font-size:11px;");
        confirm.setOnAction(e -> AppointmentsStatusDialogs.changeStatus(table, state, "CONFIRMED"));
        complete.setOnAction(e -> AppointmentsStatusDialogs.changeStatus(table, state, "COMPLETED"));
        cancel.setOnAction(e -> AppointmentsStatusDialogs.changeStatus(table, state, "CANCELLED"));
        quick.getChildren().addAll(resched, confirm, complete, cancel,
                new javafx.scene.layout.Region(), hint);
        HBox.setHgrow(quick.getChildren().get(5), javafx.scene.layout.Priority.ALWAYS);
        return quick;
    }

    static TableColumn<Appointment, String> col(String n, double w,
            java.util.function.Function<Appointment, String> fn) {
        TableColumn<Appointment, String> c = new TableColumn<>(n);
        c.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(
                fn.apply(d.getValue()) != null ? fn.apply(d.getValue()) : "—"));
        c.setPrefWidth(w);
        return c;
    }
}
