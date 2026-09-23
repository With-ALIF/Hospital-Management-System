package com.hospital.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.hospital.enums.NotificationType;

import java.time.LocalDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Notification {
    private String id;
    private NotificationType type;
    private String title;
    private String message;
    private LocalDateTime createdAt;
    private boolean read;
    private String relatedId;

    public Notification() {
        this.createdAt = LocalDateTime.now();
        this.type = NotificationType.GENERAL;
    }

    public Notification(String id, NotificationType type, String title, String message) {
        this.id = id;
        this.type = type != null ? type : NotificationType.GENERAL;
        this.title = title;
        this.message = message;
        this.createdAt = LocalDateTime.now();
        this.read = false;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public NotificationType getType() { return type; }
    public void setType(NotificationType type) { this.type = type; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public boolean isRead() { return read; }
    public void setRead(boolean read) { this.read = read; }
    public String getRelatedId() { return relatedId; }
    public void setRelatedId(String relatedId) { this.relatedId = relatedId; }
}
