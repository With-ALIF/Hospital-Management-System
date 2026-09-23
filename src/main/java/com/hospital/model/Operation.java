package com.hospital.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.hospital.enums.OperationPriority;
import com.hospital.enums.OperationStatus;

import java.time.LocalDate;
import java.time.LocalTime;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Operation {
    private String id;
    private String patientId;
    private String surgeonId;
    private String assistantDoctorId;
    private String roomId;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private String operationType;
    private OperationPriority priority;
    private OperationStatus status;
    private String notes;

    public Operation() {
        this.status = OperationStatus.SCHEDULED;
        this.priority = OperationPriority.NORMAL;
    }

    public Operation(String id, String patientId, String surgeonId, String roomId,
                     LocalDate date, LocalTime startTime, LocalTime endTime,
                     String operationType, OperationPriority priority) {
        this.id = id;
        this.patientId = patientId;
        this.surgeonId = surgeonId;
        this.roomId = roomId;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.operationType = operationType;
        this.priority = priority != null ? priority : OperationPriority.NORMAL;
        this.status = OperationStatus.SCHEDULED;
    }

    public boolean overlaps(LocalDate d, LocalTime s, LocalTime e) {
        if (date == null || !date.equals(d) || startTime == null || endTime == null) return false;
        return startTime.isBefore(e) && s.isBefore(endTime);
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getPatientId() { return patientId; }
    public void setPatientId(String patientId) { this.patientId = patientId; }
    public String getSurgeonId() { return surgeonId; }
    public void setSurgeonId(String surgeonId) { this.surgeonId = surgeonId; }
    public String getAssistantDoctorId() { return assistantDoctorId; }
    public void setAssistantDoctorId(String assistantDoctorId) { this.assistantDoctorId = assistantDoctorId; }
    public String getRoomId() { return roomId; }
    public void setRoomId(String roomId) { this.roomId = roomId; }
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    public LocalTime getStartTime() { return startTime; }
    public void setStartTime(LocalTime startTime) { this.startTime = startTime; }
    public LocalTime getEndTime() { return endTime; }
    public void setEndTime(LocalTime endTime) { this.endTime = endTime; }
    public String getOperationType() { return operationType; }
    public void setOperationType(String operationType) { this.operationType = operationType; }
    public OperationPriority getPriority() { return priority; }
    public void setPriority(OperationPriority priority) { this.priority = priority; }
    public OperationStatus getStatus() { return status; }
    public void setStatus(OperationStatus status) { this.status = status; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
