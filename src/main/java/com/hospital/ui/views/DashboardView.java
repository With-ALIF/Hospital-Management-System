package com.hospital.ui.views;

import com.hospital.model.Appointment;
import com.hospital.model.EmergencyCase;
import com.hospital.ui.AppState;
import com.hospital.ui.components.BadgeFactory;
import com.hospital.ui.components.StatCard;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.layout.*;

import java.time.LocalDate;
import java.util.List;

public class DashboardView {
    public static Node build(AppState state, Runnable onViewAppointments, Runnable onViewEmergency) {
        VBox root = new VBox(20);
        root.getStyleClass().add("main-content");
        root.setPadding(new Insets(24));
        Label greet = new Label("Good Morning");
        greet.setStyle("-fx-font-size:12.5px;-fx-text-fill:#94A3B8;-fx-font-weight:500;");
        Label title = new Label("Hospital Overview");
        title.getStyleClass().add("page-title");
        Label sub = new Label("Real-time operational summary  •  " + LocalDate.now().toString());
        sub.getStyleClass().add("page-subtitle");
        VBox header = new VBox(2, greet, title, sub);
        int totalPatients = state.patients.size();
        long availableDoctors = state.doctors.stream().filter(d -> d.getAvailable()).count();
        int totalDoctors = state.doctors.size();
        long emergencyWaiting = state.emergencyCases.stream().filter(e -> "WAITING".equals(e.getStatus().name())).count();
        long critical = state.emergencyCases.stream().filter(e -> "CRITICAL".equalsIgnoreCase(String.valueOf(e.getPriority())) && "WAITING".equals(e.getStatus().name())).count();
        int todayAppts = (int) state.appointments.stream().filter(a -> LocalDate.now().equals(a.getDate())).count();
        HBox kpis = new HBox(16);
        kpis.getChildren().addAll(
            StatCard.create("Total Patients", String.valueOf(totalPatients), "+" + Math.min(12, totalPatients) + " today", null),
            StatCard.create("Doctors", String.valueOf(totalDoctors), availableDoctors + " Available", "kpi-sub-success"),
            StatCard.create("Emergency", String.format("%02d", emergencyWaiting), critical + " Critical", critical>0 ? "kpi-sub-danger" : "kpi-sub-success"),
            StatCard.create("Appointments", String.valueOf(todayAppts), state.appointments.size() + " Total  •  Today", null)
        );
        for (var n : kpis.getChildren()) HBox.setHgrow((Region)n, Priority.ALWAYS);
        HBox columns = new HBox(16);
        VBox emergencyPanel = buildEmergencyPanel(state, onViewEmergency);
        VBox apptPanel = buildAppointmentsPanel(state, onViewAppointments);
        HBox.setHgrow(emergencyPanel, Priority.ALWAYS);
        HBox.setHgrow(apptPanel, Priority.ALWAYS);
        emergencyPanel.setPrefWidth(420);
        apptPanel.setPrefWidth(420);
        columns.getChildren().addAll(emergencyPanel, apptPanel);
        root.getChildren().addAll(header, kpis, columns);
        ScrollPane sp = new ScrollPane(root);
        sp.setFitToWidth(true);
        sp.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        sp.setStyle("-fx-background-color:transparent;");
        return sp;
    }
    private static VBox buildEmergencyPanel(AppState state, Runnable onView) {
        VBox card = new VBox(0);
        card.getStyleClass().add("card");
        HBox head = new HBox(8);
        head.setPadding(new Insets(14,16,12,16));
        head.setAlignment(Pos.CENTER_LEFT);
        Label t = new Label("Emergency Queue");
        t.getStyleClass().add("section-title");
        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);
        Label link = new Label("View all  →");
        link.setStyle("-fx-text-fill:#818CF8;-fx-font-size:11.5px;-fx-font-weight:600;-fx-cursor:hand;");
        if (onView != null) link.setOnMouseClicked(e -> onView.run());
        head.getChildren().addAll(t, sp, link);
        Separator sep = new Separator();
        HBox th = new HBox(8);
        th.setPadding(new Insets(9,16,8,16));
        th.setStyle("-fx-background-color:#1E1B4B;");
        th.getChildren().addAll(hdr("PRIORITY",106), hdr("PATIENT",120), hdr("ARRIVAL",86), hdr("STATUS",84));
        VBox rows = new VBox(0);
        List<EmergencyCase> queue = state.emergencyService.viewEmergencyQueue();
        if (queue.isEmpty()) {
            rows.getChildren().add(empty("No emergency cases found.", "All emergency cases are currently resolved."));
        } else {
            int limit = Math.min(5, queue.size());
            for (int i=0;i<limit;i++) {
                EmergencyCase ec = queue.get(i);
                HBox row = new HBox(8);
                row.setPadding(new Insets(10,16,10,16));
                row.setAlignment(Pos.CENTER_LEFT);
                if (i % 2 == 1) row.setStyle("-fx-background-color:#1E1B4B;");
                Label prio = BadgeFactory.priority(String.valueOf(ec.getPriority()));
                prio.setMinWidth(78); prio.setPrefWidth(78);
                String patientName = state.patients.stream().filter(p->p.getId().equals(ec.getPatientId())).map(p->p.getName()).findFirst().orElse(ec.getPatientId());
                Label pat = new Label(patientName); pat.setStyle("-fx-font-size:12px;-fx-font-weight:500;-fx-text-fill:#E2E8F0;"); pat.setPrefWidth(120);
                String arrival = ec.getArrivalTime()!=null ? ec.getArrivalTime().toLocalTime().toString().substring(0,5) : "—";
                Label arr = new Label(arrival); arr.setPrefWidth(86); arr.setStyle("-fx-font-size:12px;-fx-text-fill:#94A3B8; -fx-font-variant-numeric: tabular-nums;");
                Label st = BadgeFactory.status(ec.getStatus().name()); st.setPrefWidth(74);
                row.getChildren().addAll(prio, pat, arr, st);
                rows.getChildren().add(row);
                if (i < limit-1) rows.getChildren().add(sepThin());
            }
        }
        card.getChildren().addAll(head, sep, th, rows);
        return card;
    }
    private static VBox buildAppointmentsPanel(AppState state, Runnable onView) {
        VBox card = new VBox(0);
        card.getStyleClass().add("card");
        HBox head = new HBox(8);
        head.setPadding(new Insets(14,16,12,16));
        head.setAlignment(Pos.CENTER_LEFT);
        Label t = new Label("Today's Appointments");
        t.getStyleClass().add("section-title");
        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);
        Label link = new Label("View all  →");
        link.setStyle("-fx-text-fill:#818CF8;-fx-font-size:11.5px;-fx-font-weight:600;-fx-cursor:hand;");
        if (onView != null) link.setOnMouseClicked(e -> onView.run());
        head.getChildren().addAll(t, sp, link);
        Separator sep = new Separator();
        HBox th = new HBox(8);
        th.setPadding(new Insets(9,16,8,16));
        th.setStyle("-fx-background-color:#1E1B4B;");
        th.getChildren().addAll(hdr("TIME",68), hdr("PATIENT",112), hdr("DOCTOR",112), hdr("STATUS",82));
        VBox rows = new VBox(0);
        List<Appointment> today = state.appointments.stream().filter(a -> LocalDate.now().equals(a.getDate())).sorted((a,b)-> a.getTime().compareTo(b.getTime())).limit(5).toList();
        if (today.isEmpty()) {
            rows.getChildren().add(empty("No appointments today.", "Schedule will appear here."));
        } else {
            for (int i=0;i<today.size();i++) {
                Appointment a = today.get(i);
                HBox row = new HBox(8);
                row.setPadding(new Insets(10,16,10,16));
                row.setAlignment(Pos.CENTER_LEFT);
                if (i %2==1) row.setStyle("-fx-background-color:#1E1B4B;");
                Label ti = new Label(a.getTime()!=null?a.getTime().toString().substring(0,5):"—"); ti.setPrefWidth(68); ti.setStyle("-fx-font-weight:600;-fx-font-size:12px;-fx-text-fill:#E2E8F0;-fx-font-variant-numeric: tabular-nums;");
                Label pat = new Label(a.getPatientName()); pat.setPrefWidth(112); pat.setStyle("-fx-font-size:12px;-fx-text-fill:#E2E8F0;");
                Label doc = new Label(a.getDoctorName()); doc.setPrefWidth(112); doc.setStyle("-fx-font-size:12px;-fx-text-fill:#94A3B8;");
                Label st = BadgeFactory.status(a.getStatus()!=null?a.getStatus().name():""); st.setPrefWidth(78);
                row.getChildren().addAll(ti, pat, doc, st);
                rows.getChildren().add(row);
                if (i<today.size()-1) rows.getChildren().add(sepThin());
            }
        }
        card.getChildren().addAll(head, sep, th, rows);
        return card;
    }
    private static Label hdr(String t, double w){ Label l=new Label(t); l.setStyle("-fx-font-size:10px;-fx-font-weight:700;-fx-text-fill:#818CF8;-fx-letter-spacing:0.6px;"); l.setPrefWidth(w); return l; }
    private static VBox empty(String a,String b){
        VBox box=new VBox(4); box.setAlignment(Pos.CENTER); box.setPadding(new Insets(28,16,28,16));
        Label t=new Label(a); t.getStyleClass().add("empty-state-title");
        Label s=new Label(b); s.getStyleClass().add("empty-state-sub"); s.setWrapText(true);
        box.getChildren().addAll(t,s); return box;
    }
    private static Separator sepThin(){ Separator s=new Separator(); s.setOpacity(0.45); return s; }
}
