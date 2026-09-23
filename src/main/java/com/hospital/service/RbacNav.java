package com.hospital.service;

import com.hospital.enums.Permission;

import java.util.Map;
import java.util.Set;

public final class RbacNav {
    private static final Map<String, Permission> NAV = Map.ofEntries(
            Map.entry("Dashboard", Permission.VIEW_DASHBOARD),
            Map.entry("Patients", Permission.VIEW_PATIENT),
            Map.entry("Appointments", Permission.VIEW_APPOINTMENT),
            Map.entry("Emergency", Permission.VIEW_EMERGENCY),
            Map.entry("Blood Bank", Permission.VIEW_BLOOD_BANK),
            Map.entry("Ambulance", Permission.VIEW_AMBULANCE),
            Map.entry("Operations", Permission.VIEW_OPERATION),
            Map.entry("Vital Monitoring", Permission.VIEW_VITALS),
            Map.entry("Follow-Ups", Permission.VIEW_FOLLOW_UPS),
            Map.entry("Doctors", Permission.VIEW_DOCTORS),
            Map.entry("Doctor Schedule", Permission.VIEW_APPOINTMENT),
            Map.entry("Staff Shifts", Permission.VIEW_SHIFTS),
            Map.entry("Equipment", Permission.VIEW_EQUIPMENT),
            Map.entry("Reports", Permission.VIEW_REPORTS),
            Map.entry("Backups", Permission.BACKUP_DATA),
            Map.entry("Settings", Permission.MANAGE_SETTINGS),
            Map.entry("User Management", Permission.MANAGE_USERS),
            Map.entry("Profile", Permission.VIEW_PROFILE)
    );

    private static final Map<String, Set<String>> SECTIONS = Map.of(
            "PATIENT CARE", Set.of("Patients", "Emergency", "Appointments"),
            "ADVANCED CARE", Set.of("Blood Bank", "Ambulance", "Operations",
                    "Vital Monitoring", "Follow-Ups"),
            "STAFF", Set.of("Doctors", "Doctor Schedule", "Staff Shifts", "Equipment"),
            "SYSTEM", Set.of("Reports", "Backups", "Settings", "User Management")
    );

    private static final Set<String> ORDERED = Set.of(
            "Patients", "Emergency", "Appointments", "Blood Bank", "Ambulance",
            "Operations", "Vital Monitoring", "Follow-Ups", "Doctors",
            "Doctor Schedule", "Staff Shifts", "Equipment", "Reports",
            "Backups", "Settings", "User Management");

    private RbacNav() {}

    public static Permission requiredPermission(String navItem) {
        return NAV.get(navItem);
    }

    public static Map<String, Set<String>> sections() { return SECTIONS; }

    public static Set<String> orderedItems() { return ORDERED; }
}
