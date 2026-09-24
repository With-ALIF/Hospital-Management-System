package com.hospital.service;

import com.hospital.enums.Permission;
import com.hospital.enums.StaffRole;

import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

final class RolePermissions {
    private RolePermissions() {}

    static Set<Permission> forRole(StaffRole role) {
        if (role == null) return Set.of();
        if (role == StaffRole.ADMIN) return EnumSet.allOf(Permission.class);
        return Map.of(
                StaffRole.DOCTOR, doctor(),
                StaffRole.NURSE, nurse(),
                StaffRole.RECEPTIONIST, receptionist(),
                StaffRole.PHARMACIST, pharmacist(),
                StaffRole.LAB_TECHNICIAN, lab()
        ).getOrDefault(role, Set.of());
    }

    private static Set<Permission> doctor() {
        // Doctor sees only doctor-related modules: no Blood Bank, Ambulance,
        // Staff Shifts or Equipment (those stay with nurse/pharmacist/admin).
        EnumSet<Permission> p = EnumSet.of(Permission.VIEW_DASHBOARD, Permission.VIEW_PROFILE,
                Permission.VIEW_PATIENT, Permission.UPDATE_PATIENT,
                Permission.VIEW_APPOINTMENT, Permission.VIEW_EMERGENCY,
                Permission.VIEW_MEDICAL_RECORD, Permission.UPDATE_MEDICAL_RECORD,
                Permission.VIEW_PRESCRIPTION, Permission.VIEW_LAB,
                Permission.VIEW_VITALS, Permission.VIEW_OPERATION,
                Permission.VIEW_FOLLOW_UPS, Permission.VIEW_ADMISSIONS,
                Permission.VIEW_WARDS, Permission.VIEW_PHARMACY);
        p.addAll(EnumSet.of(Permission.CREATE_APPOINTMENT, Permission.UPDATE_APPOINTMENT,
                Permission.DELETE_APPOINTMENT, Permission.CREATE_MEDICAL_RECORD,
                Permission.CREATE_PRESCRIPTION, Permission.CREATE_LAB,
                Permission.MANAGE_OPERATION, Permission.MANAGE_FOLLOW_UPS,
                Permission.VIEW_DOCTORS, Permission.REQUEST_BLOOD,
                Permission.VIEW_REPORTS,
                Permission.VIEW_NOTIFICATIONS, Permission.UPDATE_EMERGENCY));
        return p;
    }

    private static Set<Permission> nurse() {
        // Nurse sees only ward-care modules: no Appointments (front desk)
        // and no Operations (doctor).
        EnumSet<Permission> p = EnumSet.of(Permission.VIEW_DASHBOARD, Permission.VIEW_PROFILE,
                Permission.VIEW_PATIENT, Permission.UPDATE_PATIENT,
                Permission.VIEW_EMERGENCY,
                Permission.VIEW_MEDICAL_RECORD, Permission.UPDATE_MEDICAL_RECORD,
                Permission.VIEW_PRESCRIPTION, Permission.VIEW_LAB,
                Permission.VIEW_VITALS, Permission.VIEW_FOLLOW_UPS,
                Permission.VIEW_ADMISSIONS,
                Permission.VIEW_WARDS, Permission.VIEW_PHARMACY,
                Permission.VIEW_BLOOD_BANK);
        p.addAll(EnumSet.of(Permission.UPDATE_MEDICAL_RECORD, Permission.UPDATE_VITALS,
                Permission.MANAGE_WARDS, Permission.MANAGE_ADMISSIONS,
                Permission.MANAGE_EQUIPMENT, Permission.VIEW_SHIFTS,
                Permission.MANAGE_SHIFTS, Permission.UPDATE_EMERGENCY,
                Permission.REQUEST_BLOOD, Permission.VIEW_NOTIFICATIONS,
                Permission.VIEW_EQUIPMENT));
        return p;
    }

    private static Set<Permission> receptionist() {
        return EnumSet.of(Permission.VIEW_DASHBOARD, Permission.VIEW_PROFILE,
                Permission.VIEW_PATIENT, Permission.CREATE_PATIENT, Permission.UPDATE_PATIENT,
                Permission.VIEW_APPOINTMENT, Permission.CREATE_APPOINTMENT,
                Permission.UPDATE_APPOINTMENT, Permission.DELETE_APPOINTMENT,
                Permission.VIEW_EMERGENCY, Permission.VIEW_ADMISSIONS,
                Permission.MANAGE_ADMISSIONS, Permission.VIEW_WARDS,
                Permission.VIEW_BILLING, Permission.MANAGE_BILLING,
                Permission.VIEW_DOCTORS, Permission.VIEW_REPORTS,
                Permission.VIEW_NOTIFICATIONS, Permission.VIEW_MEDICAL_RECORD);
    }

    private static Set<Permission> pharmacist() {
        // Dispensary only: no Patients directory, no Equipment.
        return EnumSet.of(Permission.VIEW_DASHBOARD, Permission.VIEW_PROFILE,
                Permission.VIEW_PHARMACY, Permission.MANAGE_PHARMACY,
                Permission.VIEW_PRESCRIPTION, Permission.VIEW_MEDICAL_RECORD,
                Permission.VIEW_BLOOD_BANK, Permission.MANAGE_BLOOD_BANK,
                Permission.VIEW_REPORTS, Permission.VIEW_NOTIFICATIONS);
    }

    private static Set<Permission> lab() {
        // Lab only: no Patients directory, no Emergency triage.
        return EnumSet.of(Permission.VIEW_DASHBOARD, Permission.VIEW_PROFILE,
                Permission.VIEW_LAB, Permission.CREATE_LAB, Permission.UPDATE_LAB_RESULT,
                Permission.VIEW_MEDICAL_RECORD,
                Permission.VIEW_REPORTS, Permission.VIEW_NOTIFICATIONS);
    }

    private static EnumSet<Permission> clinical() {
        return EnumSet.of(Permission.VIEW_DASHBOARD, Permission.VIEW_PROFILE,
                Permission.VIEW_PATIENT, Permission.UPDATE_PATIENT,
                Permission.VIEW_APPOINTMENT, Permission.VIEW_EMERGENCY,
                Permission.VIEW_MEDICAL_RECORD, Permission.UPDATE_MEDICAL_RECORD,
                Permission.VIEW_PRESCRIPTION, Permission.VIEW_LAB,
                Permission.VIEW_VITALS, Permission.VIEW_OPERATION,
                Permission.VIEW_FOLLOW_UPS, Permission.VIEW_ADMISSIONS,
                Permission.VIEW_WARDS, Permission.VIEW_PHARMACY,
                Permission.VIEW_BLOOD_BANK);
    }
}
