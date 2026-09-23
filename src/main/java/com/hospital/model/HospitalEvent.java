package com.hospital.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.hospital.enums.HospitalEventType;

import java.time.LocalDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
public class HospitalEvent {
    private String id;
    private HospitalEventType type;
    private String entityId;
    private String entityLabel;
    private String message;
    private String actor;
    private LocalDateTime occurredAt;

    public HospitalEvent() {
        this.occurredAt = LocalDateTime.now();
    }

    public HospitalEvent(String id, HospitalEventType type, String entityId,
                         String entityLabel, String message, String actor) {
        this.id = id;
        this.type = type;
        this.entityId = entityId;
        this.entityLabel = entityLabel;
        this.message = message;
        this.actor = actor;
        this.occurredAt = LocalDateTime.now();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public HospitalEventType getType() { return type; }
    public void setType(HospitalEventType type) { this.type = type; }
    public String getEntityId() { return entityId; }
    public void setEntityId(String entityId) { this.entityId = entityId; }
    public String getEntityLabel() { return entityLabel; }
    public void setEntityLabel(String entityLabel) { this.entityLabel = entityLabel; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public String getActor() { return actor; }
    public void setActor(String actor) { this.actor = actor; }
    public LocalDateTime getOccurredAt() { return occurredAt; }
    public void setOccurredAt(LocalDateTime occurredAt) { this.occurredAt = occurredAt; }
}
