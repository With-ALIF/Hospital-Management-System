package com.hospital.ui;

import javafx.application.Platform;
import javafx.scene.Scene;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Dev-time CSS hot reload for JavaFX.
 *
 * <p>Watches {@code src/main/resources/*.css} with a {@link WatchService}.
 * When a CSS file is saved, the current {@link Scene}'s stylesheets are
 * reloaded on the JavaFX thread — no app restart, no {@code mvn javafx:run}.
 *
 * <p>Packaged JAR-এ {@code src/main/resources} থাকে না; তখন watch() silently
 * no-op হয়ে যায়, তাই production-এ নিরাপদ।
 */
public final class CssReloader {
    private static final String[] LIGHT_FILES = {
        "light-theme.css", "light-surface.css", "light-controls.css"
    };
    private static final String[] DARK_FILES = {
        "dark-theme.css", "dark-surface.css", "dark-controls.css"
    };
    private static final AtomicBoolean WATCHING = new AtomicBoolean(false);

    private CssReloader() {
    }

    /** Default resources dir ({@code src/main/resources}) watch করে। */
    public static void watch(Scene scene) {
        watch(scene, Paths.get("src/main/resources"));
    }

    /**
     * Starts a daemon watcher thread (only once per JVM).
     * If the directory does not exist (packaged JAR), this is a no-op.
     */
    public static void watch(Scene scene, Path resourcesDir) {
        if (scene == null || resourcesDir == null || !Files.isDirectory(resourcesDir)) {
            return;
        }
        if (!WATCHING.compareAndSet(false, true)) {
            return;
        }
        reload(scene, resourcesDir);
        Thread watcher = new Thread(() -> loop(scene, resourcesDir), "css-reloader");
        watcher.setDaemon(true);
        watcher.start();
    }

    /** Manually reload stylesheets for the current theme (e.g. after a theme toggle). */
    public static void reload(Scene scene) {
        reload(scene, Paths.get("src/main/resources"));
    }

    private static void loop(Scene scene, Path resourcesDir) {
        try (WatchService ws = FileSystems.getDefault().newWatchService()) {
            resourcesDir.register(ws,
                    StandardWatchEventKinds.ENTRY_CREATE,
                    StandardWatchEventKinds.ENTRY_MODIFY);
            while (true) {
                WatchKey key = ws.take();
                boolean cssChanged = false;
                for (WatchEvent<?> event : key.pollEvents()) {
                    String name = event.context().toString();
                    if (name.endsWith(".css")) {
                        cssChanged = true;
                    }
                }
                if (!key.reset()) {
                    break;
                }
                if (cssChanged) {
                    Thread.sleep(250); // debounce: editor অনেক সময় দুবার write করে
                    Platform.runLater(() -> reload(scene, resourcesDir));
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (IOException e) {
            System.err.println("[CssReloader] " + e.getMessage());
        }
    }

    private static void reload(Scene scene, Path resourcesDir) {
        String theme = ThemeManager.current();
        String[] files = ThemeManager.DARK.equalsIgnoreCase(theme) ? DARK_FILES : LIGHT_FILES;
        List<String> sheets = new ArrayList<>();
        for (String file : files) {
            Path css = resourcesDir.resolve(file);
            if (Files.isRegularFile(css)) {
                sheets.add(css.toUri().toString());
            }
        }
        if (sheets.isEmpty()) {
            ThemeManager.apply(scene, theme);
            return;
        }
        scene.getStylesheets().setAll(sheets);
    }
}
