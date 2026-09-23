package com.hospital.ui.views;

import com.hospital.enums.StaffRole;
import com.hospital.model.UserAccount;
import com.hospital.ui.AppState;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import java.time.LocalDate;

public class DashboardView {
    public static Node build(AppState state, Runnable onViewAppointments,
                             Runnable onViewEmergency) {
        VBox root = new VBox(20);
        root.getStyleClass().add("main-content");
        root.setPadding(new Insets(24));
        UserAccount user = state.authenticationService.getCurrent();
        StaffRole role = user == null ? null : user.getRole();
        Label greet = new Label("Good Morning, "
                + (user == null ? "Guest" : user.getDisplayName()));
        greet.setStyle("-fx-font-size:12.5px;-fx-text-fill:#94A3B8;-fx-font-weight:500;");
        Label title = new Label(roleTitle(role));
        title.getStyleClass().add("page-title");
        Label sub = new Label("Real-time operational summary  •  " + LocalDate.now()
                + (role == null ? "" : "  •  " + role));
        sub.getStyleClass().add("page-subtitle");
        root.getChildren().addAll(new VBox(2, greet, title, sub),
                DashboardRoleKpis.build(state, role));
        HBox columns = new HBox(16);
        VBox emergencyPanel = DashboardEmergencyPanel.build(state, onViewEmergency);
        VBox apptPanel = DashboardAppointmentsPanel.build(state, onViewAppointments);
        HBox.setHgrow(emergencyPanel, Priority.ALWAYS);
        HBox.setHgrow(apptPanel, Priority.ALWAYS);
        emergencyPanel.setPrefWidth(420);
        apptPanel.setPrefWidth(420);
        columns.getChildren().addAll(emergencyPanel, apptPanel);
        root.getChildren().add(columns);
        ScrollPane sp = new ScrollPane(root);
        sp.setFitToWidth(true);
        sp.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        sp.setStyle("-fx-background-color:transparent;");
        return sp;
    }

    private static String roleTitle(StaffRole role) {
        if (role == null) return "Hospital Overview";
        return switch (role) {
            case DOCTOR -> "Doctor Dashboard";
            case NURSE -> "Nursing Dashboard";
            case RECEPTIONIST -> "Front Desk Dashboard";
            case PHARMACIST -> "Pharmacy Dashboard";
            case LAB_TECHNICIAN -> "Laboratory Dashboard";
            default -> "Hospital Overview";
        };
    }
}
