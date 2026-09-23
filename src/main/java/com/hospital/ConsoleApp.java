package com.hospital;

import com.hospital.exception.InvalidDataException;
import com.hospital.ui.ConsoleAppointmentOps;
import com.hospital.ui.ConsoleDoctorOps;
import com.hospital.ui.ConsoleEmergencyOps;
import com.hospital.ui.ConsoleHelpers;

import java.util.Scanner;

public class ConsoleApp {
    private final Scanner input = new Scanner(System.in);
    private final ConsoleEmergencyOps helpers = new ConsoleEmergencyOps(input);
    private final ConsoleAppointmentOps apptOps = new ConsoleAppointmentOps(input);
    private final ConsoleDoctorOps doctorOps = new ConsoleDoctorOps(input);

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
        helpers.printCounts();
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
                running = handleChoice(choice);
            } catch (InvalidDataException e) {
                System.out.println("[Error] " + e.getMessage());
            } catch (RuntimeException e) {
                System.out.println("[Unexpected error] " + e);
            }
        }
    }

    private boolean handleChoice(String choice) {
        switch (choice) {
            case "1": helpers.listPatients(); return true;
            case "2": helpers.addPatient(); return true;
            case "3": helpers.updatePatient(); return true;
            case "4": helpers.deletePatient(); return true;
            case "5": doctorOps.listDoctors(); return true;
            case "6": doctorOps.addDoctor(); return true;
            case "7": helpers.listEmergencyCases(); return true;
            case "8": helpers.registerEmergencyCase(); return true;
            case "9": helpers.updateEmergencyStatus(); return true;
            case "10": helpers.viewEmergencyQueue(); return true;
            case "11": helpers.showNextPatient(); return true;
            case "12": helpers.startTreatment(); return true;
            case "13": helpers.completeTreatment(); return true;
            case "14": helpers.cancelEmergencyCase(); return true;
            case "15": apptOps.listAppointments(); return true;
            case "16": apptOps.createAppointment(); return true;
            case "17": apptOps.findAppointment(); return true;
            case "18": apptOps.listAppointmentsByPatient(); return true;
            case "19": apptOps.listAppointmentsByDoctor(); return true;
            case "20": apptOps.rescheduleAppointment(); return true;
            case "21": apptOps.confirmAppointment(); return true;
            case "22": apptOps.completeAppointment(); return true;
            case "23": apptOps.cancelAppointment(); return true;
            case "0":
                System.out.println("Good bye. All changes are already saved in the JSON files.");
                return false;
            default:
                System.out.println("Unknown option: " + choice);
                return true;
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
}
