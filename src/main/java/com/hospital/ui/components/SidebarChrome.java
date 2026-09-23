package com.hospital.ui.components;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

final class SidebarChrome {
    private SidebarChrome() {}

    static VBox logo() {
        VBox logo = new VBox(2);
        logo.getStyleClass().add("sidebar-logo");
        HBox brand = new HBox(10);
        brand.setAlignment(Pos.CENTER_LEFT);
        javafx.scene.image.ImageView mark = new javafx.scene.image.ImageView();
        try {
            var is = SidebarChrome.class.getResourceAsStream("/images/logo-icon-64.png");
            if (is != null) {
                mark.setImage(new javafx.scene.image.Image(is));
                mark.setFitWidth(28);
                mark.setFitHeight(28);
                mark.setPreserveRatio(true);
            }
        } catch (Exception ignored) { }
        VBox txt = new VBox(1);
        Label t1 = new Label("Hospital Management");
        t1.getStyleClass().add("sidebar-logo-title");
        Label t2 = new Label("Operations System");
        t2.getStyleClass().add("sidebar-logo-sub");
        txt.getChildren().addAll(t1, t2);
        brand.getChildren().addAll(mark, txt);
        logo.getChildren().add(brand);
        return logo;
    }

    static HBox statusBox() {
        HBox status = new HBox(8);
        status.getStyleClass().add("sidebar-status");
        status.setAlignment(Pos.CENTER_LEFT);
        Region dot = new Region();
        dot.getStyleClass().add("sidebar-status-dot");
        VBox st = new VBox(1);
        Label a = new Label("System Status");
        a.setStyle("-fx-font-size:10.5px;-fx-text-fill:#94A3B8;-fx-font-weight:600;");
        Label b = new Label("Operational");
        b.setStyle("-fx-font-size:11px;-fx-text-fill:#4ADE80;-fx-font-weight:600;");
        HBox op = new HBox(4, dot, b);
        op.setAlignment(Pos.CENTER_LEFT);
        st.getChildren().addAll(a, op);
        status.getChildren().add(st);
        return status;
    }
}
