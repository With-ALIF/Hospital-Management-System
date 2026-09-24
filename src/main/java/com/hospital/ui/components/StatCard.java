package com.hospital.ui.components;

import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class StatCard {
    public static VBox create(String title, String value, String sub, String subStyle) {
        VBox card = new VBox(6);
        card.getStyleClass().add("kpi-card");
        card.setPrefWidth(200);
        // Sentence case: keep caller casing, no uppercase transform.
        Label t = new Label(title);
        t.getStyleClass().add("kpi-label");
        Label v = new Label(value);
        v.getStyleClass().addAll("kpi-value", "tnum");
        Label s = new Label(sub);
        s.getStyleClass().add("kpi-sub");
        if (subStyle != null) s.getStyleClass().add(subStyle);
        card.getChildren().addAll(t, v, s);
        return card;
    }
}
