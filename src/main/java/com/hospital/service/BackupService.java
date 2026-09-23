package com.hospital.service;

import com.hospital.enums.AuditAction;
import com.hospital.enums.Permission;
import com.hospital.exception.BackupException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class BackupService {
    private static final DateTimeFormatter FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd_HHmmss");
    private final Path dataDir;
    private final Path backupRoot;
    private final AuditLogService auditLogs;

    public BackupService() {
        this("data", "backup");
    }

    public BackupService(String dataFolder, String backupFolder) {
        this.dataDir = Paths.get(dataFolder);
        this.backupRoot = Paths.get(backupFolder);
        this.auditLogs = new AuditLogService();
    }

    public String createBackup() {
        PermissionService.getInstance().require(Permission.BACKUP_DATA);
        if (!Files.isDirectory(dataDir)) {
            throw new BackupException("Data directory not found: " + dataDir);
        }
        String name = "backup_" + LocalDateTime.now().format(FMT);
        Path target = backupRoot.resolve(name);
        try {
            Files.createDirectories(target);
            BackupFs.copyDir(dataDir, target);
            BackupFs.audit(auditLogs, AuditAction.BACKUP_CREATE, name,
                    "Backup created with " + BackupFs.countFiles(target) + " files");
            return name;
        } catch (IOException e) {
            throw new BackupException("Backup failed: " + e.getMessage());
        }
    }

    public void restoreBackup(String backupName) {
        PermissionService.getInstance().require(Permission.RESTORE_DATA);
        BackupFs.requireName(backupName);
        Path source = BackupFs.requireDir(backupRoot.resolve(backupName),
                "Backup", backupName);
        try {
            Path safety = Files.createTempDirectory(dataDir.getParent(), "pre-restore-");
            BackupFs.copyDir(dataDir, safety);
            BackupFs.copyDir(source, dataDir);
            BackupFs.audit(auditLogs, AuditAction.BACKUP_RESTORE, backupName,
                    "Backup restored successfully");
        } catch (IOException e) {
            throw new BackupException("Restore failed, data left unchanged: " + e.getMessage());
        }
    }

    public void confirmAndRestore(String backupName) {
        restoreBackup(backupName);
    }

    public List<String> listBackups() {
        return BackupFs.listDirs(backupRoot);
    }

    public void deleteBackup(String backupName) {
        PermissionService.getInstance().require(Permission.BACKUP_DATA);
        Path target = BackupFs.requireDir(backupRoot.resolve(backupName),
                "Backup", backupName);
        try {
            BackupFs.deleteRecursively(target);
            BackupFs.audit(auditLogs, AuditAction.BACKUP_DELETE, backupName,
                    "Backup deleted");
        } catch (IOException e) {
            throw new BackupException("Delete failed: " + e.getMessage());
        }
    }

    public String autoBackupIfNeeded(int maxBackups) {
        List<String> existing = listBackups();
        if (existing.size() >= maxBackups) {
            for (int i = maxBackups - 1; i < existing.size(); i++) {
                deleteBackup(existing.get(i));
            }
        }
        return createBackup();
    }

    public Path getBackupRoot() { return backupRoot; }
    public Path getDataDir() { return dataDir; }
}
