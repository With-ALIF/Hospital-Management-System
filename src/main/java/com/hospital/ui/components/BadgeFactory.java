package com.hospital.ui.components;

import javafx.scene.control.Label;

public class BadgeFactory {
    public static Label priority(String p) {
        Label l = new Label(p);
        l.getStyleClass().addAll("badge");
        if (p == null) p = "";
        switch (p.toUpperCase()) {
            case "CRITICAL" -> l.getStyleClass().add("badge-critical");
            case "SERIOUS", "HIGH" -> l.getStyleClass().add("badge-high");
            case "MODERATE", "MEDIUM" -> l.getStyleClass().add("badge-medium");
            case "LOW" -> l.getStyleClass().add("badge-low");
            default -> l.getStyleClass().add("badge-neutral");
        }
        return l;
    }
    public static Label status(String s) {
        Label l = new Label(s);
        l.getStyleClass().addAll("badge");
        if (s == null) s = "";
        switch (s.toUpperCase()) {
            case "SCHEDULED" -> l.getStyleClass().add("badge-info");
            case "CONFIRMED" -> l.getStyleClass().add("badge-success");
            case "COMPLETED" -> l.getStyleClass().add("badge-success");
            case "CANCELLED" -> l.getStyleClass().add("badge-danger");
            case "WAITING" -> l.getStyleClass().add("badge-warning");
            case "IN_TREATMENT", "IN PROGRESS", "IN_PROGRESS" -> l.getStyleClass().add("badge-info");
            default -> l.getStyleClass().add("badge-neutral");
        }
        return l;
    }
    public static Label availability(boolean available) {
        Label l = new Label(available ? "Available" : "Busy");
        l.getStyleClass().addAll("badge");
        l.getStyleClass().add(available ? "badge-success" : "badge-warning");
        return l;
    }
}
