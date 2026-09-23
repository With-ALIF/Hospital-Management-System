package com.hospital.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.hospital.enums.ShiftStatus;
import com.hospital.enums.ShiftType;

import java.time.LocalDate;
import java.time.LocalTime;

@JsonIgnoreProperties(ignoreUnknown = true)
public class StaffShift {
    private String id;
    private String staffId;
    private String staffName;
    private LocalDate date;
    private ShiftType shiftType;
    private LocalTime startTime;
    private LocalTime endTime;
    private ShiftStatus status;
    private String department;

    public StaffShift() {
        this.status = ShiftStatus.ASSIGNED;
    }

    public StaffShift(String id, String staffId, LocalDate date, ShiftType shiftType,
                      LocalTime startTime, LocalTime endTime) {
        this.id = id;
        this.staffId = staffId;
        this.date = date;
        this.shiftType = shiftType;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = ShiftStatus.ASSIGNED;
    }

    public boolean overlaps(LocalDate d, LocalTime s, LocalTime e) {
        if (date == null || !date.equals(d) || startTime == null || endTime == null) return false;
        return startTime.isBefore(e) && s.isBefore(endTime);
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getStaffId() { return staffId; }
    public void setStaffId(String staffId) { this.staffId = staffId; }
    public String getStaffName() { return staffName; }
    public void setStaffName(String staffName) { this.staffName = staffName; }
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    public ShiftType getShiftType() { return shiftType; }
    public void setShiftType(ShiftType shiftType) { this.shiftType = shiftType; }
    public LocalTime getStartTime() { return startTime; }
    public void setStartTime(LocalTime startTime) { this.startTime = startTime; }
    public LocalTime getEndTime() { return endTime; }
    public void setEndTime(LocalTime endTime) { this.endTime = endTime; }
    public ShiftStatus getStatus() { return status; }
    public void setStatus(ShiftStatus status) { this.status = status; }
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
}
