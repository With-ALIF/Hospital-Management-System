package com.hospital.service;

import com.hospital.enums.HospitalEventType;
import com.hospital.exception.ConflictException;
import com.hospital.exception.InvalidDataException;
import com.hospital.model.Discharge;
import com.hospital.model.FollowUp;
import com.hospital.enums.FollowUpStatus;
import com.hospital.repository.FollowUpRepository;

import java.time.LocalDate;

final class FollowUpMutations {
    private FollowUpMutations() {}

    static FollowUp reschedule(FollowUp f, LocalDate newDate,
                               java.util.function.Consumer<FollowUp> save) {
        if (f.getStatus() != FollowUpStatus.SCHEDULED) {
            throw new ConflictException("Only scheduled follow-ups can be rescheduled: " + f.getId());
        }
        if (newDate == null || newDate.isBefore(LocalDate.now())) {
            throw new InvalidDataException("New date must be today or future.");
        }
        f.setFollowUpDate(newDate);
        save.accept(f);
        return f;
    }

    static FollowUp transition(FollowUp f, FollowUpStatus next, String verb,
                               java.util.function.Consumer<FollowUp> save,
                               AuditLogService audit, EventBus bus) {
        if (!f.getStatus().canTransitionTo(next)) {
            throw new ConflictException("Cannot " + verb + " follow-up in status "
                    + f.getStatus() + ": " + f.getId());
        }
        f.setStatus(next);
        save.accept(f);
        if (audit != null) {
            audit.record("SYSTEM", com.hospital.enums.AuditAction.FOLLOW_UP_COMPLETE,
                    "FollowUp", f.getId(), "Follow-up " + verb);
        }
        if (bus != null) {
            bus.publish(HospitalEventType.FOLLOW_UP_CREATED, f.getId(),
                    "FollowUp", "Follow-up " + verb);
        }
        return f;
    }

    static FollowUp fromDischarge(Discharge d, String id, FollowUpRepository repo) {
        if (d.getFollowUpDate() == null) {
            throw new InvalidDataException("Discharge has no follow-up date: " + d.getId());
        }
        if (d.getFollowUpDate().isBefore(LocalDate.now())) {
            throw new InvalidDataException("Discharge follow-up date is in the past.");
        }
        FollowUp f = new FollowUp(id, d.getPatientId(), null,
                d.getFollowUpDate(), "Post-discharge follow-up",
                d.getFinalNotes() != null ? d.getFinalNotes() : "Review after discharge");
        f.setDischargeId(d.getId());
        f.setOriginalVisitId(d.getId());
        return f;
    }
}
