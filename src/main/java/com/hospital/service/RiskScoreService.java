package com.hospital.service;

import com.hospital.enums.RiskLevel;
import com.hospital.enums.VitalStatus;
import com.hospital.model.Patient;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RiskScoreService {
    private static final Map<String, Integer> VITAL_POINTS = new ConcurrentHashMap<>();

    public static final class Score {
        public final int points;
        public final RiskLevel level;
        public final String summary;

        Score(int points, RiskLevel level, String summary) {
            this.points = points;
            this.level = level;
            this.summary = summary;
        }
    }

    public Score scorePatient(Patient patient, int activeEmergencies,
                              int pendingAppointments, boolean admitted) {
        if (patient == null) {
            return new Score(0, RiskLevel.LOW, "No patient data");
        }
        int points = 0;
        int age = patient.getAge();
        if (age >= 65) points += 25;
        else if (age >= 45) points += 10;
        points += Math.min(30, activeEmergencies * 15);
        points += Math.min(20, pendingAppointments * 5);
        if (admitted) points += 20;
        points += VITAL_POINTS.getOrDefault(patient.getId(), 0);
        RiskLevel level = points >= 60 ? RiskLevel.HIGH
                : points >= 30 ? RiskLevel.MEDIUM : RiskLevel.LOW;
        String summary = "Age " + age + ", emergencies " + activeEmergencies
                + ", pending " + pendingAppointments + (admitted ? ", admitted" : "");
        return new Score(points, level, summary);
    }

    public void updateFromVitals(String patientId, int spo2, int heartRate,
                                 double temperature, VitalStatus status) {
        if (patientId == null || patientId.isBlank()) return;
        int bonus = 0;
        if (status == VitalStatus.CRITICAL) bonus = 40;
        else if (status == VitalStatus.WARNING) bonus = 20;
        if (spo2 < 90) bonus += 15;
        if (heartRate > 120 || heartRate < 45) bonus += 10;
        if (temperature > 39 || temperature < 35) bonus += 10;
        int current = VITAL_POINTS.getOrDefault(patientId, 0);
        VITAL_POINTS.put(patientId, Math.min(50, Math.max(current, bonus)));
    }

    public int getVitalPoints(String patientId) {
        return VITAL_POINTS.getOrDefault(patientId, 0);
    }

    public Score scoreFor(String patientId) {
        return new Score(VITAL_POINTS.getOrDefault(patientId, 0),
                VITAL_POINTS.getOrDefault(patientId, 0) >= 30
                        ? RiskLevel.HIGH : RiskLevel.LOW,
                "Vital-derived points");
    }
}
