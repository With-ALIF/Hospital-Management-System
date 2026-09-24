package com.hospital.ui.views;

import javafx.scene.Node;
import javafx.scene.control.ScrollPane;

import java.util.Optional;

final class PaneLookup {
    private PaneLookup() {}

    static Optional<ScrollPane> scrollPane(Node node) {
        Node n = node;
        while (n != null) {
            if (n instanceof ScrollPane sp) return Optional.of(sp);
            n = n.getParent();
        }
        return Optional.empty();
    }
}
