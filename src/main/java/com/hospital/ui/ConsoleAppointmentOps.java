package com.hospital.ui;

import com.hospital.exception.InvalidDataException;

import java.time.LocalDate;
import java.time.LocalTime;

public class ConsoleAppointmentOps extends ConsoleHelpers {

    public ConsoleAppointmentOps(java.util.Scanner input) {
        super(input);
    }

    public void listAppointments() {
        var list = appointmentService.getAllAppointments();
        if (list.isEmpty()) {
            System.out.println("No appointments yet.");
            return;
        }
        System.out.println("--- Appointments (" + list.size() + ") ---");
        System.out.printf("%-10s %-10s %-10s %-12s %-8s %-12s %s%n",
                "ID", "PATIENT", "DOCTOR", "DATE", "TIME", "STATUS", "REASON");
        for (var a : list) {
            System.out.printf("%-10s %-10s %-10s %-12s %-8s %-12s %s%n",
                    text(a.getId()), text(a.getPatientId()), text(a.getDoctorId()),
                    text(a.getDate()), text(a.getTime()), text(a.getStatus()), text(a.getReason()));
        }
    }

    public void createAppointment() {
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

    public void findAppointment() {
        String id = ask("Appointment id: ");
        var appt = appointmentService.getAppointmentById(id)
                .orElseThrow(() -> new InvalidDataException("Appointment not found: " + id));
        appt.displayInfo();
    }

    public void listAppointmentsByPatient() {
        String pid = ask("Patient id: ");
        var list = appointmentService.getAppointmentsByPatient(pid);
        if (list.isEmpty()) {
            System.out.println("No appointments for patient " + pid);
            return;
        }
        list.forEach(a -> System.out.println(a.getId() + " " + a.getDate() + " " + a.getTime()
                + " " + a.getStatus() + " Dr:" + a.getDoctorId()));
    }

    public void listAppointmentsByDoctor() {
        String did = ask("Doctor id: ");
        var list = appointmentService.getAppointmentsByDoctor(did);
        if (list.isEmpty()) {
            System.out.println("No appointments for doctor " + did);
            return;
        }
        list.forEach(a -> System.out.println(a.getId() + " " + a.getDate() + " " + a.getTime()
                + " " + a.getStatus() + " Pat:" + a.getPatientId()));
    }

    public void rescheduleAppointment() {
        String id = ask("Appointment id to reschedule: ");
        LocalDate newDate = parseDate(ask("New date (yyyy-MM-dd): "));
        LocalTime newTime = parseTime(ask("New time (HH:mm): "));
        var appt = appointmentService.rescheduleAppointment(id, newDate, newTime);
        System.out.println("Appointment rescheduled successfully.");
        System.out.println(appt.getId() + " -> " + appt.getDate() + " " + appt.getTime());
    }

    public void confirmAppointment() {
        String id = ask("Appointment id to confirm: ");
        var appt = appointmentService.confirmAppointment(id);
        System.out.println("Appointment " + appt.getId() + " confirmed. Status: " + appt.getStatus());
    }

    public void completeAppointment() {
        String id = ask("Appointment id to complete: ");
        var appt = appointmentService.completeAppointment(id);
        System.out.println("Appointment " + appt.getId() + " completed. Status: " + appt.getStatus());
    }

    public void cancelAppointment() {
        String id = ask("Appointment id to cancel: ");
        var appt = appointmentService.cancelAppointment(id);
        System.out.println("Appointment " + appt.getId() + " cancelled successfully. Status: " + appt.getStatus());
    }
}
