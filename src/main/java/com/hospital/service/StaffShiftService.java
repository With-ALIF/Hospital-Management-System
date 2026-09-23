package com.hospital.service;

import com.hospital.enums.AuditAction;
import com.hospital.enums.ShiftStatus;
import com.hospital.enums.ShiftType;
import com.hospital.exception.ConflictException;
import com.hospital.exception.InvalidDataException;
import com.hospital.model.Staff;
import com.hospital.model.StaffShift;
import com.hospital.repository.StaffRepository;
import com.hospital.repository.StaffShiftRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public class StaffShiftService {
    private final StaffShiftRepository shifts;
    private final StaffRepository staffRepo;
    private final AuditLogService auditLogs;
    private final NotificationService notifications;

    public StaffShiftService() {
        this(StaffShiftRepository.DEFAULT_FILE, StaffRepository.DEFAULT_FILE);
    }

    public StaffShiftService(String shiftFile, String staffFile) {
        this.shifts = new StaffShiftRepository(shiftFile);
        this.staffRepo = new StaffRepository(staffFile);
        this.auditLogs = new AuditLogService();
        this.notifications = new NotificationService();
    }

    public StaffShift assign(String staffId, LocalDate date, ShiftType type,
                             LocalTime start, LocalTime end, String department) {
        if (date == null || start == null || end == null || type == null) {
            throw new InvalidDataException("Date, times and shift type required.");
        }
        if (!start.isBefore(end)) {
            throw new InvalidDataException("Shift start must be before end.");
        }
        Staff staff = staffRepo.findById(staffId)
                .orElseThrow(() -> new InvalidDataException("Staff not found: " + staffId));
        ensureNoOverlap(staffId, date, start, end, null);
        StaffShift s = new StaffShift(shifts.nextId(), staffId, date, type, start, end);
        s.setStaffName(staff.getName());
        s.setDepartment(department);
        shifts.add(s);
        auditLogs.record("SYSTEM", AuditAction.SHIFT_ASSIGN, "StaffShift", s.getId(),
                "Assigned " + type + " to " + staffId);
        notifications.notify(com.hospital.enums.NotificationType.GENERAL,
                "Shift assigned", type + " on " + date + " for " + staff.getName(), s.getId());
        return s;
    }

    public StaffShift update(StaffShift shift) {
        if (shift == null || shift.getId() == null) {
            throw new InvalidDataException("Shift required.");
        }
        ensureNoOverlap(shift.getStaffId(), shift.getDate(),
                shift.getStartTime(), shift.getEndTime(), shift.getId());
        shifts.update(shift);
        return shift;
    }

    public StaffShift cancel(String id) {
        StaffShift s = require(id);
        if (!s.getStatus().canTransitionTo(ShiftStatus.CANCELLED)) {
            throw new ConflictException("Cannot cancel shift in status "
                    + s.getStatus() + ": " + id);
        }
        s.setStatus(ShiftStatus.CANCELLED);
        shifts.update(s);
        auditLogs.record("SYSTEM", AuditAction.SHIFT_CANCEL, "StaffShift", id,
                "Shift cancelled");
        return s;
    }

    public void ensureNoOverlap(String staffId, LocalDate date,
                                LocalTime start, LocalTime end, String excludeId) {
        for (StaffShift s : shifts.findByDate(date)) {
            if (s.getStatus() == ShiftStatus.CANCELLED) continue;
            if (excludeId != null && excludeId.equals(s.getId())) continue;
            if (staffId != null && staffId.equals(s.getStaffId())
                    && s.overlaps(date, start, end)) {
                throw new ConflictException("Overlapping shift for staff " + staffId
                        + " on " + date);
            }
        }
    }

    public List<StaffShift> getToday() { return shifts.findByDate(LocalDate.now()); }
    public List<StaffShift> getByStaff(String staffId) { return shifts.findByStaff(staffId); }
    public List<StaffShift> getByDepartment(String department) {
        return shifts.findByDepartment(department);
    }
    public List<StaffShift> getAll() { return shifts.findAll(); }
    public Optional<StaffShift> find(String id) { return shifts.findById(id); }
    public int getCount() { return shifts.count(); }
    public String nextId() { return shifts.nextId(); }

    public List<StaffShift> search(String query) {
        if (query == null || query.isBlank()) return shifts.findAll();
        String q = query.trim().toLowerCase();
        return shifts.findAll().stream()
                .filter(s -> (s.getId() != null && s.getId().toLowerCase().contains(q))
                        || (s.getStaffId() != null && s.getStaffId().toLowerCase().contains(q))
                        || (s.getStaffName() != null && s.getStaffName().toLowerCase().contains(q))
                        || (s.getDepartment() != null
                            && s.getDepartment().toLowerCase().contains(q)))
                .toList();
    }

    private StaffShift require(String id) {
        if (id == null || id.isBlank()) throw new InvalidDataException("Shift id required.");
        return shifts.findById(id)
                .orElseThrow(() -> new InvalidDataException("Shift not found: " + id));
    }
}
