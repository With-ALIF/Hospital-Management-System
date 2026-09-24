package com.hospital.ui.views;

import com.hospital.ui.AppState;
import javafx.collections.ListChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class EmergencyView {
    public static Node build(AppState state) {
        VBox root = new VBox(16);
        root.setPadding(new Insets(24));
        root.getStyleClass().add("main-content");
        Label title = new Label("Emergency Department");
        title.getStyleClass().add("page-title");
        Label sub = new Label("Triage queue and critical case management");
        sub.getStyleClass().add("page-subtitle");
        VBox headText = new VBox(2, title, sub);
        Button add = new Button("+ New Emergency Case");
        add.getStyleClass().addAll("btn-primary");
        HBox header = new HBox(12, headText, new Region(), add);
        header.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(header.getChildren().get(1), Priority.ALWAYS);
        HBox cols = new HBox(16);
        Runnable refreshCards = () -> {
            VBox criticalCard = EmergencyCards.buildCriticalCard(state);
            VBox queueCard = EmergencyCards.buildQueueCard(state);
            HBox.setHgrow(criticalCard, Priority.ALWAYS);
            HBox.setHgrow(queueCard, Priority.ALWAYS);
            criticalCard.setPrefWidth(420);
            queueCard.setPrefWidth(420);
            cols.getChildren().setAll(criticalCard, queueCard);
        };
        refreshCards.run();
        // Auto-refresh panels when a case is added / started / completed / cancelled.
        state.emergencyCases.addListener((ListChangeListener<? super Object>) c -> refreshCards.run());
        add.setOnAction(e -> {
            EmergencyNewCaseDialog.show(state);
            state.refreshEmergency();
            refreshCards.run();
        });
        Button start = new Button("Start Treatment");
        start.getStyleClass().addAll("btn-primary");
        Button complete = new Button("Complete");
        complete.getStyleClass().addAll("btn-secondary");
        Button cancel = new Button("Cancel");
        cancel.getStyleClass().addAll("btn-danger");
        Label hint = new Label("Tip: Next patient or enter case ID.");
        hint.setStyle("-fx-text-fill:#64748B;-fx-font-size:11px;");
        start.setOnAction(e -> {
            try {
                var c = state.emergencyService.startTreatment();
                state.refreshEmergency();
                refreshCards.run();
                EmergencyDialogs.info("Treatment started for "
                        + EmergencyDialogs.patientName(state, c) + " • " + c.getId());
            } catch (Exception ex) {
                EmergencyDialogs.error(ex.getMessage());
            }
        });
        complete.setOnAction(e -> EmergencyDialogs.promptId(
                "Complete", "Enter case ID to complete", id -> {
            try {
                state.emergencyService.completeTreatment(id);
                state.refreshEmergency();
                refreshCards.run();
                EmergencyDialogs.info("Completed " + id);
            } catch (Exception ex) {
                EmergencyDialogs.error(ex.getMessage());
            }
        }));
        cancel.setOnAction(e -> EmergencyDialogs.promptId(
                "Cancel", "Enter case ID to cancel", id -> {
            try {
                state.emergencyService.cancelEmergencyCase(id);
                state.refreshEmergency();
                refreshCards.run();
                EmergencyDialogs.info("Cancelled " + id);
            } catch (Exception ex) {
                EmergencyDialogs.error(ex.getMessage());
            }
        }));
        VBox actionsBox = new VBox(8, new HBox(8, start, complete, cancel), hint);
        ScrollPane sp = new ScrollPane(new VBox(16, header, cols, actionsBox));
        sp.setFitToWidth(true);
        sp.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        return sp;
    }
}
