package com.hospital.ui.views;

import com.hospital.model.Appointment;
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

import java.time.LocalDate;
import java.util.List;

public class DashboardAppointmentsPanel {
    static VBox build(AppState state, Runnable onView) {
        VBox card = new VBox(0);
        card.getStyleClass().add("card");
        HBox head = new HBox(8);
        head.setPadding(new Insets(14, 16, 12, 16));
        head.setAlignment(Pos.CENTER_LEFT);
        Label t = new Label("Today's Appointments");
        t.getStyleClass().add("section-title");
        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);
        Label link = ViewParts.link("View all  →", onView);
        head.getChildren().addAll(t, sp, link);
        Separator sep = new Separator();
        HBox th = ViewParts.tableHeader(
                new String[]{"TIME", "PATIENT", "DOCTOR", "STATUS"},
                new double[]{68, 112, 112, 82});
        VBox rows = new VBox(0);
        List<Appointment> today = state.appointments.stream()
                .filter(a -> LocalDate.now().equals(a.getDate()))
                .sorted((a, b) -> a.getTime().compareTo(b.getTime()))
                .limit(5).toList();
        if (today.isEmpty()) {
            rows.getChildren().add(ViewParts.empty("No appointments today.", "Schedule will appear here."));
        } else {
            for (int i = 0; i < today.size(); i++) {
                rows.getChildren().add(apptRow(today.get(i), i));
                if (i < today.size() - 1) rows.getChildren().add(ViewParts.sepThin());
            }
        }
        card.getChildren().addAll(head, sep, th, rows);
        return card;
    }

    private static HBox apptRow(Appointment a, int i) {
        HBox row = new HBox(8);
        row.setPadding(new Insets(10, 16, 10, 16));
        row.setAlignment(Pos.CENTER_LEFT);
        if (i % 2 == 1) row.setStyle("-fx-background-color:#1E1B4B;");
        Label ti = new Label(a.getTime() != null ? a.getTime().toString().substring(0, 5) : "—");
        ti.setPrefWidth(68);
        ti.setStyle("-fx-font-weight:600;-fx-font-size:12px;-fx-text-fill:#E2E8F0;-fx-font-variant-numeric:tabular-nums;");
        Label pat = new Label(a.getPatientName());
        pat.setPrefWidth(112);
        pat.setStyle("-fx-font-size:12px;-fx-text-fill:#E2E8F0;");
        Label doc = new Label(a.getDoctorName());
        doc.setPrefWidth(112);
        doc.setStyle("-fx-font-size:12px;-fx-text-fill:#94A3B8;");
        Label st = BadgeFactory.status(a.getStatus() != null ? a.getStatus().name() : "");
        st.setPrefWidth(78);
        row.getChildren().addAll(ti, pat, doc, st);
        return row;
    }
}
