package com.hospital;

import com.hospital.exception.InvalidDataException;
import com.hospital.model.Doctor;
import com.hospital.model.EmergencyCase;
import com.hospital.model.EmergencyCaseStatus;
import com.hospital.model.EmergencyPriority;
import com.hospital.model.Patient;
import com.hospital.service.DoctorService;
import com.hospital.service.EmergencyService;
import com.hospital.service.PatientService;
import com.hospital.ui.SlotParser;

import java.util.List;
import java.util.Scanner;

/**
 * Day 3 console entry point: JSON persistence and auto synchronization.
 *
 * <p>Startup: the services (and behind them the repositories) read
 * data/patients.json, data/doctors.json and data/emergency_cases.json into
 * memory and print how many records were loaded. Every change made in this menu
 * is written back to the JSON file immediately, so the data survives a restart
 * of the program.</p>
 *
 * <p>Run with:
 * {@code mvn -q compile exec:java -Dexec.mainClass=com.hospital.ConsoleApp}
 * The JavaFX interface (Main / App) is untouched and still starts with
 * {@code mvn javafx:run}.</p>
 */
public class ConsoleApp {

    private final Scanner input = new Scanner(System.in);
    private final PatientService patientService = new PatientService();
    private final DoctorService doctorService = new DoctorService();
    private final EmergencyService emergencyService = new EmergencyService(patientService);

    public static void main(String[] args) {
        ConsoleApp app = new ConsoleApp();
        app.printStartupReport();
        app.runMenu();
    }

    private void printStartupReport() {
        System.out.println("============================================================");
        System.out.println(" Hospital Operations & Emergency Response System");
        System.out.println("============================================================");
        System.out.println("Loading hospital data...");
        System.out.println("Patients loaded: " + patientService.getPatientCount());
        System.out.println("Doctors loaded: " + doctorService.getDoctorCount());
        System.out.println("Emergency cases loaded: " + emergencyService.getCaseCount());
        System.out.println("System ready.");
    }

    private void runMenu() {
        boolean running = true;
        while (running) {
            printMenu();
            if (!input.hasNextLine()) {
                System.out.println();
                break;
            }
            String choice = input.nextLine().trim();
            try {
                switch (choice) {
                    case "1":
                        listPatients();
                        break;
                    case "2":
                        addPatient();
                        break;
                    case "3":
                        updatePatient();
                        break;
                    case "4":
                        deletePatient();
                        break;
                    case "5":
                        listDoctors();
                        break;
                    case "6":
                        addDoctor();
                        break;
                    case "7":
                        listEmergencyCases();
                        break;
                    case "8":
                        registerEmergencyCase();
                        break;
                    case "9":
                        updateEmergencyStatus();
                        break;
                    case "0":
                        running = false;
                        System.out.println("Good bye. All changes are already saved in the JSON files.");
                        break;
                    default:
                        System.out.println("Unknown option: " + choice);
                }
            } catch (InvalidDataException e) {
                System.out.println("[Error] " + e.getMessage());
            } catch (RuntimeException e) {
                System.out.println("[Unexpected error] " + e);
            }
        }
    }

    private void printMenu() {
        System.out.println();
        System.out.println("--------------- MENU ---------------");
        System.out.println("1) List patients");
        System.out.println("2) Add patient");
        System.out.println("3) Update patient");
        System.out.println("4) Delete patient");
        System.out.println("5) List doctors");
        System.out.println("6) Add doctor");
        System.out.println("7) List emergency cases (triage order)");
        System.out.println("8) Register emergency case");
        System.out.println("9) Update emergency case status");
        System.out.println("0) Exit");
        System.out.print("Choice: ");
    }

    // ----------------------------------------------------------------- patients

    private void listPatients() {
        List<Patient> patients = patientService.getAllPatients();
        if (patients.isEmpty()) {
            System.out.println("No patients registered yet.");
            return;
        }
        System.out.println("--- Patients (" + patients.size() + ") ---");
        System.out.printf("%-8s %-20s %-7s %-6s %-13s %s%n",
                "ID", "NAME", "GENDER", "BLOOD", "PHONE", "EMERGENCY CONTACT");
        for (Patient p : patients) {
            System.out.printf("%-8s %-20s %-7s %-6s %-13s %s%n",
                    text(p.getId()), text(p.getName()), text(p.getGender()),
                    text(p.getBloodGroup()), text(p.getPhone()), text(p.getEmergencyContact()));
        }
    }

    private void addPatient() {
        String id = patientService.nextPatientId();
        Patient patient = new Patient(id,
                ask("Patient name: "),
                ask("Phone: "),
                ask("Gender (Male/Female/Other): "),
                ask("Blood group: "),
                ask("Emergency contact: "));
        patientService.registerPatient(patient);
        System.out.println("Saved " + id + " -> data/patients.json updated automatically.");
    }

    private void updatePatient() {
        String id = ask("Patient id to update: ");
        Patient patient = patientService.findPatientById(id)
                .orElseThrow(() -> new InvalidDataException("Patient not found: " + id));

        System.out.println("Leave a field empty to keep the current value.");
        patient.setName(askOrDefault("Name [" + text(patient.getName()) + "]: ", patient.getName()));
        patient.setPhone(askOrDefault("Phone [" + text(patient.getPhone()) + "]: ", patient.getPhone()));
        patient.setBloodGroup(askOrDefault(
                "Blood group [" + text(patient.getBloodGroup()) + "]: ", patient.getBloodGroup()));
        patient.setEmergencyContact(askOrDefault(
                "Emergency contact [" + text(patient.getEmergencyContact()) + "]: ", patient.getEmergencyContact()));

        patientService.updatePatient(patient);
        System.out.println("Updated " + patient.getId() + " -> data/patients.json updated automatically.");
    }

    private void deletePatient() {
        String id = ask("Patient id to delete: ");
        patientService.removePatient(id);
        System.out.println("Deleted " + id + " -> data/patients.json updated automatically.");
    }

    // ------------------------------------------------------------------ doctors

    private void listDoctors() {
        List<Doctor> doctors = doctorService.getAllDoctors();
        if (doctors.isEmpty()) {
            System.out.println("No doctors registered yet.");
            return;
        }
        System.out.println("--- Doctors (" + doctors.size() + ") ---");
        System.out.printf("%-8s %-20s %-16s %-10s %s%n", "ID", "NAME", "SPECIALIZATION", "AVAILABLE", "DUTY HOURS");
        for (Doctor d : doctors) {
            System.out.printf("%-8s %-20s %-16s %-10s %s%n",
                    text(d.getId()), text(d.getName()), text(d.getSpecialization()),
                    d.getAvailable() ? "yes" : "no", text(d.getDutyScheduleString()));
        }
    }

    private void addDoctor() {
        String id = doctorService.nextDoctorId();
        Doctor doctor = new Doctor(id,
                ask("Doctor name: "),
                ask("Phone: "),
                ask("Gender (Male/Female/Other): "),
                ask("Specialization: "));
        // SlotParser is the same helper the JavaFX form uses, e.g. "09:00-13:00".
        SlotParser.parseAndAdd(doctor, ask("Duty slot 1 (e.g. 09:00-13:00, empty = all hours): "));
        SlotParser.parseAndAdd(doctor, ask("Duty slot 2 (optional): "));
        doctorService.registerDoctor(doctor);
        System.out.println("Saved " + id + " -> data/doctors.json updated automatically.");
    }

    // ----------------------------------------------------------- emergency cases

    private void listEmergencyCases() {
        List<EmergencyCase> cases = emergencyService.getAllCases();
        if (cases.isEmpty()) {
            System.out.println("No emergency cases registered yet.");
            return;
        }
        System.out.println("--- Emergency cases (" + cases.size() + ") ---");
        printCases(cases);

        List<EmergencyCase> queue = emergencyService.getWaitingQueue();
        if (!queue.isEmpty()) {
            System.out.println("--- Waiting queue (most severe first) ---");
            printCases(queue);
        }
    }

    private void registerEmergencyCase() {
        String patientId = ask("Patient id: ");
        EmergencyPriority priority = parsePriority(ask("Priority (LOW/MEDIUM/HIGH/CRITICAL): "));
        String description = ask("Description: ");
        EmergencyCase emergencyCase = emergencyService.registerCase(patientId, priority, description);
        System.out.println("Saved " + emergencyCase.getId()
                + " -> data/emergency_cases.json updated automatically.");
    }

    private void updateEmergencyStatus() {
        String caseId = ask("Emergency case id: ");
        EmergencyCaseStatus status = parseStatus(ask("New status (WAITING/IN_TREATMENT/TREATED/DISCHARGED): "));
        emergencyService.updateStatus(caseId, status);
        System.out.println("Updated " + caseId + " -> data/emergency_cases.json updated automatically.");
    }

    private void printCases(List<EmergencyCase> cases) {
        System.out.printf("%-8s %-10s %-10s %-14s %s%n", "ID", "PATIENT", "PRIORITY", "STATUS", "DESCRIPTION");
        for (EmergencyCase c : cases) {
            System.out.printf("%-8s %-10s %-10s %-14s %s%n",
                    text(c.getId()), text(c.getPatientId()), text(c.getPriority()),
                    text(c.getStatus()), text(c.getDescription()));
        }
    }

    // ------------------------------------------------------------------- helpers

    private static EmergencyPriority parsePriority(String value) {
        try {
            return EmergencyPriority.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidDataException("Unknown priority: " + value);
        }
    }

    private static EmergencyCaseStatus parseStatus(String value) {
        try {
            return EmergencyCaseStatus.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidDataException("Unknown status: " + value);
        }
    }

    private String ask(String prompt) {
        System.out.print(prompt);
        if (!input.hasNextLine()) {
            return "";
        }
        return input.nextLine().trim();
    }

    private String askOrDefault(String prompt, String currentValue) {
        String answer = ask(prompt);
        return answer.isEmpty() ? currentValue : answer;
    }

    private static String text(Object value) {
        return value == null ? "-" : value.toString();
    }
}