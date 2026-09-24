package com.hospital;

import com.hospital.ui.AppState;
import com.hospital.ui.ThemeManager;
import com.hospital.ui.views.*;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class App extends Application {
    private final AppState state = new AppState();
    private StackPane window;
    private Scene scene;
    private AppShell shell;
    private String pendingPage;

    @Override
    public void start(Stage primaryStage) {
        window = new StackPane();
        scene = new Scene(window, 1280, 800);
        shell = new AppShell(this, state, window, scene);
        ThemeManager.apply(scene, ThemeManager.LIGHT);
        showPublic();
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

    void showPublic() {
        ThemeManager.apply(scene, ThemeManager.LIGHT);
        shell.clear();
        pendingPage = null;
        window.getChildren().setAll(
                PublicDashboardView.build(state, () -> showLogin(null)));
    }

    void showLogin(String requestedPage) {
        pendingPage = requestedPage;
        ThemeManager.apply(scene, ThemeManager.LIGHT);
        window.getChildren().setAll(LoginView.build(state, this::onLoginSuccess));
    }

    private void onLoginSuccess() {
        shell.show();
        String target = pendingPage;
        pendingPage = null;
        if (target != null && !"Dashboard".equals(target)) {
            navigate(target);
        }
    }

    void showLoginRequired(String page) {
        pendingPage = page;
        ThemeManager.apply(scene, ThemeManager.LIGHT);
        window.getChildren().setAll(LoginRequiredView.build(
                page, () -> showLogin(page), this::showPublic));
    }

    void logout() {
        state.authenticationService.logout();
        showPublic();
    }

    void navigate(String name) {
        if ("Logout".equals(name)) {
            logout();
            return;
        }
        if (!state.sessionManager.isLoggedIn()) {
            showLoginRequired(name);
            return;
        }
        if (!state.permissionService.canViewNav(name)) {
            shell.content.getChildren().setAll(
                    AccessDeniedView.build("You don't have permission to "
                            + "perform this action."));
            return;
        }
        try {
            shell.openPage(name);
        } catch (RuntimeException ex) {
            shell.content.getChildren().setAll(AccessDeniedView.forException(ex));
        }
    }

    public static void main(String[] args) { launch(args); }
}
