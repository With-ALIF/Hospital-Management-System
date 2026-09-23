package com.hospital.ui.views;

import com.hospital.ui.AppState;
import javafx.scene.Node;
import javafx.scene.Scene;

public final class AppPages {
    private AppPages() {}

    public static Node build(String name, AppState state, Scene scene) {
        return switch (name) {
            case "Patients" -> PatientsView.build(state);
            case "Emergency" -> EmergencyView.build(state);
            case "Appointments" -> AppointmentsView.build(state);
            case "Doctors" -> DoctorsView.build(state);
            case "Doctor Schedule" -> DoctorScheduleView.build(state);
            case "Blood Bank" -> BloodBankView.build(state);
            case "Ambulance" -> AmbulanceView.build(state);
            case "Operations" -> OperationsView.build(state);
            case "Vital Monitoring" -> VitalMonitoringView.build(state);
            case "Follow-Ups" -> FollowUpsView.build(state);
            case "Equipment" -> EquipmentView.build(state);
            case "Staff Shifts" -> StaffShiftsView.build(state);
            case "Backups" -> BackupsView.build(state);
            case "Reports" -> ReportsView.build(state);
            case "Settings" -> SettingsView.build(scene);
            case "User Management" -> UserManagementView.build(state);
            case "Profile" -> ProfileView.build(state);
            default -> DashboardView.build(state,
                    () -> {}, () -> {});
        };
    }
}
