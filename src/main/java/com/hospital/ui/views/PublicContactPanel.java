package com.hospital.ui.views;

import com.hospital.ui.AppState;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class PublicContactPanel {
    private static final String DESK_NUMBER = "+1 (555) 010-9110";
    private static final String DESK_WHATSAPP_URL =
            "https://wa.me/15550109110?text=Hello%20City%20General%20Hospital%2C%20I%20need%20emergency%20help.";

    public static HBox emergencyCard() {
        Label title = PublicDepartments.sectionTitle("Emergency information");
        Label sub = PublicDepartments.sectionSub(
                "Immediate care available 24 hours a day, 7 days a week");

        Label big911 = new Label("911");
        big911.setStyle("-fx-font-size:44px;-fx-font-weight:800;-fx-text-fill:#C1272D;"
                + "-fx-font-family:\"IBM Plex Sans\",\"Segoe UI\",Arial,sans-serif;");
        big911.getStyleClass().add("tnum");
        big911.setAccessibleText("Emergency number 9 1 1");
        Label big911Hint = new Label("Emergency number\nCall first for life-threatening emergencies");
        big911Hint.setStyle("-fx-font-size:12.5px;-fx-text-fill:#10263B;-fx-font-weight:600;");
        HBox hero = new HBox(14, big911, big911Hint);
        hero.setAlignment(Pos.CENTER_LEFT);

        Label deskLabel = new Label("Hospital emergency desk:");
        deskLabel.setStyle("-fx-font-size:13px;-fx-text-fill:#46647A;-fx-font-weight:600;");
        Hyperlink deskLink = new Hyperlink(DESK_NUMBER);
        deskLink.setStyle("-fx-font-size:15px;-fx-font-weight:700;-fx-text-fill:#0B5C75;"
                + "-fx-underline:true;-fx-cursor:hand;-fx-padding:0;");
        deskLink.setAccessibleText("Chat on WhatsApp with hospital emergency desk " + DESK_NUMBER);
        deskLink.setOnAction(e -> openWhatsApp(DESK_WHATSAPP_URL));
        deskLink.setFocusTraversable(true);

        Button callBtn = new Button("Call emergency desk");
        callBtn.setStyle("-fx-background-color:#C1272D;-fx-text-fill:white;"
                + "-fx-font-size:13px;-fx-font-weight:700;-fx-padding:10 20;"
                + "-fx-background-radius:8;-fx-cursor:hand;");
        callBtn.setOnMouseEntered(e -> callBtn.setStyle(callBtn.getStyle()
                + "-fx-background-color:#A31F25;"));
        callBtn.setOnMouseExited(e -> callBtn.setStyle("-fx-background-color:#C1272D;"
                + "-fx-text-fill:white;-fx-font-size:13px;-fx-font-weight:700;"
                + "-fx-padding:10 20;-fx-background-radius:8;-fx-cursor:hand;"));
        callBtn.setOnAction(e -> openWhatsApp(DESK_WHATSAPP_URL));
        callBtn.setAccessibleText("Chat on WhatsApp with hospital emergency desk " + DESK_NUMBER);

        HBox deskRow = new HBox(10, deskLabel, deskLink);
        deskRow.setAlignment(Pos.CENTER_LEFT);

        Label note = new Label("For life-threatening emergencies, call 911 first. "
                + "Our trauma and ambulance teams are on standby around the clock.");
        note.setStyle("-fx-font-size:12.5px;-fx-text-fill:#46647A;");
        note.setWrapText(true);

        VBox content = new VBox(10, new VBox(2, title, sub), hero, deskRow, callBtn, note);
        content.setPadding(new Insets(18));
        HBox.setHgrow(content, Priority.ALWAYS);

        Region accent = new Region();
        accent.getStyleClass().add("emergency-accent");
        accent.setPrefHeight(120);
        accent.setMinHeight(80);

        HBox box = new HBox(12, accent, content);
        box.setId("emergency");
        box.getStyleClass().add("emergency-card");
        box.setPadding(new Insets(0, 0, 0, 12));
        box.setAccessibleText("Emergency information. Emergency number 911. "
                + "Hospital emergency desk " + DESK_NUMBER);
        return box;
    }

    private static void openWhatsApp(String url) {
        try {
            if (java.awt.Desktop.isDesktopSupported()
                    && java.awt.Desktop.getDesktop().isSupported(java.awt.Desktop.Action.BROWSE)) {
                java.awt.Desktop.getDesktop().browse(java.net.URI.create(url));
            }
        } catch (Exception ignored) {
            // Browser unavailable — link remains focusable/copyable.
        }
    }

    public static VBox contactCard() {
        Label title = PublicDepartments.sectionTitle("Hospital contact");
        Label sub = PublicDepartments.sectionSub("We are here to help — reach us anytime");
        VBox lines = new VBox(8,
                line("Address", "120 Healing Avenue, Medical District, City 10001"),
                line("Reception", "+1 (555) 010-2000"),
                line("Emergency", "+1 (555) 010-9110"),
                line("Email", "info@citygeneralhospital.org"),
                line("Hours", "Open 24/7 — Emergency & Inpatient Care"));
        VBox box = new VBox(12, new VBox(2, title, sub), lines);
        box.setId("about");
        box.getStyleClass().add("card");
        box.setPadding(new Insets(18));
        return box;
    }

    public static VBox bloodCard(AppState state) {
        Label title = PublicDepartments.sectionTitle("Blood bank summary");
        Label sub = PublicDepartments.sectionSub(
                "Live availability across all blood groups");
        FlowPane chips = new FlowPane(8, 8);
        chips.setMaxWidth(Double.MAX_VALUE);
        var counts = state.bloodBankService.availableCounts();
        if (counts.isEmpty()) {
            chips.getChildren().add(simpleChip("Stock data unavailable"));
        } else {
            counts.entrySet().stream()
                    .sorted((a, b) -> a.getKey().name().compareTo(b.getKey().name()))
                    .forEach(e -> chips.getChildren().add(
                            groupChip(e.getKey().name().replace("_", " "), e.getValue())));
        }
        Label total = new Label("Total available units: "
                + counts.values().stream().mapToLong(Long::longValue).sum());
        total.setStyle("-fx-font-size:12.5px;-fx-text-fill:#46647A;-fx-font-weight:600;");
        total.getStyleClass().add("tnum");
        VBox box = new VBox(12, new VBox(2, title, sub), chips, total);
        box.getStyleClass().add("card");
        box.setPadding(new Insets(18));
        return box;
    }

    private static HBox line(String label, String value) {
        Label k = new Label(label);
        k.setStyle("-fx-font-size:12px;-fx-font-weight:700;-fx-text-fill:#0B5C75;"
                + "-fx-min-width:90;");
        Label v = new Label(value);
        v.setStyle("-fx-font-size:13px;-fx-text-fill:#10263B;");
        v.setWrapText(true);
        HBox row = new HBox(10, k, v);
        return row;
    }

    private static Label groupChip(String group, long units) {
        return simpleChip(group + " · " + units);
    }

    private static Label simpleChip(String text) {
        Label c = new Label(text);
        c.setStyle("-fx-font-size:12px;-fx-font-weight:600;-fx-text-fill:#0B5C75;"
                + "-fx-background-color:#E2EBEF;-fx-border-color:#B9D2DB;"
                + "-fx-padding:6 12;-fx-background-radius:16;-fx-border-radius:16;");
        c.getStyleClass().add("tnum");
        return c;
    }
}
