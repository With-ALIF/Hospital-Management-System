package com.hospital.service;

import com.hospital.exception.InvalidDataException;
import com.hospital.model.Appointment;
import com.hospital.model.Doctor;
import com.hospital.model.Patient;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public final class AppointmentRules {

    private AppointmentRules() {
    }

    public static void validateBookingInput(Appointment appointment) {
        if (appointment == null) {
            throw new InvalidDataException("Appointment cannot be null.");
        }
        if (appointment.getId() == null || appointment.getId().isBlank()) {
            throw new InvalidDataException("Appointment ID cannot be empty.");
        }
        if (appointment.getPatient() == null) {
            throw new InvalidDataException("Patient must be selected.");
        }
        if (appointment.getDoctor() == null) {
            throw new InvalidDataException("Doctor must be selected.");
        }
        if (appointment.getDate() == null) {
            throw new InvalidDataException("Appointment date must be specified.");
        }
        if (appointment.getTime() == null) {
            throw new InvalidDataException("Appointment time must be specified.");
        }
    }

    public static void validateDoctorBookable(Doctor doc, LocalTime time) {
        if (doc == null) {
            throw new InvalidDataException("Doctor must be selected.");
        }
        if (!doc.getAvailable()) {
            throw new InvalidDataException(
                    "Doctor " + doc.getName() + " is currently marked as unavailable.");
        }
        if (time != null && !doc.isAvailableAt(time)) {
            throw new InvalidDataException("Appointment time " + time
                    + " is outside Doctor " + doc.getName() + "'s duty hours. Available schedule: "
                    + doc.getDutyScheduleString());
        }
    }

    public static void ensureNoDuplicateId(List<Appointment> existing, String id) {
        boolean idExists = existing.stream()
                .anyMatch(a -> a.getId() != null && a.getId().equalsIgnoreCase(id));
        if (idExists) {
            throw new InvalidDataException("Appointment ID " + id + " already exists.");
        }
    }

    public static void ensureSlotFree(List<Appointment> existing, String appointmentIdToSkip,
                                      Patient patient, Doctor doctor, LocalDate date, LocalTime time) {
        boolean conflict = existing.stream()
                .anyMatch(a -> (appointmentIdToSkip == null
                                || !a.getId().equalsIgnoreCase(appointmentIdToSkip))
                        && date.equals(a.getDate())
                        && time.equals(a.getTime())
                        && a.getStatus() != com.hospital.enums.AppointmentStatus.CANCELLED
                        && ((doctor != null && a.getDoctor() != null
                                && a.getDoctor().getId().equalsIgnoreCase(doctor.getId()))
                            || (patient != null && a.getPatient() != null
                                && a.getPatient().getId().equalsIgnoreCase(patient.getId()))));
        if (conflict) {
            String who = doctor != null ? "Doctor " + doctor.getName() : "Patient " + patient.getName();
            throw new InvalidDataException(who + " already has an appointment at "
                    + time + " on " + date + ".");
        }
    }
}
