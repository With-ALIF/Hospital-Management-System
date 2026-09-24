package com.hospital.ui.views;

import com.hospital.ui.AppState;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class PublicHeader {
    public static HBox build(AppState state, Runnable onLogin,
                             Runnable onDashboard, Runnable onEmergency) {
        HBox bar = new HBox(16);
        bar.setAlignment(Pos.CENTER);
        bar.setPadding(new Insets(14, 28, 14, 28));
        bar.setStyle("-fx-background-color:#FFFFFF;"
                + "-fx-border-color:#E2E8F0;-fx-border-width:0 0 1 0;");
        bar.getChildren().addAll(logo(), spacer(), nav(onDashboard, onEmergency),
                loginBtn(onLogin));
        return bar;
    }

    private static HBox logo() {
        ImageView icon = new ImageView(new Image(
                PublicHeader.class.getResourceAsStream("/images/logo-icon-64.png")));
        icon.setFitWidth(32);
        icon.setFitHeight(32);
        Label name = new Label("City General Hospital");
        name.setStyle("-fx-font-size:15px;-fx-font-weight:700;-fx-text-fill:#10263B;");
        Label tag = new Label("24/7 emergency & multi-specialty care");
        tag.setStyle("-fx-font-size:10.5px;-fx-text-fill:#0B5C75;");
        VBox texts = new VBox(1, name, tag);
        HBox box = new HBox(10, icon, texts);
        box.setAlignment(Pos.CENTER_LEFT);
        return box;
    }

    private static Region spacer() {
        Region r = new Region();
        HBox.setHgrow(r, Priority.ALWAYS);
        return r;
    }

    private static HBox nav(Runnable onDashboard, Runnable onEmergency) {
        HBox nav = new HBox(6);
        nav.getChildren().addAll(
                link("Dashboard", onDashboard),
                link("Emergency info", onEmergency));
        return nav;
    }

    private static Label link(String text, Runnable action) {
        Label l = new Label(text);
        l.setStyle("-fx-font-size:13px;-fx-text-fill:#10263B;-fx-font-weight:600;"
                + "-fx-padding:8 12;-fx-background-radius:8;-fx-cursor:hand;");
        l.setFocusTraversable(true);
        l.setOnMouseEntered(e -> l.setStyle(l.getStyle()
                + "-fx-background-color:#E2EBEF;-fx-text-fill:#0B5C75;"));
        l.setOnMouseExited(e -> l.setStyle("-fx-font-size:13px;-fx-text-fill:#10263B;"
                + "-fx-font-weight:600;-fx-padding:8 12;-fx-background-radius:8;-fx-cursor:hand;"));
        l.setOnMouseClicked(e -> action.run());
        return l;
    }

    private static javafx.scene.control.Button loginBtn(Runnable onLogin) {
        javafx.scene.control.Button b = new javafx.scene.control.Button("Log in");
        b.setStyle("-fx-background-color:#0B5C75;-fx-text-fill:white;"
                + "-fx-font-size:13px;-fx-font-weight:700;-fx-padding:9 22;"
                + "-fx-background-radius:8;-fx-cursor:hand;");
        b.setOnMouseEntered(e -> b.setStyle(b.getStyle()
                + "-fx-background-color:#094E64;"));
        b.setOnMouseExited(e -> b.setStyle("-fx-background-color:#0B5C75;"
                + "-fx-text-fill:white;-fx-font-size:13px;-fx-font-weight:700;"
                + "-fx-padding:9 22;-fx-background-radius:8;-fx-cursor:hand;"));
        b.setOnAction(e -> onLogin.run());
        return b;
    }
}
