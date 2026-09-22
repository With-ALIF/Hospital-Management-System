package com.hospital;

import com.hospital.exception.InvalidDataException;
import com.hospital.model.Doctor;
import com.hospital.model.EmergencyCase;
import com.hospital.model.EmergencyCaseStatus;
import com.hospital.model.EmergencyPriority;
import com.hospital.model.Patient;
import com.hospital.service.AppointmentService;
import com.hospital.service.DoctorService;
import com.hospital.service.EmergencyService;
import com.hospital.service.PatientService;
import com.hospital.ui.SlotParser;

import java.time.LocalDate;
import java.time.LocalTime;
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
    private final EmergencyService emergencyService = new EmergencyService(patientService, doctorService);
    private final AppointmentService appointmentService = new AppointmentService(patientService, doctorService);

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
        System.out.println("Appointments loaded: " + appointmentService.getAppointmentCount());
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
                    case "10":
                        viewEmergencyQueue();
                        break;
                    case "11":
                        showNextPatient();
                        break;
                    case "12":
                        startTreatment();
                        break;
                    case "13":
                        completeTreatment();
                        break;
                    case "14":
                        cancelEmergencyCase();
                        break;
                    case "15":
                        listAppointments();
                        break;
                    case "16":
                        createAppointment();
                        break;
                    case "17":
                        findAppointment();
                        break;
                    case "18":
                        listAppointmentsByPatient();
                        break;
                    case "19":
                        listAppointmentsByDoctor();
                        break;
                    case "20":
                        rescheduleAppointment();
                        break;
                    case "21":
                        confirmAppointment();
                        break;
                    case "22":
                        completeAppointment();
                        break;
                    case "23":
                        cancelAppointment();
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
        System.out.println("10) View emergency queue (priority order)");
        System.out.println("11) Show next patient in queue");
        System.out.println("12) Start treatment (assign available doctor)");
        System.out.println("13) Complete treatment");
        System.out.println("14) Cancel emergency case");
        System.out.println("--- Appointments ---");
        System.out.println("15) List appointments");
        System.out.println("16) Create appointment");
        System.out.println("17) Find appointment by ID");
        System.out.println("18) Appointments by patient");
        System.out.println("19) Appointments by doctor");
        System.out.println("20) Reschedule appointment");
        System.out.println("21) Confirm appointment");
        System.out.println("22) Complete appointment");
        System.out.println("23) Cancel appointment");
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
    }

    private void registerEmergencyCase() {
        String patientId = ask("Patient id: ");
        EmergencyPriority priority = parsePriority(ask("Priority (LOW/MEDIUM/HIGH/CRITICAL): "));
        String description = ask("Description: ");
        EmergencyCase emergencyCase = emergencyService.addEmergencyCase(patientId, priority, description);
        System.out.println("Emergency Case Added");
        System.out.println("Patient: " + patientName(emergencyCase.getPatientId()));
        System.out.println("Priority: " + emergencyCase.getPriority());
        System.out.println("Status: " + emergencyCase.getStatus());
        System.out.println("Case id: " + emergencyCase.getId()
                + " -> data/emergency_cases.json updated automatically.");
    }

    private void updateEmergencyStatus() {
        String caseId = ask("Emergency case id: ");
        EmergencyCaseStatus status = parseStatus(ask("New status (WAITING/IN_TREATMENT/COMPLETED/CANCELLED): "));
        emergencyService.updateStatus(caseId, status);
        System.out.println("Updated " + caseId + " -> data/emergency_cases.json updated automatically.");
    }

    private void viewEmergencyQueue() {
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

    private void showNextPatient() {
        EmergencyCase next = emergencyService.getNextPatient();
        System.out.println("Next patient:");
        System.out.println("Patient: " + patientName(next.getPatientId()));
        System.out.println("Priority: " + next.getPriority());
        System.out.println("Status: " + next.getStatus());
        System.out.println("Description: " + next.getDescription());
    }

    private void startTreatment() {
        EmergencyCase started = emergencyService.startTreatment();
        Doctor doctor = doctorService.findDoctorById(started.getAssignedDoctorId()).orElse(null);
        System.out.println("Treatment started:");
        System.out.println("Patient: " + patientName(started.getPatientId()));
        System.out.println("Priority: " + started.getPriority());
        System.out.println("Status: " + started.getStatus());
        System.out.println("Doctor: " + (doctor != null ? doctor.getName() : "-"));
        System.out.println("data/emergency_cases.json and data/doctors.json updated automatically.");
    }

    private void completeTreatment() {
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

    private void cancelEmergencyCase() {
        EmergencyCase cancelled = emergencyService.cancelEmergencyCase(ask("Emergency case id to cancel: "));
        System.out.println("Cancelled case " + cancelled.getId() + " for patient "
                + patientName(cancelled.getPatientId())
                + " -> data/emergency_cases.json updated automatically.");
    }

    // -------------------------------------------------------------- appointments

    private void listAppointments() {
        var list = appointmentService.getAllAppointments();
        if (list.isEmpty()) {
            System.out.println("No appointments yet.");
            return;
        }
        System.out.println("--- Appointments (" + list.size() + ") ---");
        System.out.printf("%-8s %-10s %-10s %-12s %-8s %-12s %s%n", "ID", "PATIENT", "DOCTOR", "DATE", "TIME", "STATUS", "REASON");
        for (var a : list) {
            System.out.printf("%-8s %-10s %-10s %-12s %-8s %-12s %s%n",
                    text(a.getId()), text(a.getPatientId()), text(a.getDoctorId()),
                    text(a.getDate()), text(a.getTime()), text(a.getStatus()), text(a.getReason()));
        }
    }

    private void createAppointment() {
        String patientId = ask("Patient id: ");
        String doctorId = ask("Doctor id: ");
        LocalDate date = parseDate(ask("Date (yyyy-MM-dd): "));
        LocalTime time = parseTime(ask("Time (HH:mm): "));
        String reason = ask("Reason: ");
        var appt = appointmentService.createAppointment(patientId, doctorId, date, time, reason);
        System.out.println("Appointment created successfully.");
        System.out.println("Appointment ID: " + appt.getId());
        System.out.println(appt.getId() + " -> data/appointments.json updated automatically.");
    }

    private void findAppointment() {
        String id = ask("Appointment id: ");
        var appt = appointmentService.getAppointmentById(id)
                .orElseThrow(() -> new InvalidDataException("Appointment not found: " + id));
        appt.displayInfo();
    }

    private void listAppointmentsByPatient() {
        String pid = ask("Patient id: ");
        var list = appointmentService.getAppointmentsByPatient(pid);
        if (list.isEmpty()) { System.out.println("No appointments for patient " + pid); return; }
        list.forEach(a -> System.out.println(a.getId() + " " + a.getDate() + " " + a.getTime() + " " + a.getStatus() + " Dr:" + a.getDoctorId()));
    }

    private void listAppointmentsByDoctor() {
        String did = ask("Doctor id: ");
        var list = appointmentService.getAppointmentsByDoctor(did);
        if (list.isEmpty()) { System.out.println("No appointments for doctor " + did); return; }
        list.forEach(a -> System.out.println(a.getId() + " " + a.getDate() + " " + a.getTime() + " " + a.getStatus() + " Pat:" + a.getPatientId()));
    }

    private void rescheduleAppointment() {
        String id = ask("Appointment id to reschedule: ");
        LocalDate newDate = parseDate(ask("New date (yyyy-MM-dd): "));
        LocalTime newTime = parseTime(ask("New time (HH:mm): "));
        var appt = appointmentService.rescheduleAppointment(id, newDate, newTime);
        System.out.println("Appointment rescheduled successfully.");
        System.out.println(appt.getId() + " -> " + appt.getDate() + " " + appt.getTime());
    }

    private void confirmAppointment() {
        String id = ask("Appointment id to confirm: ");
        var appt = appointmentService.confirmAppointment(id);
        System.out.println("Appointment " + appt.getId() + " confirmed. Status: " + appt.getStatus());
    }

    private void completeAppointment() {
        String id = ask("Appointment id to complete: ");
        var appt = appointmentService.completeAppointment(id);
        System.out.println("Appointment " + appt.getId() + " completed. Status: " + appt.getStatus());
    }

    private void cancelAppointment() {
        String id = ask("Appointment id to cancel: ");
        var appt = appointmentService.cancelAppointment(id);
        System.out.println("Appointment #" + appt.getId() + " cancelled successfully. Status: " + appt.getStatus());
    }

    private static LocalDate parseDate(String value) {
        try { return LocalDate.parse(value.trim()); }
        catch (Exception e) { throw new InvalidDataException("Invalid date, expected yyyy-MM-dd: " + value); }
    }

    private static LocalTime parseTime(String value) {
        try { return LocalTime.parse(value.trim()); }
        catch (Exception e) { throw new InvalidDataException("Invalid time, expected HH:mm: " + value); }
    }

    private String patientName(String patientId) {
        return patientService.findPatientById(patientId)
                .map(Patient::getName)
                .orElse(patientId);
    }

    private void printCases(List<EmergencyCase> cases) {
        System.out.printf("%-8s %-10s %-10s %-14s %s%n", "ID", "PATIENT", "PRIORITY", "STATUS", "DESCRIPTION");
        for (EmergencyCase c : cases) {
            System.out.printf("%-8s %-10s %-10s %-14s %s%n",
                    text(c.getId()), patientName(c.getPatientId()), text(c.getPriority()),
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