package com.hospital.ui;

import com.hospital.exception.InvalidDataException;
import com.hospital.model.Patient;
import com.hospital.service.PatientService;

public class ConsolePatientOps {
    private final ConsoleHelpers helpers;
    private final PatientService patientService;

    ConsolePatientOps(ConsoleHelpers helpers, PatientService patientService) {
        this.helpers = helpers;
        this.patientService = patientService;
    }

    public void listPatients() {
        var patients = patientService.getAllPatients();
        if (patients.isEmpty()) {
            System.out.println("No patients registered yet.");
            return;
        }
        System.out.println("--- Patients (" + patients.size() + ") ---");
        System.out.printf("%-10s %-20s %-7s %-6s %-13s %s%n",
                "ID", "NAME", "GENDER", "BLOOD", "PHONE", "EMERGENCY CONTACT");
        for (Patient p : patients) {
            System.out.printf("%-10s %-20s %-7s %-6s %-13s %s%n",
                    ConsoleHelpers.text(p.getId()), ConsoleHelpers.text(p.getName()),
                    ConsoleHelpers.text(p.getGenderName()), ConsoleHelpers.text(p.getBloodGroup()),
                    ConsoleHelpers.text(p.getPhone()), ConsoleHelpers.text(p.getEmergencyContact()));
        }
    }

    public void addPatient() {
        String id = patientService.nextPatientId();
        Patient patient = new Patient(id, helpers.ask("Patient name: "), helpers.ask("Phone: "),
                helpers.ask("Gender (Male/Female/Other): "), helpers.ask("Blood group: "),
                helpers.ask("Emergency contact: "));
        patientService.registerPatient(patient);
        System.out.println("Saved " + id + " -> data/patients.json updated automatically.");
    }

    public void updatePatient() {
        String id = helpers.ask("Patient id to update: ");
        Patient patient = patientService.findPatientById(id)
                .orElseThrow(() -> new InvalidDataException("Patient not found: " + id));
        System.out.println("Leave a field empty to keep the current value.");
        patient.setName(helpers.askOrDefault("Name [" + ConsoleHelpers.text(patient.getName()) + "]: ",
                patient.getName()));
        patient.setPhone(helpers.askOrDefault("Phone [" + ConsoleHelpers.text(patient.getPhone()) + "]: ",
                patient.getPhone()));
        patient.setBloodGroup(helpers.askOrDefault(
                "Blood group [" + ConsoleHelpers.text(patient.getBloodGroup()) + "]: ", patient.getBloodGroup()));
        patient.setEmergencyContact(helpers.askOrDefault(
                "Emergency contact [" + ConsoleHelpers.text(patient.getEmergencyContact()) + "]: ",
                patient.getEmergencyContact()));
        patientService.updatePatient(patient);
        System.out.println("Updated " + patient.getId() + " -> data/patients.json updated automatically.");
    }

    public void deletePatient() {
        String id = helpers.ask("Patient id to delete: ");
        patientService.removePatient(id);
        System.out.println("Deleted " + id + " -> data/patients.json updated automatically.");
    }
}
