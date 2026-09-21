package com.hospital.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.hospital.exception.InvalidDataException;
import com.hospital.model.Appointment;
import com.hospital.model.AppointmentStatus;
import com.hospital.model.Doctor;
import com.hospital.model.Patient;
import com.hospital.repository.JsonRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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

    /**
     * Allows the data directory to be replaced, which is used by the unit tests
     * so that they never touch the real files inside {@code data/}.
     */
    public HospitalService(String dataDir) {
        String dir = (dataDir == null || dataDir.isBlank()) ? "data/" : dataDir;
        if (!dir.endsWith("/")) {
            dir = dir + "/";
        }
        patientRepository = new JsonRepository<>(dir + "patients.json", new TypeReference<List<Patient>>() {});
        doctorRepository = new JsonRepository<>(dir + "doctors.json", new TypeReference<List<Doctor>>() {});
        appointmentRepository = new JsonRepository<>(dir + "appointments.json", new TypeReference<List<Appointment>>() {});
        loadAllData();
    }

    public void loadAllData() {
        patients.addAll(patientRepository.loadAll());
        doctors.addAll(doctorRepository.loadAll());
        appointments.addAll(appointmentRepository.loadAll());
    }

    private void savePatients() {
        patientRepository.saveAll(patients);
    }

    private void saveDoctors() {
        doctorRepository.saveAll(doctors);
    }

    private void saveAppointments() {
        appointmentRepository.saveAll(appointments);
    }

    public void addPatient(Patient patient){
        if (patient.getName() == null ||  patient.getName().isBlank()) {
            throw new InvalidDataException("Patient name cannot be empty.");
        }
        patients.add(patient);
        savePatients();
    }   

    public void addDoctor(Doctor doctor){
        if (doctor.getName() == null ||  doctor.getName().isBlank()) {
            throw new InvalidDataException("Doctor name cannot be empty.");
        }
        doctors.add(doctor);
        saveDoctors();
    } 

    public void bookAppointment(Appointment appointment) {
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

        Doctor doc = appointment.getDoctor();

        // 1. Check general availability
        if (!doc.getAvailable()) {
            throw new InvalidDataException("Doctor " + doc.getName() + " is currently marked as unavailable.");
        }

        // 2. Check doctor duty hours / schedule
        if (!doc.isAvailableAt(appointment.getTime())) {
            throw new InvalidDataException("Appointment time " + appointment.getFormattedTime()
                    + " is outside Doctor " + doc.getName() + "'s duty hours. Available schedule: "
                    + doc.getDutyScheduleString());
        }

        // 3. Check for appointment ID uniqueness
        boolean idExists = appointments.stream()
                .anyMatch(a -> a.getId().equalsIgnoreCase(appointment.getId()));
        if (idExists) {
            throw new InvalidDataException("Appointment ID " + appointment.getId() + " already exists.");
        }

        // 4. Check for doctor double-booking at the exact same date & time
        boolean doctorSlotOccupied = appointments.stream()
                .anyMatch(a -> a.getDoctor().getId().equalsIgnoreCase(doc.getId())
                        && a.getDate().equals(appointment.getDate())
                        && a.getTime().equals(appointment.getTime())
                        && a.getStatus() != AppointmentStatus.CANCELLED);
        if (doctorSlotOccupied) {
            throw new InvalidDataException("Doctor " + doc.getName() + " already has an appointment at "
                    + appointment.getFormattedTime() + " on " + appointment.getDate() + ".");
        }

        // 5. Check for patient double-booking at the exact same date & time
        Patient pat = appointment.getPatient();
        boolean patientSlotOccupied = appointments.stream()
                .anyMatch(a -> a.getPatient().getId().equalsIgnoreCase(pat.getId())
                        && a.getDate().equals(appointment.getDate())
                        && a.getTime().equals(appointment.getTime())
                        && a.getStatus() != AppointmentStatus.CANCELLED);
        if (patientSlotOccupied) {
            throw new InvalidDataException("Patient " + pat.getName() + " already has an appointment at "
                    + appointment.getFormattedTime() + " on " + appointment.getDate() + ".");
        }

        appointments.add(appointment);
        saveAppointments();
    }

    public void updateAppointmentStatus(String appointmentId, AppointmentStatus newStatus) {
        if (newStatus == null) {
            throw new InvalidDataException("Status cannot be null.");
        }
        Appointment appointment = appointments.stream()
                .filter(a -> a.getId().equalsIgnoreCase(appointmentId))
                .findFirst()
                .orElseThrow(() -> new InvalidDataException("Appointment not found: " + appointmentId));
        appointment.setStatus(newStatus);
        saveAppointments();
    }

    public void cancelAppointment(String appointmentId) {
        Appointment appointment = appointments.stream()
                .filter(a -> a.getId().equalsIgnoreCase(appointmentId))
                .findFirst()
                .orElseThrow(() -> new InvalidDataException("Appointment not found: " + appointmentId));
        
        if (appointment.getStatus() == AppointmentStatus.CANCELLED) {
            throw new InvalidDataException("Appointment " + appointmentId + " is already cancelled.");
        }
        
        appointment.setStatus(AppointmentStatus.CANCELLED);
        saveAppointments();
    }

    public void rescheduleAppointment(String appointmentId, java.time.LocalDate newDate, java.time.LocalTime newTime) {
        Appointment appointment = appointments.stream()
                .filter(a -> a.getId().equalsIgnoreCase(appointmentId))
                .findFirst()
                .orElseThrow(() -> new InvalidDataException("Appointment not found: " + appointmentId));

        if (appointment.getStatus() == AppointmentStatus.CANCELLED) {
            throw new InvalidDataException("Cannot reschedule a cancelled appointment.");
        }

        if (newDate == null || newTime == null) {
            throw new InvalidDataException("New date and time must be specified.");
        }

        Doctor doc = appointment.getDoctor();

        // 1. Check doctor availability
        if (!doc.getAvailable()) {
            throw new InvalidDataException("Doctor " + doc.getName() + " is currently unavailable.");
        }

        // 2. Check doctor duty hours
        if (!doc.isAvailableAt(newTime)) {
            throw new InvalidDataException("New time " + newTime
                    + " is outside Doctor " + doc.getName() + "'s duty hours. Available schedule: "
                    + doc.getDutyScheduleString());
        }

        // 3. Check doctor conflict (excluding current appointment)
        boolean doctorConflict = appointments.stream()
                .anyMatch(a -> !a.getId().equalsIgnoreCase(appointmentId)
                        && a.getDoctor().getId().equalsIgnoreCase(doc.getId())
                        && a.getDate().equals(newDate)
                        && a.getTime().equals(newTime)
                        && a.getStatus() != AppointmentStatus.CANCELLED);
        if (doctorConflict) {
            throw new InvalidDataException("Doctor " + doc.getName() + " already has an appointment at "
                    + newTime + " on " + newDate + ".");
        }

        // 4. Check patient conflict (excluding current appointment)
        Patient pat = appointment.getPatient();
        boolean patientConflict = appointments.stream()
                .anyMatch(a -> !a.getId().equalsIgnoreCase(appointmentId)
                        && a.getPatient().getId().equalsIgnoreCase(pat.getId())
                        && a.getDate().equals(newDate)
                        && a.getTime().equals(newTime)
                        && a.getStatus() != AppointmentStatus.CANCELLED);
        if (patientConflict) {
            throw new InvalidDataException("Patient " + pat.getName() + " already has an appointment at "
                    + newTime + " on " + newDate + ".");
        }

        // All checks passed - update the appointment
        appointment.setDate(newDate);
        appointment.setTime(newTime);
        saveAppointments();
    }

    public void showDoctors(){
        System.out.println("=== Doctor List ===");
        for (Doctor doctor : doctors) {
            doctor.displayInfo();
        }
    }

    public void showPatients(){
        System.out.println("=== Patient List ===");
        for (Patient patient : patients) {
            patient.displayInfo();
        }
    }

    public void showAppointments(){
        System.out.println("=== Appointment List ===");
        for (Appointment appointment : appointments) {
            appointment.displayInfo();
        }
    }

    public List<Doctor> getDoctors() {
        return Collections.unmodifiableList(doctors);
    }

    public List<Patient> getPatients() {
        return Collections.unmodifiableList(patients);
    }

    public List<Appointment> getAppointments() {
        return Collections.unmodifiableList(appointments);
    }

    public List<Appointment> getPatientHistory(String patientId) {
        return appointments.stream()
                .filter(a -> a.getPatient().getId().equalsIgnoreCase(patientId))
                .collect(Collectors.toList());
    }

    public List<Appointment> getDoctorHistory(String doctorId) {
        return appointments.stream()
                .filter(a -> a.getDoctor().getId().equalsIgnoreCase(doctorId))
                .collect(Collectors.toList());
    }
}
