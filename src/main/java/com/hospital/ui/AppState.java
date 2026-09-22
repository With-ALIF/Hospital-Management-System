package com.hospital.ui;

import com.hospital.model.*;
import com.hospital.service.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class AppState {
    // Legacy unified service (kept for backward compat)
    public final HospitalService service = new HospitalService();

    // Day3/Day4/Day5 layered services — singletons backed by same JSON files
    public final PatientService patientService = new PatientService();
    public final DoctorService doctorService = new DoctorService();
    public final EmergencyService emergencyService = new EmergencyService(patientService, doctorService);
    public final AppointmentService appointmentService = new AppointmentService(patientService, doctorService);

    public final ObservableList<Doctor> doctors = FXCollections.observableArrayList();
    public final ObservableList<Patient> patients = FXCollections.observableArrayList();
    public final ObservableList<Appointment> appointments = FXCollections.observableArrayList();
    public final ObservableList<EmergencyCase> emergencyCases = FXCollections.observableArrayList();

    public Runnable onMetricsChanged;

    public AppState() {
        loadDataFromService();
    }

    public void notifyChange() {
        if (onMetricsChanged != null) onMetricsChanged.run();
    }

    private void loadDataFromService() {
        System.out.println("Loading hospital data...");
        // legacy HospitalService is source of truth for patients/doctors/appointments UI lists
        // layered services read same files, so keep them in sync
        doctors.addAll(service.getDoctors());
        patients.addAll(service.getPatients());
        appointments.addAll(service.getAppointments());
        try { emergencyCases.addAll(emergencyService.getAllCases()); } catch (Exception ignored) {}
        System.out.println("Patients loaded: " + patients.size());
        System.out.println("Doctors loaded: " + doctors.size());
        System.out.println("Appointments loaded: " + appointments.size());
        System.out.println("Emergency cases loaded: " + emergencyCases.size());
        System.out.println("Hospital data synchronized.");
    }

    public void refreshFromService() {
        service.loadAllData();
        doctors.setAll(service.getDoctors());
        patients.setAll(service.getPatients());
        appointments.setAll(service.getAppointments());
        try { emergencyCases.setAll(emergencyService.getAllCases()); } catch (Exception ignored) {}
        notifyChange();
    }

    public void refreshEmergency() {
        try { emergencyCases.setAll(emergencyService.getAllCases()); } catch (Exception ignored) {}
        notifyChange();
    }
    public void refreshAppointments() {
        try { appointments.setAll(appointmentService.getAllAppointments()); } catch (Exception ignored) {}
        // also keep legacy service in sync for any old views
        service.loadAllData();
        notifyChange();
    }
}
