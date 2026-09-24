package com.hospital.ui.views;

import com.hospital.ui.AppState;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class PublicDepartments {
    private static final String[] FALLBACK = {
            "Cardiology", "Neurology", "Orthopedics", "Pediatrics",
            "Emergency Medicine", "Internal Medicine", "Radiology", "Surgery"
    };

    public static VBox build(AppState state) {
        Set<String> names = new LinkedHashSet<>();
        state.doctorService.getAllDoctors().stream()
                .map(d -> d.getSpecialization())
                .filter(s -> s != null && !s.isBlank())
                .forEach(names::add);
        if (names.isEmpty()) List.of(FALLBACK).forEach(names::add);

        FlowPane flow = new FlowPane(10, 10);
        for (String name : names) flow.getChildren().add(chip(name));
        flow.setMaxWidth(Double.MAX_VALUE);

        Label title = sectionTitle("Hospital departments");
        Label sub = sectionSub("Comprehensive specialty services for every patient need");
        VBox box = new VBox(12, new VBox(2, title, sub), flow);
        box.setId("departments");
        box.getStyleClass().add("card");
        box.setPadding(new Insets(18));
        return box;
    }

    private static Label chip(String name) {
        Label c = new Label(name);
        c.setStyle("-fx-font-size:12.5px;-fx-font-weight:600;-fx-text-fill:#0B5C75;"
                + "-fx-background-color:#E2EBEF;-fx-border-color:#B9D2DB;"
                + "-fx-padding:8 14;-fx-background-radius:20;-fx-border-radius:20;");
        return c;
    }

    static Label sectionTitle(String text) {
        Label t = new Label(text);
        t.setStyle("-fx-font-size:16px;-fx-font-weight:700;-fx-text-fill:#10263B;");
        return t;
    }

    static Label sectionSub(String text) {
        Label s = new Label(text);
        s.setStyle("-fx-font-size:12.5px;-fx-text-fill:#46647A;");
        return s;
    }

    public static HBox twoCol(VBox left, VBox right) {
        HBox row = new HBox(16, left, right);
        HBox.setHgrow(left, Priority.ALWAYS);
        HBox.setHgrow(right, Priority.ALWAYS);
        left.setPrefWidth(420);
        right.setPrefWidth(420);
        Region sp = new Region();
        return row;
    }
}
