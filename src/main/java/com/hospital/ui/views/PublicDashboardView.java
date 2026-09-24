package com.hospital.ui.views;

import com.hospital.ui.AppState;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;

public class PublicDashboardView {
    public static Node build(AppState state, Runnable onLogin) {
        VBox body = new VBox(22);
        body.setPadding(new Insets(28, 32, 40, 32));
        body.getChildren().addAll(
                welcome(),
                PublicStatsCards.wrapTitle("Hospital statistics",
                        "Safe aggregate overview — no personal data shown",
                        PublicStatsCards.build(state)),
                PublicDepartments.build(state),
                PublicContactPanel.emergencyCard(),
                PublicContactPanel.bloodCard(state),
                PublicContactPanel.contactCard(),
                footer());
        body.setId("dashboard");

        VBox page = new VBox(PublicHeader.build(state, onLogin,
                () -> scrollTo(body, 0),
                () -> scrollToId(body, "emergency")), scroll(body));
        page.setStyle("-fx-background-color:#E9EEF2;"
                + "-fx-font-family:\"IBM Plex Sans\",\"Segoe UI\",Arial,sans-serif;");
        return page;
    }

    private static VBox welcome() {
        Label eyebrow = new Label("Welcome to City General Hospital");
        eyebrow.setStyle("-fx-font-size:13px;-fx-font-weight:700;-fx-text-fill:#0B5C75;");
        Label title = new Label("Compassionate care, advanced medicine");
        title.setStyle("-fx-font-size:28px;-fx-font-weight:800;-fx-text-fill:#10263B;");
        Label sub = new Label("A multi-specialty hospital delivering emergency, inpatient "
                + "and outpatient care with modern facilities and experienced clinicians.");
        sub.setStyle("-fx-font-size:14px;-fx-text-fill:#46647A;");
        sub.setWrapText(true);
        sub.setMaxWidth(720);
        VBox box = new VBox(8, eyebrow, title, sub);
        box.setId("welcome");
        return box;
    }

    private static ScrollPane scroll(VBox body) {
        ScrollPane sp = new ScrollPane(body);
        sp.setFitToWidth(true);
        sp.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        sp.setStyle("-fx-background-color:transparent;-fx-background:transparent;");
        return sp;
    }

    private static void scrollTo(VBox body, double value) {
        PaneLookup.scrollPane(body).ifPresent(sp -> sp.setVvalue(value));
    }

    private static void scrollToId(VBox body, String id) {
        PaneLookup.scrollPane(body).ifPresent(sp -> {
            Node n = body.lookup("#" + id);
            if (n == null) return;
            double maxY = sp.getContent().getBoundsInLocal().getHeight()
                    - sp.getViewportBounds().getHeight();
            if (maxY <= 0) return;
            double y = n.getLayoutY() + body.getChildren().indexOf(n) * 0
                    + n.getBoundsInParent().getMinY();
            sp.setVvalue(Math.min(1.0, Math.max(0.0, y / maxY)));
        });
    }

    private static VBox footer() {
        Label f = new Label("© City General Hospital · Patient care with integrity · "
                + "Log in for staff modules");
        f.setStyle("-fx-font-size:12px;-fx-text-fill:#46647A;-fx-alignment:center;");
        f.setAlignment(Pos.CENTER);
        VBox box = new VBox(f);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(8, 0, 0, 0));
        return box;
    }
}
