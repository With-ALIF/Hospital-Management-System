package com.hospital.service;

import com.hospital.enums.AuditAction;
import com.hospital.exception.BackupException;
import com.hospital.util.AppLogger;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

final class BackupFs {
    private BackupFs() {}

    static void copyDir(Path source, Path target) throws IOException {
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(source, "*.json")) {
            for (Path file : stream) {
                Files.copy(file, target.resolve(file.getFileName()),
                        StandardCopyOption.REPLACE_EXISTING);
            }
        }
    }

    static List<String> listDirs(Path backupRoot) {
        List<String> names = new ArrayList<>();
        if (!Files.isDirectory(backupRoot)) return names;
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(backupRoot)) {
            for (Path p : stream) {
                if (Files.isDirectory(p)) names.add(p.getFileName().toString());
            }
        } catch (IOException e) {
            AppLogger.warn("Cannot list backups: " + e.getMessage());
        }
        names.sort(Comparator.reverseOrder());
        return names;
    }

    static void deleteRecursively(Path path) throws IOException {
        try (Stream<Path> s = Files.walk(path)) {
            s.sorted(Comparator.reverseOrder()).forEach(p -> {
                try {
                    Files.delete(p);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }

    static long countFiles(Path dir) throws IOException {
        try (Stream<Path> s = Files.list(dir)) {
            return s.filter(p -> p.toString().endsWith(".json")).count();
        }
    }

    static void audit(AuditLogService logs, AuditAction action, String name, String desc) {
        logs.record("SYSTEM", action, "Backup", name, desc);
    }

    static void requireName(String backupName) {
        if (backupName == null || backupName.isBlank()) {
            throw new BackupException("Backup name required.");
        }
    }

    static Path requireDir(Path dir, String label, String name) {
        if (!Files.isDirectory(dir)) {
            throw new BackupException(label + " not found: " + name);
        }
        return dir;
    }
}
