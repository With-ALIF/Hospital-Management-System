package com.hospital.ui;

import com.hospital.enums.EmergencyCaseStatus;
import com.hospital.model.EmergencyCase;

import java.util.List;

public class ConsoleEmergencyOps extends ConsoleHelpers {

    public ConsoleEmergencyOps(java.util.Scanner input) {
        super(input);
    }

    public void listEmergencyCases() {
        List<EmergencyCase> cases = emergencyService.getAllCases();
        if (cases.isEmpty()) {
            System.out.println("No emergency cases registered yet.");
            return;
        }
        System.out.println("--- Emergency cases (" + cases.size() + ") ---");
        System.out.printf("%-10s %-12s %-10s %-14s %s%n", "ID", "PATIENT", "PRIORITY", "STATUS", "DESCRIPTION");
        for (EmergencyCase c : cases) {
            System.out.printf("%-10s %-12s %-10s %-14s %s%n",
                    text(c.getId()), patientName(c.getPatientId()), text(c.getPriority()),
                    text(c.getStatus()), text(c.getDescription()));
        }
    }

    public void registerEmergencyCase() {
        String patientId = ask("Patient id: ");
        var priority = parsePriority(ask("Priority (LOW/MODERATE/SERIOUS/CRITICAL): "));
        String description = ask("Description: ");
        EmergencyCase emergencyCase = emergencyService.addEmergencyCase(patientId, priority, description);
        System.out.println("Emergency Case Added");
        System.out.println("Patient: " + patientName(emergencyCase.getPatientId()));
        System.out.println("Priority: " + emergencyCase.getPriority());
        System.out.println("Status: " + emergencyCase.getStatus());
        System.out.println("Case id: " + emergencyCase.getId()
                + " -> data/emergency_cases.json updated automatically.");
    }

    public void updateEmergencyStatus() {
        String caseId = ask("Emergency case id: ");
        EmergencyCaseStatus status = parseStatus(ask("New status (WAITING/IN_TREATMENT/COMPLETED/CANCELLED): "));
        emergencyService.updateStatus(caseId, status);
        System.out.println("Updated " + caseId + " -> data/emergency_cases.json updated automatically.");
    }

    public void viewEmergencyQueue() {
        List<EmergencyCase> queue = emergencyService.viewEmergencyQueue();
        if (queue.isEmpty()) {
            System.out.println("Emergency queue is empty.");
            return;
        }
        System.out.println("--- Emergency queue (priority order, " + queue.size() + " waiting) ---");
        int position = 1;
        for (EmergencyCase emergencyCase : queue) {
            System.out.printf("%d. %-12s %-10s %s%n", position++,
                    patientName(emergencyCase.getPatientId()),
                    emergencyCase.getPriority(), emergencyCase.getDescription());
        }
    }

    public void showNextPatient() {
        EmergencyCase next = emergencyService.getNextPatient();
        System.out.println("Next patient:");
        System.out.println("Patient: " + patientName(next.getPatientId()));
        System.out.println("Priority: " + next.getPriority());
        System.out.println("Status: " + next.getStatus());
        System.out.println("Description: " + next.getDescription());
    }

    public void startTreatment() {
        EmergencyCase started = emergencyService.startTreatment();
        var doctor = doctorService.findDoctorById(started.getAssignedDoctorId()).orElse(null);
        System.out.println("Treatment started:");
        System.out.println("Patient: " + patientName(started.getPatientId()));
        System.out.println("Priority: " + started.getPriority());
        System.out.println("Status: " + started.getStatus());
        System.out.println("Doctor: " + (doctor != null ? doctor.getName() : "-"));
        System.out.println("data/emergency_cases.json and data/doctors.json updated automatically.");
    }

    public void completeTreatment() {
        EmergencyCase finished = emergencyService.completeTreatment(ask("Emergency case id to complete: "));
        System.out.println("Treatment completed:");
        System.out.println("Patient: " + patientName(finished.getPatientId()));
        System.out.println("Status: " + finished.getStatus());
        String doctorId = finished.getAssignedDoctorId();
        if (doctorId != null) {
            doctorService.findDoctorById(doctorId)
                    .ifPresent(d -> System.out.println("Doctor: " + d.getName() + " (available again)"));
        }
        System.out.println("data/emergency_cases.json and data/doctors.json updated automatically.");
    }

    public void cancelEmergencyCase() {
        EmergencyCase cancelled = emergencyService.cancelEmergencyCase(ask("Emergency case id to cancel: "));
        System.out.println("Cancelled case " + cancelled.getId() + " for patient "
                + patientName(cancelled.getPatientId())
                + " -> data/emergency_cases.json updated automatically.");
    }
}
