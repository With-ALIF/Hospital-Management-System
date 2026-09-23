package com.hospital.service;

import com.hospital.enums.NotificationType;
import com.hospital.enums.VitalStatus;
import com.hospital.exception.InvalidDataException;
import com.hospital.model.VitalRecord;
import com.hospital.repository.VitalRecordRepository;

final class VitalRules {
    private VitalRules() {}

    static void validate(String patientId, double temp, int hr,
                         String bp, int spo2, int rr) {
        if (patientId == null || patientId.isBlank()) {
            throw new InvalidDataException("Patient id required for vitals.");
        }
        if (temp <= 0 || hr <= 0 || spo2 <= 0 || rr <= 0) {
            throw new InvalidDataException("Vital values must be positive.");
        }
        if (bp == null || bp.isBlank()) {
            throw new InvalidDataException("Blood pressure required.");
        }
        if (spo2 > 100) throw new InvalidDataException("SpO2 cannot exceed 100.");
        if (hr > 300) throw new InvalidDataException("Heart rate out of range.");
    }

    static VitalStatus classify(double temp, int hr, int spo2, int rr) {
        if (spo2 < 90 || temp > 40 || temp < 34 || rr > 30 || rr < 8 || hr > 140 || hr < 40) {
            return VitalStatus.CRITICAL;
        }
        if (spo2 < 95 || temp > 38.5 || temp < 36 || rr > 24 || rr < 10 || hr > 110 || hr < 50) {
            return VitalStatus.WARNING;
        }
        return VitalStatus.NORMAL;
    }

    static void handleAbnormal(VitalRecord v, VitalStatus status, String patientId,
                               NotificationService notifications, AuditLogService audit,
                               RiskScoreService riskScores) {
        NotificationType type = status == VitalStatus.CRITICAL
                ? NotificationType.EMERGENCY_ALERT : NotificationType.GENERAL;
        String title = status == VitalStatus.CRITICAL
                ? "CRITICAL vital alert" : "Abnormal vital warning";
        String msg = "Patient " + patientId + " SpO2=" + v.getSpo2()
                + " HR=" + v.getHeartRate() + " temp=" + v.getTemperature();
        notifications.notify(type, title, msg, v.getId());
        audit.record(v.getRecordedBy() != null ? v.getRecordedBy() : "SYSTEM",
                status == VitalStatus.CRITICAL
                        ? com.hospital.enums.AuditAction.VITAL_CRITICAL
                        : com.hospital.enums.AuditAction.VITAL_WARNING,
                "VitalRecord", v.getId(), msg);
        try {
            riskScores.updateFromVitals(patientId, v.getSpo2(), v.getHeartRate(),
                    v.getTemperature(), status);
        } catch (Exception ignored) {
        }
    }

    static VitalRecord require(VitalRecordRepository records, String id) {
        if (id == null || id.isBlank()) throw new InvalidDataException("Vital id required.");
        return records.findById(id)
                .orElseThrow(() -> new InvalidDataException("Vital record not found: " + id));
    }

    static java.util.List<VitalRecord> search(java.util.List<VitalRecord> all, String query) {
        if (query == null || query.isBlank()) return all;
        String q = query.trim().toLowerCase();
        return all.stream()
                .filter(v -> (v.getId() != null && v.getId().toLowerCase().contains(q))
                        || (v.getPatientId() != null && v.getPatientId().toLowerCase().contains(q))
                        || (v.getRecordedBy() != null && v.getRecordedBy().toLowerCase().contains(q)))
                .toList();
    }
}
