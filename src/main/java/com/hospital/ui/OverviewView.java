package com.hospital.ui;

import com.hospital.model.Doctor;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class OverviewView {
    public static Node build(AppState state, TabPane tabPane, Tab docTab, Tab patTab, Tab aptTab) {
        VBox root = new VBox(24);
        root.setPadding(new Insets(24));
        root.setAlignment(Pos.TOP_CENTER);
        HBox cards = new HBox(16);
        cards.setAlignment(Pos.CENTER);
        VBox docCard = createMetricCard("Total Doctors", state.doctors.size() + "", "Specialist Staff", "badge-ok");
        VBox availCard = createMetricCard("On Duty Doctors",
                state.doctors.stream().filter(Doctor::getAvailable).count() + "", "Ready for Consultations", "badge-ok");
        VBox ptCard = createMetricCard("Registered Patients", state.patients.size() + "", "Active Profiles", "badge-accent");
        VBox aptCard = createMetricCard("Total Appointments", state.appointments.size() + "", "Booked Sessions", "badge-warning");
        HBox.setHgrow(docCard, Priority.ALWAYS);
        HBox.setHgrow(availCard, Priority.ALWAYS);
        HBox.setHgrow(ptCard, Priority.ALWAYS);
        HBox.setHgrow(aptCard, Priority.ALWAYS);
        cards.getChildren().addAll(docCard, availCard, ptCard, aptCard);
        VBox actionsCard = new VBox(16);
        actionsCard.getStyleClass().add("card");
        Label actionTitle = new Label("Quick Operations");
        actionTitle.getStyleClass().add("section-title");
        HBox buttonRow = new HBox(16);
        Button bookAptBtn = new Button("Book Appointment");
        bookAptBtn.getStyleClass().add("btn-warning");
        bookAptBtn.setOnAction(e -> tabPane.getSelectionModel().select(aptTab));
        Button manageDoctorsBtn = new Button("Manage Doctors");
        manageDoctorsBtn.getStyleClass().add("btn-primary");
        manageDoctorsBtn.setOnAction(e -> tabPane.getSelectionModel().select(docTab));
        Button managePatientsBtn = new Button("Manage Patients");
        managePatientsBtn.getStyleClass().add("btn-success");
        managePatientsBtn.setOnAction(e -> tabPane.getSelectionModel().select(patTab));
        buttonRow.getChildren().addAll(bookAptBtn, manageDoctorsBtn, managePatientsBtn);
        actionsCard.getChildren().addAll(actionTitle, buttonRow);
        root.getChildren().addAll(cards, actionsCard);
        return new ScrollPane(root);
    }

    private static VBox createMetricCard(String title, String count, String subtext, String badgeClass) {
        VBox card = new VBox(8);
        card.getStyleClass().add("card");
        Label titleLbl = new Label(title);
        titleLbl.getStyleClass().add("sub-title");
        Label countLbl = new Label(count);
        countLbl.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #f8fafc;");
        Label subLbl = new Label(subtext);
        subLbl.getStyleClass().add("sub-title");
        card.getChildren().addAll(titleLbl, countLbl, subLbl);
        return card;
    }
}
