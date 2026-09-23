package com.hospital.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.hospital.enums.RoomStatus;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OperationRoom {
    private String id;
    private String name;
    private String type;
    private RoomStatus status;

    public OperationRoom() {
        this.status = RoomStatus.AVAILABLE;
    }

    public OperationRoom(String id, String name, String type) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.status = RoomStatus.AVAILABLE;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public RoomStatus getStatus() { return status; }
    public void setStatus(RoomStatus status) { this.status = status; }
}
