package com.hospital.ui;

import com.hospital.exception.InvalidDataException;
import com.hospital.model.Doctor;

public class ConsoleDoctorOps extends ConsoleHelpers {

    public ConsoleDoctorOps(java.util.Scanner input) {
        super(input);
    }

    public void listDoctors() {
        var doctors = doctorService.getAllDoctors();
        if (doctors.isEmpty()) {
            System.out.println("No doctors registered yet.");
            return;
        }
        System.out.println("--- Doctors (" + doctors.size() + ") ---");
        System.out.printf("%-10s %-20s %-16s %-10s %s%n", "ID", "NAME", "SPECIALIZATION", "AVAILABLE", "DUTY HOURS");
        for (Doctor d : doctors) {
            System.out.printf("%-10s %-20s %-16s %-10s %s%n",
                    text(d.getId()), text(d.getName()), text(d.getSpecialization()),
                    d.getAvailable() ? "yes" : "no", text(d.getDutyScheduleString()));
        }
    }

    public void addDoctor() {
        String id = doctorService.nextDoctorId();
        Doctor doctor = new Doctor(id,
                ask("Doctor name: "),
                ask("Phone: "),
                ask("Gender (Male/Female/Other): "),
                ask("Specialization: "));
        SlotParser.parseAndAdd(doctor, ask("Duty slot 1 (e.g. 09:00-13:00, empty = all hours): "));
        SlotParser.parseAndAdd(doctor, ask("Duty slot 2 (optional): "));
        doctorService.registerDoctor(doctor);
        System.out.println("Saved " + id + " -> data/doctors.json updated automatically.");
    }
}
