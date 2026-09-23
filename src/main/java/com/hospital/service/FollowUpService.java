package com.hospital.service;

import com.hospital.enums.HospitalEventType;
import com.hospital.enums.NotificationType;
import com.hospital.exception.ConflictException;
import com.hospital.exception.InvalidDataException;
import com.hospital.model.Discharge;
import com.hospital.model.FollowUp;
import com.hospital.enums.FollowUpStatus;
import com.hospital.repository.DischargeRepository;
import com.hospital.repository.FollowUpRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class FollowUpService {
    private final FollowUpRepository followUps;
    private final DischargeRepository discharges;
    private final NotificationService notifications;
    private final AuditLogService auditLogs;
    private final EventBus eventBus;

    public FollowUpService() {
        this(FollowUpRepository.DEFAULT_FILE, DischargeRepository.DEFAULT_FILE);
    }

    public FollowUpService(String followUpFile, String dischargeFile) {
        this.followUps = new FollowUpRepository(followUpFile);
        this.discharges = new DischargeRepository(dischargeFile);
        this.notifications = new NotificationService();
        this.auditLogs = new AuditLogService();
        this.eventBus = EventBus.getInstance();
    }

    public FollowUp schedule(String patientId, String doctorId, LocalDate date,
                             String reason, String instructions) {
        if (patientId == null || patientId.isBlank()) {
            throw new InvalidDataException("Patient required for follow-up.");
        }
        if (date == null) {
            throw new InvalidDataException("Follow-up date required.");
        }
        if (date.isBefore(LocalDate.now())) {
            throw new InvalidDataException("Follow-up date cannot be in the past.");
        }
        FollowUp f = new FollowUp(followUps.nextId(), patientId, doctorId,
                date, reason, instructions);
        followUps.add(f);
        notifications.notify(NotificationType.APPOINTMENT_REMINDER,
                "Follow-up scheduled", patientId + " on " + date, f.getId());
        auditLogs.record("SYSTEM", com.hospital.enums.AuditAction.FOLLOW_UP_SCHEDULE,
                "FollowUp", f.getId(), "Scheduled for " + date);
        eventBus.publish(HospitalEventType.FOLLOW_UP_CREATED, f.getId(),
                "FollowUp", "Follow-up created for " + patientId);
        return f;
    }

    public FollowUp scheduleFromDischarge(String dischargeId) {
        Discharge d = discharges.findById(dischargeId)
                .orElseThrow(() -> new InvalidDataException("Discharge not found: " + dischargeId));
        FollowUp f = FollowUpMutations.fromDischarge(d, followUps.nextId(), followUps);
        followUps.add(f);
        eventBus.publish(HospitalEventType.FOLLOW_UP_CREATED, f.getId(),
                "FollowUp", "Follow-up from discharge " + dischargeId);
        return f;
    }

    public FollowUp reschedule(String id, LocalDate newDate) {
        return FollowUpMutations.reschedule(require(id), newDate, followUps::update);
    }

    public FollowUp complete(String id) {
        return FollowUpMutations.transition(require(id), FollowUpStatus.COMPLETED,
                "completed", followUps::update, auditLogs, eventBus);
    }

    public FollowUp cancel(String id) {
        return FollowUpMutations.transition(require(id), FollowUpStatus.CANCELLED,
                "cancelled", followUps::update, null, eventBus);
    }

    public FollowUp markMissed(String id) {
        return FollowUpMutations.transition(require(id), FollowUpStatus.MISSED,
                "marked missed", followUps::update, null, eventBus);
    }

    public List<FollowUp> getUpcoming() { return followUps.findUpcoming(); }
    public List<FollowUp> getToday() { return followUps.findByDate(LocalDate.now()); }
    public List<FollowUp> getByPatient(String patientId) {
        return followUps.findByPatient(patientId);
    }
    public List<FollowUp> getByDoctor(String doctorId) {
        return followUps.findByDoctor(doctorId);
    }
    public List<FollowUp> getAll() { return followUps.findAll(); }
    public Optional<FollowUp> find(String id) { return followUps.findById(id); }
    public int getCount() { return followUps.count(); }
    public String nextId() { return followUps.nextId(); }

    public List<FollowUp> search(String query) {
        if (query == null || query.isBlank()) return followUps.findAll();
        String q = query.trim().toLowerCase();
        return followUps.findAll().stream()
                .filter(f -> (f.getId() != null && f.getId().toLowerCase().contains(q))
                        || (f.getPatientId() != null && f.getPatientId().toLowerCase().contains(q))
                        || (f.getDoctorId() != null && f.getDoctorId().toLowerCase().contains(q))
                        || (f.getReason() != null && f.getReason().toLowerCase().contains(q)))
                .toList();
    }

    private FollowUp require(String id) {
        if (id == null || id.isBlank()) throw new InvalidDataException("Follow-up id required.");
        return followUps.findById(id)
                .orElseThrow(() -> new InvalidDataException("Follow-up not found: " + id));
    }
}
