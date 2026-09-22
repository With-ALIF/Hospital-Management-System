package com.hospital;

import com.hospital.ui.AppState;
import com.hospital.ui.components.SidebarView;
import com.hospital.ui.components.TopBarView;
import com.hospital.ui.views.*;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class App extends Application {
    private final AppState state = new AppState();
    private StackPane contentPane;
    private SidebarView sidebarView;

    @Override
    public void start(Stage primaryStage) {
        BorderPane root = new BorderPane();
        root.getStyleClass().add("root");

        String cssLight = getClass().getResource("/light-theme.css").toExternalForm();

        sidebarView = new SidebarView(this::navigate);
        TopBarView topBar = new TopBarView(null);

        contentPane = new StackPane();
        contentPane.getStyleClass().add("main-content");
        showDashboard();

        VBox center = new VBox();
        center.getChildren().addAll(topBar.getRoot(), contentPane);
        VBox.setVgrow(contentPane, Priority.ALWAYS);

        root.setLeft(sidebarView.getRoot());
        root.setCenter(center);

        Scene scene = new Scene(root, 1280, 800);
        scene.getStylesheets().add(cssLight);
        primaryStage.setTitle("Hospital Management System");
        primaryStage.getIcons().addAll(
            new Image(getClass().getResourceAsStream("/images/logo-icon-16.png")),
            new Image(getClass().getResourceAsStream("/images/logo-icon-32.png")),
            new Image(getClass().getResourceAsStream("/images/logo-icon-64.png")),
            new Image(getClass().getResourceAsStream("/images/logo-icon-128.png")),
            new Image(getClass().getResourceAsStream("/images/logo-icon.png"))
        );
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(1100);
        primaryStage.setMinHeight(700);
        primaryStage.show();
    }

    private void navigate(String name) {
        switch (name) {
            case "Dashboard" -> showDashboard();
            case "Patients" -> contentPane.getChildren().setAll(PatientsView.build(state));
            case "Emergency" -> contentPane.getChildren().setAll(EmergencyView.build(state));
            case "Appointments" -> contentPane.getChildren().setAll(AppointmentsView.build(state));
            case "Doctors" -> contentPane.getChildren().setAll(DoctorsView.build(state));
            case "Doctor Schedule" -> contentPane.getChildren().setAll(DoctorScheduleView.build(state));
            case "Reports" -> contentPane.getChildren().setAll(ReportsView.build(state));
            case "Settings" -> contentPane.getChildren().setAll(SettingsView.build());
            default -> showDashboard();
        }
    }

    private void showDashboard() {
        contentPane.getChildren().setAll(
            DashboardView.build(state,
                () -> { sidebarView.setActive("Appointments"); navigate("Appointments"); },
                () -> { sidebarView.setActive("Emergency"); navigate("Emergency"); }
            )
        );
    }

    public static void main(String[] args) { launch(args); }
}
