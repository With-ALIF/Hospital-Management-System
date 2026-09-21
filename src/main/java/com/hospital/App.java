package com.hospital;

import com.hospital.ui.*;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class App extends Application {
    private final AppState state = new AppState();

    @Override
    public void start(Stage primaryStage) {
        BorderPane root = new BorderPane();
        root.getStyleClass().add("root");

        String css = getClass().getResource("/dark-theme.css").toExternalForm();
        
        HeaderView headerView = new HeaderView(state);
        state.onMetricsChanged = headerView::updateMetrics;
        root.setTop(headerView.build());

        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabPane.getStyleClass().add("tab-pane");

        Tab doctorTab = new Tab("Doctors", DoctorView.build(state));
        Tab patientTab = new Tab("Patients", PatientView.build(state));
        Tab appointmentTab = new Tab("Appointments", AppointmentView.build(state));
        Tab overviewTab = new Tab("Dashboard", OverviewView.build(state, tabPane, doctorTab, patientTab, appointmentTab));

        tabPane.getTabs().addAll(overviewTab, appointmentTab, doctorTab, patientTab);
        root.setCenter(tabPane);

        Scene scene = new Scene(root, 1150, 740);
        scene.getStylesheets().add(css);
        primaryStage.setTitle("Hospital Management System");
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(1000);
        primaryStage.setMinHeight(650);
        primaryStage.show();
        headerView.updateMetrics();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
