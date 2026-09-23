package com.hospital.ui.views;

import com.hospital.enums.StaffRole;
import com.hospital.service.PharmacyService;
import com.hospital.service.LabService;
import com.hospital.service.BloodBankService;
import com.hospital.service.VitalMonitoringService;
import com.hospital.service.FollowUpService;
import com.hospital.ui.AppState;
import com.hospital.ui.components.StatCard;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import java.time.LocalDate;

public final class DashboardRoleKpis {
    private DashboardRoleKpis() {}

    public static HBox build(AppState state, StaffRole role) {
        HBox kpis = new HBox(16);
        int patients = state.patients.size();
        long docs = state.doctors.stream().filter(d -> d.getAvailable()).count();
        long waiting = state.emergencyCases.stream()
                .filter(e -> "WAITING".equals(e.getStatus().name())).count();
        int todayAppts = (int) state.appointments.stream()
                .filter(a -> LocalDate.now().equals(a.getDate())).count();
        if (role == StaffRole.PHARMACIST) {
            addCards(kpis, StatCard.create("Medicines",
                    String.valueOf(new PharmacyService().getMedicineCount()),
                    "In stock", null),
                    StatCard.create("Patients", String.valueOf(patients), "Registered", null),
                    StatCard.create("Prescriptions", "—", "View Rx list", null),
                    StatCard.create("Blood Units",
                            String.valueOf(new BloodBankService().getUnitCount()),
                            "Available", null));
            return kpis;
        }
        if (role == StaffRole.LAB_TECHNICIAN) {
            addCards(kpis, StatCard.create("Lab Tests",
                    String.valueOf(new LabService().getTestCount()), "Total", null),
                    StatCard.create("Patients", String.valueOf(patients), "Registered", null),
                    StatCard.create("Emergency", String.format("%02d", waiting),
                            "Waiting", null),
                    StatCard.create("Appointments", String.valueOf(todayAppts), "Today", null));
            return kpis;
        }
        if (role == StaffRole.NURSE) {
            long crit = critical(state);
            addCards(kpis, StatCard.create("Patients", String.valueOf(patients),
                    "+" + Math.min(12, patients) + " today", null),
                    StatCard.create("Vitals",
                            String.valueOf(new VitalMonitoringService().getCount()),
                            "Recorded", null),
                    StatCard.create("Emergency", String.format("%02d", waiting),
                            crit + " Critical", crit > 0 ? "kpi-sub-danger" : "kpi-sub-success"),
                    StatCard.create("Follow-Ups",
                            String.valueOf(new FollowUpService().getUpcoming().size()),
                            "Upcoming", null));
            return kpis;
        }
        if (role == StaffRole.RECEPTIONIST) {
            addCards(kpis, StatCard.create("Patients", String.valueOf(patients),
                    "+" + Math.min(12, patients) + " today", null),
                    StatCard.create("Appointments", String.valueOf(todayAppts),
                            state.appointments.size() + " Total", null),
                    StatCard.create("Doctors", String.valueOf(state.doctors.size()),
                            docs + " Available", "kpi-sub-success"),
                    StatCard.create("Emergency", String.format("%02d", waiting),
                            "Waiting", null));
            return kpis;
        }
        long crit = critical(state);
        addCards(kpis, StatCard.create("Total Patients", String.valueOf(patients),
                "+" + Math.min(12, patients) + " today", null),
                StatCard.create("Doctors", String.valueOf(state.doctors.size()),
                        docs + " Available", "kpi-sub-success"),
                StatCard.create("Emergency", String.format("%02d", waiting),
                        crit + " Critical",
                        crit > 0 ? "kpi-sub-danger" : "kpi-sub-success"),
                StatCard.create("Appointments", String.valueOf(todayAppts),
                        state.appointments.size() + " Total  •  Today", null));
        return kpis;
    }

    private static long critical(AppState state) {
        return state.emergencyCases.stream()
                .filter(e -> "CRITICAL".equalsIgnoreCase(String.valueOf(e.getPriority()))
                        && "WAITING".equals(e.getStatus().name())).count();
    }

    private static void addCards(HBox kpis, Node... cards) {
        kpis.getChildren().addAll(cards);
        for (var n : kpis.getChildren()) HBox.setHgrow((Region) n, Priority.ALWAYS);
    }
}
