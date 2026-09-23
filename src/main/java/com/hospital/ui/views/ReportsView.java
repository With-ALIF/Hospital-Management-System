package com.hospital.ui.views;

import com.hospital.ui.AppState;
import com.hospital.ui.components.StatCard;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.*;

import java.time.LocalDate;

public class ReportsView {
    public static Pane build(AppState state) {
        VBox root = new VBox(20);
        root.getStyleClass().add("main-content");
        root.setPadding(new Insets(24));
        Label title = new Label("Reports");
        title.getStyleClass().add("page-title");
        Label sub = new Label("Operational analytics and system summary");
        sub.getStyleClass().add("page-subtitle");
        VBox head = new VBox(2, title, sub);
        HBox summary = new HBox(16);
        summary.getChildren().addAll(
            StatCard.create("Total Patients", String.valueOf(state.patients.size()), "Registered", null),
            StatCard.create("Total Doctors", String.valueOf(state.doctors.size()), state.doctors.stream().filter(d->d.getAvailable()).count()+" Available", "kpi-sub-success"),
            StatCard.create("Appointments", String.valueOf(state.appointments.size()), "All time", null),
            StatCard.create("Emergency", String.valueOf(state.emergencyCases.size()), state.emergencyService.viewEmergencyQueue().size()+" Waiting", null)
        );
        for(var n: summary.getChildren()) HBox.setHgrow((Region)n, Priority.ALWAYS);
        HBox cols = new HBox(16);
        VBox left = new VBox(0);
        left.getStyleClass().add("card");
        left.setPrefWidth(400);
        Label lh = new Label("Appointments by Status");
        lh.getStyleClass().add("section-title"); lh.setPadding(new Insets(14,16,10,16));
        VBox rows = new VBox(0);
        long sched = state.appointments.stream().filter(a-> a.getStatus()!=null && "SCHEDULED".equals(a.getStatus().name())).count();
        long conf = state.appointments.stream().filter(a-> a.getStatus()!=null && "CONFIRMED".equals(a.getStatus().name())).count();
        long comp = state.appointments.stream().filter(a-> a.getStatus()!=null && "COMPLETED".equals(a.getStatus().name())).count();
        long canc = state.appointments.stream().filter(a-> a.getStatus()!=null && "CANCELLED".equals(a.getStatus().name())).count();
        rows.getChildren().addAll(statRow("Scheduled", String.valueOf(sched), "#818CF8"), sep(), statRow("Confirmed", String.valueOf(conf), "#4ADE80"), sep(), statRow("Completed", String.valueOf(comp), "#4ADE80"), sep(), statRow("Cancelled", String.valueOf(canc), "#FCA5A5"));
        left.getChildren().addAll(lh, new Separator(), rows);
        VBox right = new VBox(0);
        right.getStyleClass().add("card");
        right.setPrefWidth(400);
        Label rh = new Label("Emergency by Priority");
        rh.getStyleClass().add("section-title"); rh.setPadding(new Insets(14,16,10,16));
        VBox rRows = new VBox(0);
        long crit = state.emergencyCases.stream().filter(e-> "CRITICAL".equalsIgnoreCase(String.valueOf(e.getPriority()))).count();
        long high = state.emergencyCases.stream().filter(e-> "SERIOUS".equalsIgnoreCase(String.valueOf(e.getPriority()))).count();
        long med = state.emergencyCases.stream().filter(e-> "MODERATE".equalsIgnoreCase(String.valueOf(e.getPriority()))).count();
        long low = state.emergencyCases.stream().filter(e-> "LOW".equalsIgnoreCase(String.valueOf(e.getPriority()))).count();
        rRows.getChildren().addAll(statRow("Critical", String.valueOf(crit), "#FCA5A5"), sep(), statRow("Serious", String.valueOf(high), "#FCD34D"), sep(), statRow("Moderate", String.valueOf(med), "#A5B4FC"), sep(), statRow("Low", String.valueOf(low), "#94A3B8"));
        right.getChildren().addAll(rh, new Separator(), rRows);
        HBox.setHgrow(left, Priority.ALWAYS);
        HBox.setHgrow(right, Priority.ALWAYS);
        cols.getChildren().addAll(left, right);
        VBox sys = new VBox(0);
        sys.getStyleClass().add("card");
        Label sh = new Label("System");
        sh.getStyleClass().add("section-title"); sh.setPadding(new Insets(14,16,10,16));
        VBox srows = new VBox(6);
        srows.setPadding(new Insets(12,16,16,16));
        srows.getChildren().addAll(info("Date", LocalDate.now().toString()), info("Storage", "JSON — data/ (auto-sync)"), info("Build", "HospitalSystem 1.0 — Java 21 + JavaFX 21 — Dark Indigo"));
        sys.getChildren().addAll(sh, new Separator(), srows);
        root.getChildren().addAll(head, summary, cols, sys);
        return root;
    }
    private static HBox statRow(String label, String value, String color){
        HBox row = new HBox(12);
        row.setPadding(new Insets(10,16,10,16));
        row.setAlignment(Pos.CENTER_LEFT);
        Region dot = new Region(); dot.setStyle("-fx-background-color:"+color+";-fx-min-width:8;-fx-min-height:8;-fx-max-width:8;-fx-max-height:8;-fx-background-radius:4;");
        Label l = new Label(label); l.setStyle("-fx-font-size:12px;-fx-text-fill:#CBD5E1;");
        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);
        Label v = new Label(value); v.setStyle("-fx-font-weight:700;-fx-font-size:13px;-fx-text-fill:#F1F5F9;");
        row.getChildren().addAll(dot, l, sp, v);
        return row;
    }
    private static HBox info(String k,String v){
        HBox r=new HBox(8); Label a=new Label(k); a.setStyle("-fx-font-size:11px;-fx-font-weight:700;-fx-text-fill:#818CF8; -fx-pref-width:80;"); Label b=new Label(v); b.setStyle("-fx-font-size:12px;-fx-text-fill:#CBD5E1;"); r.getChildren().addAll(a,b); return r;
    }
    private static Separator sep(){ Separator s=new Separator(); s.setOpacity(0.5); return s; }
}
