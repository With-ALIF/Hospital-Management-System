package com.hospital.ui.views;

import com.hospital.model.EmergencyCase;
import com.hospital.ui.AppState;
import com.hospital.ui.components.BadgeFactory;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.util.List;

public class DashboardEmergencyPanel {
    static VBox build(AppState state, Runnable onView) {
        VBox card = new VBox(0);
        card.getStyleClass().add("card");
        HBox head = new HBox(8);
        head.setPadding(new Insets(14, 16, 12, 16));
        head.setAlignment(Pos.CENTER_LEFT);
        Label t = new Label("Emergency Queue");
        t.getStyleClass().add("section-title");
        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);
        Label link = ViewParts.link("View all  →", onView);
        head.getChildren().addAll(t, sp, link);
        Separator sep = new Separator();
        HBox th = ViewParts.tableHeader(
                new String[]{"PRIORITY", "PATIENT", "ARRIVAL", "STATUS"},
                new double[]{106, 120, 86, 84});
        VBox rows = new VBox(0);
        List<EmergencyCase> queue = state.emergencyService.viewEmergencyQueue();
        if (queue.isEmpty()) {
            rows.getChildren().add(ViewParts.empty("No emergency cases found.",
                    "All emergency cases are currently resolved."));
        } else {
            int limit = Math.min(5, queue.size());
            for (int i = 0; i < limit; i++) {
                rows.getChildren().add(queueRow(state, queue.get(i), i));
                if (i < limit - 1) rows.getChildren().add(ViewParts.sepThin());
            }
        }
        card.getChildren().addAll(head, sep, th, rows);
        return card;
    }

    private static HBox queueRow(AppState state, EmergencyCase ec, int i) {
        HBox row = new HBox(8);
        row.setPadding(new Insets(10, 16, 10, 16));
        row.setAlignment(Pos.CENTER_LEFT);
        if (i % 2 == 1) row.setStyle("-fx-background-color:#1E1B4B;");
        Label prio = BadgeFactory.priority(String.valueOf(ec.getPriority()));
        prio.setMinWidth(78);
        prio.setPrefWidth(78);
        String patientName = state.patients.stream()
                .filter(p -> p.getId().equals(ec.getPatientId()))
                .map(p -> p.getName()).findFirst().orElse(ec.getPatientId());
        Label pat = new Label(patientName);
        pat.setStyle("-fx-font-size:12px;-fx-font-weight:500;-fx-text-fill:#E2E8F0;");
        pat.setPrefWidth(120);
        String arrival = ec.getArrivalTime() != null
                ? ec.getArrivalTime().toLocalTime().toString().substring(0, 5) : "—";
        Label arr = new Label(arrival);
        arr.setPrefWidth(86);
        arr.setStyle("-fx-font-size:12px;-fx-text-fill:#94A3B8;-fx-font-variant-numeric:tabular-nums;");
        Label st = BadgeFactory.status(ec.getStatus().name());
        st.setPrefWidth(74);
        row.getChildren().addAll(prio, pat, arr, st);
        return row;
    }
}
