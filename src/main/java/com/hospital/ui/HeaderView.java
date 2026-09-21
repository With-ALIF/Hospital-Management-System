package com.hospital.ui;

import com.hospital.model.Doctor;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.*;

public class HeaderView {
    private final AppState state;
    private final Label docBadge = UIHelper.createBadge("Doctors: 0", "badge-ok");
    private final Label availBadge = UIHelper.createBadge("Available: 0", "badge-ok");
    private final Label patBadge = UIHelper.createBadge("Patients: 0", "badge-accent");
    private final Label aptBadge = UIHelper.createBadge("Appointments: 0", "badge-warning");

    public HeaderView(AppState state) {
        this.state = state;
        updateMetrics();
    }

    public VBox build() {
        VBox container = new VBox();
        container.getStyleClass().add("top-bar");
        container.setPadding(new Insets(16, 24, 16, 24));
        HBox topRow = new HBox(16);
        topRow.setAlignment(Pos.CENTER_LEFT);
        Label icon = new Label("HMS");
        icon.getStyleClass().add("app-title");
        icon.setStyle("-fx-font-size: 24px;");
        VBox titleBox = new VBox(2);
        Label title = new Label("Hospital Management System");
        title.getStyleClass().add("app-title");
        Label sub = new Label("Appointments, Patient Care & Doctor Schedules");
        sub.getStyleClass().add("sub-title");
        titleBox.getChildren().addAll(title, sub);
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        HBox badges = new HBox(10);
        badges.setAlignment(Pos.CENTER_RIGHT);
        badges.getChildren().addAll(docBadge, availBadge, patBadge, aptBadge);
        topRow.getChildren().addAll(icon, titleBox, spacer, badges);
        container.getChildren().add(topRow);
        return container;
    }

    public void updateMetrics() {
        long totalDocs = state.doctors.size();
        long availDocs = state.doctors.stream().filter(Doctor::getAvailable).count();
        long totalPts = state.patients.size();
        long totalApts = state.appointments.size();
        docBadge.setText("Doctors: " + totalDocs);
        availBadge.setText("Available: " + availDocs);
        patBadge.setText("Patients: " + totalPts);
        aptBadge.setText("Appointments: " + totalApts);
    }
}
