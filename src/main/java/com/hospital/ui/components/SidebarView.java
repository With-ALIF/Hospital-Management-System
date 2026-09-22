package com.hospital.ui.components;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import java.util.function.Consumer;

public class SidebarView {
    private final VBox root;
    private final Consumer<String> onNavigate;
    private String active = "Dashboard";
    private final VBox navBox;

    public SidebarView(Consumer<String> onNavigate) {
        this.onNavigate = onNavigate;
        root = new VBox();
        root.getStyleClass().add("sidebar");
        root.setPrefWidth(240);
        root.setMinWidth(240);
        root.setMaxWidth(240);
        VBox logo = new VBox(2);
        logo.getStyleClass().add("sidebar-logo");
        HBox brand = new HBox(10);
        brand.setAlignment(Pos.CENTER_LEFT);
        javafx.scene.image.ImageView mark = new javafx.scene.image.ImageView();
        try {
            var is = getClass().getResourceAsStream("/images/logo-icon-64.png");
            if (is != null) {
                mark.setImage(new javafx.scene.image.Image(is));
                mark.setFitWidth(28); mark.setFitHeight(28);
                mark.setPreserveRatio(true);
                mark.setSmooth(true);
            }
        } catch (Exception ignored) {}
        VBox txt = new VBox(1);
        Label t1 = new Label("Hospital Management");
        t1.getStyleClass().add("sidebar-logo-title");
        Label t2 = new Label("Operations System");
        t2.getStyleClass().add("sidebar-logo-sub");
        txt.getChildren().addAll(t1, t2);
        brand.getChildren().addAll(mark, txt);
        logo.getChildren().add(brand);
        navBox = new VBox(4);
        navBox.setPadding(new Insets(12, 8, 12, 8));
        VBox.setVgrow(navBox, Priority.ALWAYS);
        root.getChildren().addAll(logo, navBox);
        rebuild();
    }
    private void rebuild() {
        navBox.getChildren().clear();
        addItem("Dashboard");
        addSection("PATIENT CARE");
        addItem("Patients");
        addItem("Emergency");
        addItem("Appointments");
        addSection("STAFF");
        addItem("Doctors");
        addItem("Doctor Schedule");
        addSection("SYSTEM");
        addItem("Reports");
        addItem("Settings");
        root.getChildren().removeIf(n -> n.getStyleClass().contains("sidebar-status"));
        HBox status = new HBox(8);
        status.getStyleClass().add("sidebar-status");
        status.setAlignment(Pos.CENTER_LEFT);
        Region dot = new Region();
        dot.getStyleClass().add("sidebar-status-dot");
        VBox st = new VBox(1);
        Label a = new Label("System Status");
        a.setStyle("-fx-font-size:10.5px;-fx-text-fill:#94A3B8;-fx-font-weight:600;-fx-letter-spacing:0.3px;");
        Label b = new Label("Operational");
        b.setStyle("-fx-font-size:11px;-fx-text-fill:#4ADE80;-fx-font-weight:600;");
        HBox op = new HBox(4, dot, b);
        op.setAlignment(Pos.CENTER_LEFT);
        st.getChildren().addAll(a, op);
        status.getChildren().add(st);
        root.getChildren().add(status);
    }
    private void addSection(String text) {
        Label l = new Label(text);
        l.getStyleClass().add("sidebar-section");
        navBox.getChildren().add(l);
    }
    private void addItem(String name) {
        Label item = new Label(name);
        item.getStyleClass().add("sidebar-item");
        if (name.equals(active)) item.getStyleClass().add("sidebar-item-active");
        item.setMaxWidth(Double.MAX_VALUE);
        item.setOnMouseClicked(e -> {
            if (name.equals(active)) return;
            active = name;
            rebuild();
            onNavigate.accept(name);
        });
        navBox.getChildren().add(item);
    }
    public VBox getRoot() { return root; }
    public void setActive(String name) { this.active = name; rebuild(); }
}
