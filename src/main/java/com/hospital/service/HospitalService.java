package com.hospital.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.hospital.enums.AppointmentStatus;
import com.hospital.exception.InvalidDataException;
import com.hospital.model.Appointment;
import com.hospital.model.Doctor;
import com.hospital.model.Patient;
import com.hospital.repository.JsonRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HospitalService {
    private final List<Patient> patients = new ArrayList<>();
    private final List<Doctor> doctors = new ArrayList<>();
    private final List<Appointment> appointments = new ArrayList<>();
    private final JsonRepository<Patient> patientRepository;
    private final JsonRepository<Doctor> doctorRepository;
    private final JsonRepository<Appointment> appointmentRepository;

    public HospitalService() {
        this("data/");
    }

    public HospitalService(String dataDir) {
        String dir = (dataDir == null || dataDir.isBlank()) ? "data/" : dataDir;
        if (!dir.endsWith("/")) dir = dir + "/";
        patientRepository = new JsonRepository<>(dir + "patients.json", new TypeReference<List<Patient>>() {});
        doctorRepository = new JsonRepository<>(dir + "doctors.json", new TypeReference<List<Doctor>>() {});
        appointmentRepository = new JsonRepository<>(dir + "appointments.json", new TypeReference<List<Appointment>>() {});
        loadAllData();
    }

    public void loadAllData() {
        patients.clear();
        doctors.clear();
        appointments.clear();
        patients.addAll(patientRepository.loadAll());
        doctors.addAll(doctorRepository.loadAll());
        appointments.addAll(appointmentRepository.loadAll());
    }

    private void savePatients() { patientRepository.saveAll(patients); }
    private void saveDoctors() { doctorRepository.saveAll(doctors); }
    private void saveAppointments() { appointmentRepository.saveAll(appointments); }

    public void addPatient(Patient patient) {
        if (patient == null || patient.getName() == null || patient.getName().isBlank()) {
            throw new InvalidDataException("Patient name cannot be empty.");
        }
        patients.add(patient);
        savePatients();
    }

    public void addDoctor(Doctor doctor) {
        if (doctor == null || doctor.getName() == null || doctor.getName().isBlank()) {
            throw new InvalidDataException("Doctor name cannot be empty.");
        }
        doctors.add(doctor);
        saveDoctors();
    }

    public void bookAppointment(Appointment appointment) {
        AppointmentRules.validateBookingInput(appointment);
        AppointmentRules.validateDoctorBookable(appointment.getDoctor(), appointment.getTime());
        AppointmentRules.ensureNoDuplicateId(appointments, appointment.getId());
        AppointmentRules.ensureSlotFree(appointments, null,
                appointment.getPatient(), appointment.getDoctor(),
                appointment.getDate(), appointment.getTime());
        appointments.add(appointment);
        saveAppointments();
    }

    public void updateAppointmentStatus(String appointmentId, AppointmentStatus newStatus) {
        if (newStatus == null) throw new InvalidDataException("Status cannot be null.");
        requireAppointment(appointmentId).setStatus(newStatus);
        saveAppointments();
    }

    public void cancelAppointment(String appointmentId) {
        Appointment appointment = requireAppointment(appointmentId);
        if (appointment.getStatus() == AppointmentStatus.CANCELLED) {
            throw new InvalidDataException("Appointment " + appointmentId + " is already cancelled.");
        }
        appointment.setStatus(AppointmentStatus.CANCELLED);
        saveAppointments();
    }

    public void rescheduleAppointment(String appointmentId, java.time.LocalDate newDate,
                                      java.time.LocalTime newTime) {
        Appointment appointment = requireAppointment(appointmentId);
        if (appointment.getStatus() == AppointmentStatus.CANCELLED) {
            throw new InvalidDataException("Cannot reschedule a cancelled appointment.");
        }
        if (newDate == null || newTime == null) {
            throw new InvalidDataException("New date and time must be specified.");
        }
        AppointmentRules.validateDoctorBookable(appointment.getDoctor(), newTime);
        AppointmentRules.ensureSlotFree(appointments, appointmentId,
                appointment.getPatient(), appointment.getDoctor(), newDate, newTime);
        appointment.setDate(newDate);
        appointment.setTime(newTime);
        saveAppointments();
    }

    private Appointment requireAppointment(String appointmentId) {
        return appointments.stream()
                .filter(a -> a.getId() != null && a.getId().equalsIgnoreCase(appointmentId))
                .findFirst()
                .orElseThrow(() -> new InvalidDataException("Appointment not found: " + appointmentId));
    }

    public List<Doctor> getDoctors() { return Collections.unmodifiableList(doctors); }
    public List<Patient> getPatients() { return Collections.unmodifiableList(patients); }
    public List<Appointment> getAppointments() { return Collections.unmodifiableList(appointments); }
}
