package com.hospital.ui.views;

import com.hospital.ui.AppState;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class PublicAppointmentRequestPage {
    public static Node build(AppState state, Runnable onBack, Runnable onLogin,
                             Runnable onDashboard, Runnable onEmergency) {
        VBox formWrap = new VBox(16);
        formWrap.setPadding(new Insets(28, 32, 40, 32));
        formWrap.setMaxWidth(760);

        Button back = new Button("← Back to dashboard");
        back.getStyleClass().add("btn-secondary");
        back.setOnAction(e -> onBack.run());

        Label heading = new Label("Request an appointment");
        heading.setStyle("-fx-font-size:24px;-fx-font-weight:800;-fx-text-fill:#10263B;");
        Label hint = new Label("Fill in your details — staff will confirm your slot. No login needed.");
        hint.setStyle("-fx-font-size:13.5px;-fx-text-fill:#46647A;");
        hint.setWrapText(true);
        VBox head = new VBox(4, back, heading, hint);
        head.setAlignment(Pos.TOP_LEFT);

        VBox formCard = PublicAppointmentRequestView.build(state);
        formCard.setId("appointment-request");

        VBox center = new VBox(formWrap);
        center.setAlignment(Pos.TOP_CENTER);
        formWrap.getChildren().addAll(head, formCard);
        VBox.setVgrow(formCard, Priority.ALWAYS);

        ScrollPane sp = new ScrollPane(center);
        sp.setFitToWidth(true);
        sp.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        sp.setStyle("-fx-background-color:transparent;-fx-background:transparent;");

        VBox page = new VBox(
                PublicHeader.build(state, onLogin, onDashboard, onEmergency, () -> {
                }), sp);
        page.setStyle("-fx-background-color:#E9EEF2;"
                + "-fx-font-family:\"IBM Plex Sans\",\"Segoe UI\",Arial,sans-serif;");
        return page;
    }
}
