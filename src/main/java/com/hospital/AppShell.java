package com.hospital;

import com.hospital.ui.AppState;
import com.hospital.ui.ThemeManager;
import com.hospital.ui.components.SidebarView;
import com.hospital.ui.components.TopBarView;
import com.hospital.ui.views.*;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

class AppShell {
    private final App app;
    private final AppState state;
    private final StackPane window;
    private final Scene scene;
    SidebarView sidebar;
    TopBarView topBar;
    StackPane content;

    AppShell(App app, AppState state, StackPane window, Scene scene) {
        this.app = app;
        this.state = state;
        this.window = window;
        this.scene = scene;
    }

    void show() {
        ThemeManager.apply(scene, ThemeManager.DARK);
        BorderPane shell = new BorderPane();
        shell.getStyleClass().add("root");
        sidebar = new SidebarView(app::navigate);
        topBar = new TopBarView(null);
        topBar.setOnLogout(app::logout);
        topBar.setOnProfile(() -> app.navigate("Profile"));
        topBar.refreshUser();
        content = new StackPane();
        content.getStyleClass().add("main-content");
        showDashboard();
        VBox center = new VBox(topBar.getRoot(), content);
        VBox.setVgrow(content, Priority.ALWAYS);
        shell.setLeft(sidebar.getRoot());
        shell.setCenter(center);
        window.getChildren().setAll(shell);
    }

    void showDashboard() {
        content.getChildren().setAll(DashboardView.build(state,
                () -> { sidebar.setActive("Appointments"); app.navigate("Appointments"); },
                () -> { sidebar.setActive("Emergency"); app.navigate("Emergency"); }));
    }

    void openPage(String name) {
        if ("Dashboard".equals(name)) {
            showDashboard();
        } else {
            content.getChildren().setAll(AppPages.build(name, state, scene));
        }
        if (topBar != null) topBar.refreshUser();
    }

    void clear() {
        sidebar = null;
        topBar = null;
        content = null;
    }
}
