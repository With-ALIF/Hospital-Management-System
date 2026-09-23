package com.hospital.ui;

import com.hospital.enums.EmergencyCaseStatus;
import com.hospital.enums.EmergencyLevel;
import com.hospital.exception.InvalidDataException;
import com.hospital.model.Patient;
import com.hospital.service.AppointmentService;
import com.hospital.service.DoctorService;
import com.hospital.service.EmergencyService;
import com.hospital.service.PatientService;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Scanner;

public class ConsoleHelpers {
    final Scanner input;
    final PatientService patientService = new PatientService();
    final DoctorService doctorService = new DoctorService();
    final EmergencyService emergencyService = new EmergencyService(patientService, doctorService);
    final AppointmentService appointmentService = new AppointmentService(patientService, doctorService);
    final ConsolePatientOps patientOps = new ConsolePatientOps(this, patientService);

    public ConsoleHelpers(Scanner input) {
        this.input = input;
    }

    public void printCounts() {
        System.out.println("Patients loaded: " + patientService.getPatientCount());
        System.out.println("Doctors loaded: " + doctorService.getDoctorCount());
        System.out.println("Emergency cases loaded: " + emergencyService.getCaseCount());
        System.out.println("Appointments loaded: " + appointmentService.getAppointmentCount());
    }

    public void listPatients() {
        patientOps.listPatients();
    }

    public void addPatient() {
        patientOps.addPatient();
    }

    public void updatePatient() {
        patientOps.updatePatient();
    }

    public void deletePatient() {
        patientOps.deletePatient();
    }

    String patientName(String pid) {
        return patientService.findPatientById(pid).map(Patient::getName).orElse(pid);
    }

    static LocalDate parseDate(String value) {
        try {
            return LocalDate.parse(value.trim());
        } catch (Exception e) {
            throw new InvalidDataException("Invalid date, expected yyyy-MM-dd: " + value);
        }
    }

    static LocalTime parseTime(String value) {
        try {
            return LocalTime.parse(value.trim());
        } catch (Exception e) {
            throw new InvalidDataException("Invalid time, expected HH:mm: " + value);
        }
    }

    static EmergencyLevel parsePriority(String value) {
        try {
            return EmergencyLevel.from(value);
        } catch (IllegalArgumentException e) {
            throw new InvalidDataException("Unknown priority: " + value);
        }
    }

    static EmergencyCaseStatus parseStatus(String value) {
        try {
            return EmergencyCaseStatus.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidDataException("Unknown status: " + value);
        }
    }

    String ask(String prompt) {
        System.out.print(prompt);
        if (!input.hasNextLine()) return "";
        return input.nextLine().trim();
    }

    String askOrDefault(String prompt, String currentValue) {
        String answer = ask(prompt);
        return answer.isEmpty() ? currentValue : answer;
    }

    static String text(Object value) {
        return value == null ? "-" : value.toString();
    }
}
