package com.hospital.ui;

import javafx.scene.Scene;

import java.util.ArrayList;
import java.util.List;

public final class ThemeManager {
    public static final String LIGHT = "light";
    public static final String DARK = "dark";
    private static final String[] LIGHT_FILES = {
        "/light-theme.css", "/light-surface.css", "/light-controls.css"
    };
    private static final String[] DARK_FILES = {
        "/dark-theme.css", "/dark-surface.css", "/dark-controls.css"
    };
    private static String current = DARK;

    private ThemeManager() {
    }

    public static String current() {
        return current;
    }

    public static void apply(Scene scene, String theme) {
        if (scene == null) {
            return;
        }
        current = DARK.equalsIgnoreCase(theme) ? DARK : LIGHT;
        List<String> sheets = new ArrayList<>();
        for (String f : files(current)) {
            var url = ThemeManager.class.getResource(f);
            if (url != null) {
                sheets.add(url.toExternalForm());
            }
        }
        scene.getStylesheets().setAll(sheets);
    }

    public static void toggle(Scene scene) {
        apply(scene, LIGHT.equals(current) ? DARK : LIGHT);
    }

    private static String[] files(String theme) {
        return DARK.equals(theme) ? DARK_FILES : LIGHT_FILES;
    }
}
