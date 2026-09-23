package com.hospital.service;

import com.hospital.enums.WorkloadLevel;
import com.hospital.model.Appointment;
import com.hospital.model.Doctor;

import java.time.LocalDate;
import java.util.List;

public class WorkloadService {
    public static final class DoctorWorkload {
        public final String doctorId;
        public final String doctorName;
        public final int appointmentCount;
        public final WorkloadLevel level;

        DoctorWorkload(String doctorId, String doctorName, int appointmentCount, WorkloadLevel level) {
            this.doctorId = doctorId;
            this.doctorName = doctorName;
            this.appointmentCount = appointmentCount;
            this.level = level;
        }
    }

    public List<DoctorWorkload> forDoctors(List<Doctor> doctors, List<Appointment> appointments) {
        LocalDate today = LocalDate.now();
        return doctors.stream()
                .map(d -> {
                    int count = (int) appointments.stream()
                            .filter(a -> d.getId().equals(a.getDoctorId()))
                            .filter(a -> a.getDate() != null && !a.getDate().isBefore(today))
                            .filter(a -> a.getStatus() != com.hospital.enums.AppointmentStatus.CANCELLED)
                            .count();
                    return new DoctorWorkload(d.getId(), d.getName(), count, levelFor(count));
                })
                .sorted((a, b) -> Integer.compare(b.appointmentCount, a.appointmentCount))
                .toList();
    }

    public WorkloadLevel levelFor(int appointmentCount) {
        if (appointmentCount >= 8) return WorkloadLevel.HIGH;
        if (appointmentCount >= 4) return WorkloadLevel.MEDIUM;
        return WorkloadLevel.LOW;
    }
}
