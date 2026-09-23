package com.hospital.util;

import com.hospital.enums.StaffRole;

import java.util.Map;
import java.util.Set;

public final class RbacPolicy {
    private static final Set<String> ADMIN_NAV = Set.of(
            "Dashboard", "Patients", "Doctors", "Appointments", "Emergency",
            "Blood Bank", "Ambulance", "Operations", "Vital Monitoring",
            "Follow-Ups", "Doctor Schedule", "Staff Shifts", "Equipment",
            "Reports", "Backups", "Settings", "User Management", "Profile");

    private static final Map<StaffRole, Set<String>> NAV = Map.of(
            StaffRole.ADMIN, ADMIN_NAV,
            StaffRole.DOCTOR, Set.of("Dashboard", "Patients", "Appointments",
                    "Emergency", "Operations", "Vital Monitoring", "Follow-Ups",
                    "Doctor Schedule", "Doctors", "Reports", "Profile"),
            StaffRole.NURSE, Set.of("Dashboard", "Patients", "Emergency",
                    "Vital Monitoring", "Staff Shifts", "Equipment", "Follow-Ups",
                    "Blood Bank", "Profile"),
            StaffRole.RECEPTIONIST, Set.of("Dashboard", "Patients", "Appointments",
                    "Emergency", "Reports", "Profile"),
            StaffRole.PHARMACIST, Set.of("Dashboard", "Blood Bank", "Equipment",
                    "Reports", "Profile"),
            StaffRole.LAB_TECHNICIAN, Set.of("Dashboard", "Emergency", "Reports",
                    "Profile"));

    private RbacPolicy() {}

    public static boolean canView(StaffRole role, String navItem) {
        if (role == null) return false;
        return NAV.getOrDefault(role, Set.of()).contains(navItem);
    }

    public static Set<String> navFor(StaffRole role) {
        if (role == null) return Set.of();
        return NAV.getOrDefault(role, Set.of());
    }

    public static boolean can(StaffRole role, String action) {
        if (role == null) return false;
        if (StaffRole.ADMIN == role) return true;
        return switch (action) {
            case "patients.delete", "staff.manage", "doctors.delete",
                 "users.manage", "backup.manage", "settings.manage" -> false;
            case "pharmacy.sell" -> role == StaffRole.PHARMACIST;
            case "lab.result" -> role == StaffRole.LAB_TECHNICIAN;
            case "prescription.create" -> role == StaffRole.DOCTOR;
            case "billing.pay" -> role == StaffRole.RECEPTIONIST;
            case "bed.assign" -> role == StaffRole.NURSE
                    || role == StaffRole.RECEPTIONIST || role == StaffRole.DOCTOR;
            default -> true;
        };
    }
}
