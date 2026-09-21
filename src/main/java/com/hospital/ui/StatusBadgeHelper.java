package com.hospital.ui;

public class StatusBadgeHelper {
    public static String getStatusBadgeStyle(String status) {
        switch (status.toUpperCase()) {
            case "SCHEDULED":
                return "-fx-background-color: rgba(37, 99, 235, 0.15); -fx-text-fill: #60a5fa; -fx-border-color: rgba(37, 99, 235, 0.3); -fx-border-radius: 20; -fx-background-radius: 20;";
            case "CONFIRMED":
                return "-fx-background-color: rgba(34, 197, 94, 0.15); -fx-text-fill: #4ade80; -fx-border-color: rgba(34, 197, 94, 0.3); -fx-border-radius: 20; -fx-background-radius: 20;";
            case "IN_PROGRESS":
                return "-fx-background-color: rgba(245, 158, 11, 0.15); -fx-text-fill: #fbbf24; -fx-border-color: rgba(245, 158, 11, 0.3); -fx-border-radius: 20; -fx-background-radius: 20;";
            case "COMPLETED":
                return "-fx-background-color: rgba(34, 197, 94, 0.15); -fx-text-fill: #4ade80; -fx-border-color: rgba(34, 197, 94, 0.3); -fx-border-radius: 20; -fx-background-radius: 20;";
            case "CANCELLED":
                return "-fx-background-color: rgba(239, 68, 68, 0.15); -fx-text-fill: #f87171; -fx-border-color: rgba(239, 68, 68, 0.3); -fx-border-radius: 20; -fx-background-radius: 20;";
            default:
                return "-fx-background-color: rgba(100, 116, 139, 0.15); -fx-text-fill: #94a3b8; -fx-border-color: rgba(100, 116, 139, 0.3); -fx-border-radius: 20; -fx-background-radius: 20;";
        }
    }
}
