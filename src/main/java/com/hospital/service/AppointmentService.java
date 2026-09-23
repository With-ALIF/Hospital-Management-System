package com.hospital.service;

import com.hospital.enums.AppointmentStatus;
import com.hospital.exception.InvalidDataException;
import com.hospital.model.Appointment;
import com.hospital.model.Doctor;
import com.hospital.model.Patient;
import com.hospital.repository.AppointmentRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public class AppointmentService {
    private final AppointmentRepository repository;
    private final PatientService patientService;
    private final DoctorService doctorService;
    private final AppointmentTransitions transitions;

    public AppointmentService(PatientService p, DoctorService d) {
        this(p, d, AppointmentRepository.DEFAULT_FILE);
    }

    public AppointmentService(PatientService p, DoctorService d, String filePath) {
        if (p == null) throw new InvalidDataException("AppointmentService needs PatientService");
        if (d == null) throw new InvalidDataException("AppointmentService needs DoctorService");
        this.patientService = p;
        this.doctorService = d;
        this.repository = new AppointmentRepository(filePath);
        this.transitions = new AppointmentTransitions(repository);
    }

    public AppointmentService(String filePath) {
        this(new PatientService(), new DoctorService(), filePath);
    }

    public Appointment createAppointment(String patientId, String doctorId, LocalDate date,
                                         LocalTime time, String reason) {
        Patient patient = patientService.findPatientById(patientId)
                .orElseThrow(() -> new InvalidDataException("Patient not found. Appointment cannot be created."));
        Doctor doctor = doctorService.findDoctorById(doctorId)
                .orElseThrow(() -> new InvalidDataException("Doctor not found. Appointment cannot be created."));
        if (date == null) throw new InvalidDataException("Appointment date must be specified.");
        if (time == null) throw new InvalidDataException("Appointment time must be specified.");
        if (reason == null || reason.isBlank()) throw new InvalidDataException("Appointment reason cannot be empty.");
        AppointmentRules.validateDoctorBookable(doctor, time);
        AppointmentRules.ensureSlotFree(repository.findAll(), null, patient, doctor, date, time);
        Appointment a = new Appointment(repository.nextAppointmentId(), patient, doctor,
                date, time, reason, AppointmentStatus.SCHEDULED);
        a.setPatientId(patientId);
        a.setDoctorId(doctorId);
        a.setCreatedAt(LocalDateTime.now());
        repository.add(a);
        return a;
    }

    public Appointment createAppointment(Patient patient, Doctor doctor, LocalDate date,
                                         LocalTime time, String reason) {
        if (patient == null) throw new InvalidDataException("Patient not found. Appointment cannot be created.");
        if (doctor == null) throw new InvalidDataException("Doctor not found. Appointment cannot be created.");
        return createAppointment(patient.getId(), doctor.getId(), date, time, reason);
    }

    public Optional<Appointment> getAppointmentById(String id) { return repository.findById(id); }
    public List<Appointment> getAllAppointments() { return repository.findAll(); }
    public List<Appointment> getAppointmentsByPatient(String pid) { return repository.findByPatientId(pid); }
    public List<Appointment> getAppointmentsByDoctor(String did) { return repository.findByDoctorId(did); }
    public int getAppointmentCount() { return repository.count(); }
    public String nextAppointmentId() { return repository.nextAppointmentId(); }
    public List<Appointment> findByDate(LocalDate date) { return repository.findByDate(date); }

    public Appointment confirmAppointment(String id) { return transitions.transition(id, AppointmentStatus.CONFIRMED); }
    public Appointment cancelAppointment(String id) { return transitions.transition(id, AppointmentStatus.CANCELLED); }
    public Appointment updateStatus(String id, AppointmentStatus s) { return transitions.transition(id, s); }
    public Appointment completeAppointment(String id) { return transitions.complete(id); }

    public Appointment rescheduleAppointment(String id, LocalDate newDate, LocalTime newTime) {
        Appointment appointment = transitions.require(id);
        if (appointment.getStatus() == AppointmentStatus.COMPLETED
                || appointment.getStatus() == AppointmentStatus.CANCELLED) {
            throw new InvalidDataException("Cannot reschedule a " + appointment.getStatus() + " appointment.");
        }
        if (newDate == null || newTime == null) throw new InvalidDataException("New date and time must be specified.");
        Doctor doctor = doctorService.findDoctorById(appointment.getDoctorId())
                .orElseThrow(() -> new InvalidDataException("Doctor not found. Appointment cannot be created."));
        Patient patient = patientService.findPatientById(appointment.getPatientId()).orElse(null);
        AppointmentRules.validateDoctorBookable(doctor, newTime);
        AppointmentRules.ensureSlotFree(repository.findAll(), id, patient, doctor, newDate, newTime);
        appointment.setDate(newDate);
        appointment.setTime(newTime);
        repository.update(appointment);
        return appointment;
    }
}
