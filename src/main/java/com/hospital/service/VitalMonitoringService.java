package com.hospital.service;

import com.hospital.enums.VitalStatus;
import com.hospital.model.VitalRecord;
import com.hospital.repository.VitalRecordRepository;

import java.util.List;
import java.util.Optional;

public class VitalMonitoringService {
    private final VitalRecordRepository records;
    private final NotificationService notifications;
    private final AuditLogService auditLogs;
    private final EventBus eventBus;
    private final RiskScoreService riskScores;

    public VitalMonitoringService() {
        this(VitalRecordRepository.DEFAULT_FILE);
    }

    public VitalMonitoringService(String filePath) {
        this.records = new VitalRecordRepository(filePath);
        this.notifications = new NotificationService();
        this.auditLogs = new AuditLogService();
        this.eventBus = EventBus.getInstance();
        this.riskScores = new RiskScoreService();
    }

    public VitalRecord record(String patientId, double temperature, int heartRate,
                              String bloodPressure, int spo2, int respiratoryRate,
                              String recordedBy) {
        VitalRules.validate(patientId, temperature, heartRate, bloodPressure, spo2, respiratoryRate);
        VitalRecord v = new VitalRecord(records.nextId(), patientId, temperature,
                heartRate, bloodPressure, spo2, respiratoryRate, recordedBy);
        VitalStatus status = classify(temperature, heartRate, spo2, respiratoryRate);
        v.setOverallStatus(status);
        records.add(v);
        if (status == VitalStatus.WARNING || status == VitalStatus.CRITICAL) {
            VitalRules.handleAbnormal(v, status, patientId, notifications, auditLogs, riskScores);
        }
        return v;
    }

    public VitalRecord update(VitalRecord record) {
        if (record == null || record.getId() == null) {
            throw new com.hospital.exception.InvalidDataException("Vital record required.");
        }
        VitalRules.validate(record.getPatientId(), record.getTemperature(),
                record.getHeartRate(), record.getBloodPressure(),
                record.getSpo2(), record.getRespiratoryRate());
        VitalStatus status = classify(record.getTemperature(), record.getHeartRate(),
                record.getSpo2(), record.getRespiratoryRate());
        record.setOverallStatus(status);
        records.update(record);
        if (status == VitalStatus.WARNING || status == VitalStatus.CRITICAL) {
            VitalRules.handleAbnormal(record, status, record.getPatientId(),
                    notifications, auditLogs, riskScores);
        }
        return record;
    }

    public VitalRecord updateVitals(String recordId, double temperature, int heartRate,
                                    String bloodPressure, int spo2, int respiratoryRate) {
        VitalRecord r = require(recordId);
        r.setTemperature(temperature);
        r.setHeartRate(heartRate);
        r.setBloodPressure(bloodPressure);
        r.setSpo2(spo2);
        r.setRespiratoryRate(respiratoryRate);
        return update(r);
    }

    public VitalStatus classify(double temp, int hr, int spo2, int rr) {
        return VitalRules.classify(temp, hr, spo2, rr);
    }

    public VitalStatus classify(VitalRecord record) {
        if (record == null) return VitalStatus.NORMAL;
        return classify(record.getTemperature(), record.getHeartRate(),
                record.getSpo2(), record.getRespiratoryRate());
    }

    public List<VitalRecord> getHistory(String patientId) {
        return records.findByPatient(patientId);
    }

    public Optional<VitalRecord> getLatest(String patientId) {
        return records.findByPatient(patientId).stream()
                .filter(r -> r.getRecordedAt() != null)
                .max(java.util.Comparator.comparing(VitalRecord::getRecordedAt));
    }

    public List<VitalRecord> getCritical() { return records.findCritical(); }
    public List<VitalRecord> getWarnings() { return records.findWarning(); }
    public List<VitalRecord> getAll() { return records.findAll(); }
    public Optional<VitalRecord> find(String id) { return records.findById(id); }
    public int getCount() { return records.count(); }
    public String nextId() { return records.nextId(); }

    public List<VitalRecord> search(String query) {
        return VitalRules.search(records.findAll(), query);
    }

    private VitalRecord require(String id) {
        return VitalRules.require(records, id);
    }
}
