package com.hospital.ui;

import com.hospital.model.*;
import com.hospital.service.HospitalService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class AppState {
    public final HospitalService service = new HospitalService();
    public final ObservableList<Doctor> doctors = FXCollections.observableArrayList();
    public final ObservableList<Patient> patients = FXCollections.observableArrayList();
    public final ObservableList<Appointment> appointments = FXCollections.observableArrayList();
    public Runnable onMetricsChanged;

    public AppState() {
        loadDataFromService();
    }

    public void notifyChange() {
        if (onMetricsChanged != null) onMetricsChanged.run();
    }

    private void loadDataFromService() {
        System.out.println("Loading hospital data...");
        doctors.addAll(service.getDoctors());
        patients.addAll(service.getPatients());
        appointments.addAll(service.getAppointments());
        System.out.println("Patients loaded: " + patients.size());
        System.out.println("Doctors loaded: " + doctors.size());
        System.out.println("Appointments loaded: " + appointments.size());
        System.out.println("Hospital data synchronized.");
    }

    public void refreshFromService() {
        doctors.clear();
        patients.clear();
        appointments.clear();
        loadDataFromService();
    }
}
