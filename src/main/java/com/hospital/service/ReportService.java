package com.hospital.service;

import com.hospital.enums.EmergencyCaseStatus;
import com.hospital.model.Appointment;
import com.hospital.model.Doctor;
import com.hospital.model.EmergencyCase;
import com.hospital.model.Patient;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ReportService {
    public static final class Summary {
        public final Map<String, Integer> appointmentByStatus = new LinkedHashMap<>();
        public final Map<String, Integer> emergencyByPriority = new LinkedHashMap<>();
        public final Map<String, Integer> emergencyByStatus = new LinkedHashMap<>();
        public int totalPatients;
        public int totalDoctors;
        public int availableDoctors;
        public int appointmentsToday;
        public int emergencyWaiting;
        public int emergencyCritical;
    }

    public Summary summarize(List<Patient> patients, List<Doctor> doctors,
                             List<Appointment> appointments, List<EmergencyCase> emergencies) {
        Summary s = new Summary();
        s.totalPatients = patients.size();
        s.totalDoctors = doctors.size();
        s.availableDoctors = (int) doctors.stream().filter(Doctor::getAvailable).count();
        LocalDate today = LocalDate.now();
        s.appointmentsToday = (int) appointments.stream()
                .filter(a -> today.equals(a.getDate())).count();
        appointments.forEach(a -> s.appointmentByStatus.merge(
                a.getStatus() != null ? a.getStatus().name() : "UNKNOWN", 1, Integer::sum));
        emergencies.forEach(e -> {
            s.emergencyByPriority.merge(String.valueOf(e.getPriority()), 1, Integer::sum);
            s.emergencyByStatus.merge(
                    e.getStatus() != null ? e.getStatus().name() : "UNKNOWN", 1, Integer::sum);
            if (e.getStatus() == EmergencyCaseStatus.WAITING) s.emergencyWaiting++;
            if ("CRITICAL".equalsIgnoreCase(String.valueOf(e.getPriority()))
                    && e.getStatus() == EmergencyCaseStatus.WAITING) s.emergencyCritical++;
        });
        return s;
    }
}
