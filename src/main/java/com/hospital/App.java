package com.hospital;

import com.hospital.ui.AppState;
import com.hospital.ui.ThemeManager;
import com.hospital.ui.components.SidebarView;
import com.hospital.ui.components.TopBarView;
import com.hospital.ui.views.*;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class App extends Application {
    private final AppState state = new AppState();
    private StackPane window;
    private StackPane contentPane;
    private SidebarView sidebarView;
    private TopBarView topBar;
    private Scene scene;

    @Override
    public void start(Stage primaryStage) {
        window = new StackPane();
        scene = new Scene(window, 1280, 800);
        ThemeManager.apply(scene, ThemeManager.DARK);
        showLogin();
        primaryStage.setTitle("Hospital Management System");
        primaryStage.getIcons().addAll(
            new Image(getClass().getResourceAsStream("/images/logo-icon-16.png")),
            new Image(getClass().getResourceAsStream("/images/logo-icon-32.png")),
            new Image(getClass().getResourceAsStream("/images/logo-icon-64.png")),
            new Image(getClass().getResourceAsStream("/images/logo-icon-128.png")),
            new Image(getClass().getResourceAsStream("/images/logo-icon.png"))
        );
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(1100);
        primaryStage.setMinHeight(700);
        primaryStage.setOnCloseRequest(e -> state.authenticationService.logout());
        primaryStage.show();
    }

    private void showLogin() {
        window.getChildren().setAll(LoginView.build(state, this::showShell));
    }

    private void showShell() {
        BorderPane shell = new BorderPane();
        shell.getStyleClass().add("root");
        sidebarView = new SidebarView(this::navigate);
        topBar = new TopBarView(null);
        topBar.setOnLogout(this::logout);
        topBar.setOnProfile(() -> navigate("Profile"));
        topBar.refreshUser();
        contentPane = new StackPane();
        contentPane.getStyleClass().add("main-content");
        showDashboard();
        VBox center = new VBox(topBar.getRoot(), contentPane);
        VBox.setVgrow(contentPane, Priority.ALWAYS);
        shell.setLeft(sidebarView.getRoot());
        shell.setCenter(center);
        window.getChildren().setAll(shell);
    }

    private void logout() {
        state.authenticationService.logout();
        sidebarView = null;
        topBar = null;
        showLogin();
    }

    private void navigate(String name) {
        if ("Logout".equals(name)) {
            logout();
            return;
        }
        if (!state.permissionService.canViewNav(name)) {
            contentPane.getChildren().setAll(
                    AccessDeniedView.build("You don't have permission to "
                            + "perform this action."));
            return;
        }
        try {
            openPage(name);
        } catch (RuntimeException ex) {
            contentPane.getChildren().setAll(AccessDeniedView.forException(ex));
        }
    }

    private void openPage(String name) {
        if ("Dashboard".equals(name)) {
            showDashboard();
        } else {
            contentPane.getChildren().setAll(AppPages.build(name, state, scene));
        }
        if (topBar != null) topBar.refreshUser();
    }

    private void showDashboard() {
        contentPane.getChildren().setAll(DashboardView.build(state,
                () -> { sidebarView.setActive("Appointments"); navigate("Appointments"); },
                () -> { sidebarView.setActive("Emergency"); navigate("Emergency"); }));
    }

    public static void main(String[] args) { launch(args); }
}
