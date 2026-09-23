package com.hospital.ui.components;

import com.hospital.service.PermissionService;
import com.hospital.service.RbacNav;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class SidebarView {
    private final VBox root;
    private final Consumer<String> onNavigate;
    private String active = "Dashboard";
    private final VBox navBox;
    private final PermissionService permissions = PermissionService.getInstance();

    public SidebarView(Consumer<String> onNavigate) {
        this.onNavigate = onNavigate;
        root = new VBox();
        root.getStyleClass().add("sidebar");
        root.setPrefWidth(240);
        root.setMinWidth(240);
        root.setMaxWidth(240);
        root.getChildren().add(SidebarChrome.logo());
        navBox = new VBox(4);
        navBox.setPadding(new Insets(12, 8, 12, 8));
        VBox.setVgrow(navBox, Priority.ALWAYS);
        ScrollPane scroll = new ScrollPane(navBox);
        scroll.setFitToWidth(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setStyle("-fx-background-color:transparent;-fx-background:transparent;");
        scroll.getStyleClass().add("side-scroll");
        root.getChildren().add(scroll);
        rebuild();
    }

    public void rebuild() {
        navBox.getChildren().clear();
        addItem("Dashboard");
        for (var entry : visibleSections().entrySet()) {
            if (entry.getValue().isEmpty()) continue;
            addSection(entry.getKey());
            entry.getValue().forEach(this::addItem);
        }
        addSection("ACCOUNT");
        addItem("Profile");
        addItem("Logout");
        addStatus();
    }

    private Map<String, List<String>> visibleSections() {
        Map<String, List<String>> out = new LinkedHashMap<>();
        for (var entry : RbacNav.sections().entrySet()) {
            List<String> visible = new ArrayList<>();
            for (String item : entry.getValue()) {
                if (permissions.canViewNav(item)) visible.add(item);
            }
            out.put(entry.getKey(), visible);
        }
        return out;
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
            if (name.equals(active) && !"Logout".equals(name)) return;
            if (!"Logout".equals(name)) active = name;
            rebuild();
            onNavigate.accept(name);
        });
        navBox.getChildren().add(item);
    }

    private void addStatus() {
        root.getChildren().removeIf(n -> n.getStyleClass().contains("sidebar-status"));
        root.getChildren().add(SidebarChrome.statusBox());
    }

    public VBox getRoot() { return root; }
    public void setActive(String name) { this.active = name; rebuild(); }
}
