package com.hospital.ui.views;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

public class SettingsView {
    public static Pane build() {
        VBox root = new VBox(20);
        root.getStyleClass().add("main-content");
        root.setPadding(new Insets(24));
        Label title = new Label("Settings");
        title.getStyleClass().add("page-title");
        Label sub = new Label("System configuration and preferences");
        sub.getStyleClass().add("page-subtitle");
        VBox head = new VBox(2, title, sub);
        VBox general = card("General",
            row("Theme", "Dark Indigo — Professional Medical"),
            row("Primary", "#6366F1  •  Background #0F0F1E  •  Surface #1E293B"),
            row("Typography", "Inter / Segoe UI — 12–20px hierarchy"),
            row("Spacing", "8 / 12 / 16 / 24 / 32  — consistent")
        );
        VBox data = card("Data & Storage",
            row("Directory", "data/"),
            row("Patients", "patients.json — auto-sync"),
            row("Doctors", "doctors.json — auto-sync"),
            row("Emergency", "emergency_cases.json — auto-sync"),
            row("Appointments", "appointments.json — auto-sync")
        );
        VBox about = card("About",
            row("System", "Hospital Management System 1.0"),
            row("Stack", "Java 21 • JavaFX 21 • Jackson 2.17 — Dark Indigo"),
            row("Build", "Desktop-first • Minimal • Data-focused • No glassmorphism")
        );
        root.getChildren().addAll(head, general, data, about);
        return root;
    }
    private static VBox card(String title, VBox... rows){
        VBox c = new VBox(0);
        c.getStyleClass().add("card");
        Label h = new Label(title);
        h.getStyleClass().add("section-title");
        h.setPadding(new Insets(14,16,10,16));
        c.getChildren().add(h);
        c.getChildren().add(new Separator());
        VBox body = new VBox(0);
        body.setPadding(new Insets(8,0,8,0));
        for(int i=0;i<rows.length;i++){
            body.getChildren().add(rows[i]);
            if(i<rows.length-1) body.getChildren().add(sep());
        }
        c.getChildren().add(body);
        return c;
    }
    private static VBox row(String k,String v){
        VBox r = new VBox(2);
        r.setPadding(new Insets(8,16,8,16));
        Label a = new Label(k.toUpperCase());
        a.setStyle("-fx-font-size:10px;-fx-font-weight:700;-fx-text-fill:#818CF8;-fx-letter-spacing:0.6px;");
        Label b = new Label(v);
        b.setStyle("-fx-font-size:12.5px;-fx-text-fill:#E2E8F0;");
        b.setWrapText(true);
        r.getChildren().addAll(a,b);
        return r;
    }
    private static Separator sep(){ Separator s=new Separator(); s.setOpacity(0.45); return s; }
}
