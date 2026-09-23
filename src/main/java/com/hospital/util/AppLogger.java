package com.hospital.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;

public final class AppLogger {
    private static final Path LOG_FILE = Path.of("data", "app.log");

    private AppLogger() {
    }

    public static void info(String message) {
        write("INFO", message);
    }

    public static void error(String message) {
        write("ERROR", message);
    }

    public static void error(String message, Throwable cause) {
        write("ERROR", message + " — " + cause.getClass().getSimpleName() + ": " + cause.getMessage());
    }

    public static void warn(String message) {
        write("WARN", message);
    }

    private static void write(String level, String message) {
        String line = "[" + LocalDateTime.now().format(DateTimeUtil.DATE_TIME) + "] [" + level + "] " + message;
        System.out.println(line);
        try {
            Files.createDirectories(LOG_FILE.getParent());
            Files.writeString(LOG_FILE, line + System.lineSeparator(),
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException ignored) {
        }
    }
}
