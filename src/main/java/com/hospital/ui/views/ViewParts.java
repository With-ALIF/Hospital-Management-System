package com.hospital.ui.views;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public final class ViewParts {
    private ViewParts() {
    }

    static Label link(String text, Runnable onView) {
        Label link = new Label(text);
        link.setStyle("-fx-text-fill:#818CF8;-fx-font-size:11.5px;-fx-font-weight:600;-fx-cursor:hand;");
        if (onView != null) link.setOnMouseClicked(e -> onView.run());
        return link;
    }

    static HBox tableHeader(String[] titles, double[] widths) {
        HBox th = new HBox(8);
        th.setPadding(new Insets(9, 16, 8, 16));
        th.setStyle("-fx-background-color:#1E1B4B;");
        for (int i = 0; i < titles.length; i++) {
            Label l = new Label(titles[i]);
            l.setStyle("-fx-font-size:10px;-fx-font-weight:700;-fx-text-fill:#818CF8;-fx-letter-spacing:0.6px;");
            l.setPrefWidth(widths[i]);
            th.getChildren().add(l);
        }
        return th;
    }

    static VBox empty(String a, String b) {
        VBox box = new VBox(4);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(28, 16, 28, 16));
        Label t = new Label(a);
        t.getStyleClass().add("empty-state-title");
        Label s = new Label(b);
        s.getStyleClass().add("empty-state-sub");
        s.setWrapText(true);
        box.getChildren().addAll(t, s);
        return box;
    }

    static Separator sepThin() {
        Separator s = new Separator();
        s.setOpacity(0.45);
        return s;
    }
}
