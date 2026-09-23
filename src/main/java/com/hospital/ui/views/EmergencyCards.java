package com.hospital.ui.views;

import com.hospital.model.EmergencyCase;
import com.hospital.ui.AppState;
import com.hospital.ui.components.BadgeFactory;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.Comparator;

final class EmergencyCards {
    private EmergencyCards() {
    }

    static VBox buildCriticalCard(AppState state) {
        VBox card = new VBox(0);
        card.getStyleClass().add("card");
        Label h = new Label("Critical Cases");
        h.getStyleClass().add("section-title");
        h.setPadding(new Insets(14, 16, 10, 16));
        Separator sep = new Separator();
        VBox list = new VBox(0);
        var crit = state.emergencyCases.stream()
                .filter(e -> "CRITICAL".equalsIgnoreCase(String.valueOf(e.getPriority()))
                        && "WAITING".equals(e.getStatus().name()))
                .sorted(Comparator.comparing(e -> e.getArrivalTime() != null
                        ? e.getArrivalTime() : java.time.LocalDateTime.MIN))
                .limit(5).toList();
        if (crit.isEmpty()) {
            list.getChildren().add(ViewParts.empty("No critical cases.", "Queue is clear — all stable."));
        } else {
            for (EmergencyCase ec : crit) {
                VBox row = new VBox(6);
                row.setPadding(new Insets(12, 16, 12, 16));
                row.setStyle("-fx-border-color:#7F1D1D;-fx-border-width:0 0 0 3;-fx-background-color:#1E293B;");
                Label name = new Label(EmergencyDialogs.patientName(state, ec));
                name.setStyle("-fx-font-weight:600;-fx-font-size:13px;-fx-text-fill:#F1F5F9;");
                Label desc = new Label(ec.getDescription() != null ? ec.getDescription() : "—");
                desc.setStyle("-fx-text-fill:#94A3B8;-fx-font-size:12px;");
                Label meta = new Label(ec.getId() + "  •  " + (ec.getArrivalTime() != null
                        ? ec.getArrivalTime().toLocalTime().toString().substring(0, 5) : "—"));
                meta.setStyle("-fx-text-fill:#64748B;-fx-font-size:11px;");
                HBox badges = new HBox(6,
                        BadgeFactory.priority(String.valueOf(ec.getPriority())),
                        BadgeFactory.status(ec.getStatus().name()));
                row.getChildren().addAll(name, desc, meta, badges);
                list.getChildren().add(row);
                list.getChildren().add(new Separator());
            }
        }
        card.getChildren().addAll(h, sep, list);
        return card;
    }

    static VBox buildQueueCard(AppState state) {
        VBox card = new VBox(0);
        card.getStyleClass().add("card");
        Label h = new Label("Emergency Queue");
        h.getStyleClass().add("section-title");
        h.setPadding(new Insets(14, 16, 10, 16));
        Separator sep = new Separator();
        HBox th = ViewParts.tableHeader(
                new String[]{" #", "PATIENT", "PRIORITY", "ID"},
                new double[]{24, 110, 88, 64});
        VBox list = new VBox(0);
        var queue = state.emergencyService.viewEmergencyQueue();
        if (queue.isEmpty()) {
            list.getChildren().add(ViewParts.empty("No emergency cases found.",
                    "All emergency cases are currently resolved."));
        } else {
            int limit = Math.min(8, queue.size());
            for (int i = 0; i < limit; i++) {
                EmergencyCase ec = queue.get(i);
                HBox row = new HBox(8);
                row.setPadding(new Insets(10, 16, 10, 16));
                row.setAlignment(Pos.CENTER_LEFT);
                if (i % 2 == 1) row.setStyle("-fx-background-color:#1E1B4B;");
                Label idx = new Label(String.valueOf(i + 1));
                idx.setStyle("-fx-font-weight:700;-fx-text-fill:#64748B;-fx-font-size:12px;");
                idx.setPrefWidth(24);
                Label name = new Label(EmergencyDialogs.patientName(state, ec));
                name.setPrefWidth(110);
                name.setStyle("-fx-font-size:12px;-fx-font-weight:500;-fx-text-fill:#E2E8F0;");
                Label prio = BadgeFactory.priority(String.valueOf(ec.getPriority()));
                prio.setPrefWidth(82);
                Label id = new Label(ec.getId());
                id.setStyle("-fx-text-fill:#64748B;-fx-font-size:11px;");
                id.setPrefWidth(64);
                row.getChildren().addAll(idx, name, prio, id);
                list.getChildren().add(row);
                if (i < limit - 1) list.getChildren().add(ViewParts.sepThin());
            }
        }
        card.getChildren().addAll(h, sep, th, list);
        return card;
    }
}
