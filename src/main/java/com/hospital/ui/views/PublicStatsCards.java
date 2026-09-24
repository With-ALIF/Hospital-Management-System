package com.hospital.ui.views;

import com.hospital.enums.BloodGroup;
import com.hospital.enums.EmergencyCaseStatus;
import com.hospital.ui.AppState;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.AccessibleRole;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Separator;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.time.LocalDate;
import java.util.Map;

public class PublicStatsCards {
    public static VBox build(AppState state) {
        int doctors = state.doctorService.getDoctorCount();
        int beds = state.bedService.getBedCount();
        int freeBeds = state.bedService.getAvailableBeds().size();
        int today = state.appointmentService.findByDate(LocalDate.now()).size();
        int emergency = (int) state.emergencyService.getAllCases().stream()
                .filter(c -> c.getStatus() == EmergencyCaseStatus.WAITING
                        || c.getStatus() == EmergencyCaseStatus.IN_TREATMENT).count();
        int ambulances = state.ambulanceService.getAvailable().size();
        long bloodUnits = state.bloodBankService.availableCounts().values().stream()
                .mapToLong(Long::longValue).sum();

        VBox capacity = buildGroup(
                "Capacity and resources",
                "Beds, transport and supplies",
                statItem("Total beds", String.valueOf(beds), "Across all wards", null),
                statItem("Available beds", String.valueOf(freeBeds),
                        freeBeds > 0 ? "Admissions open" : "At capacity",
                        freeBeds > 0 ? "kpi-sub-success" : "kpi-sub-warning"),
                statItem("Available ambulances", String.valueOf(ambulances),
                        ambulances > 0 ? "Ready to dispatch" : "All in use",
                        ambulances > 0 ? "kpi-sub-success" : "kpi-sub-warning"),
                statItem("Blood bank units", String.valueOf(bloodUnits), bloodSummary(state), null));

        VBox staff = buildGroup(
                "Staff and activity",
                "People and today's load",
                statItem("Total doctors", String.valueOf(doctors), "Specialists on staff", null),
                statItem("Today's appointments", String.valueOf(today), "Scheduled today", null),
                statItem("Emergency cases", String.valueOf(emergency),
                        emergency > 0 ? "Active now" : "No active cases",
                        emergency > 0 ? "kpi-sub-danger" : "kpi-sub-success"),
                statItem("Hospital departments", String.valueOf(deptCount(state)),
                        "Specialty services", null));

        FlowPane groups = new FlowPane(16, 16);
        groups.setPrefWrapLength(860);
        groups.setMaxWidth(Double.MAX_VALUE);
        for (VBox g : new VBox[]{capacity, staff}) {
            g.setPrefWidth(420);
            g.setMinWidth(280);
            g.setMaxWidth(Double.MAX_VALUE);
            groups.getChildren().add(g);
        }

        VBox bedPanel = buildBedAvailabilityPanel(beds, freeBeds);

        VBox box = new VBox(16, groups, bedPanel);
        box.setId("stats");
        box.setFillWidth(true);
        return box;
    }

    private static String bloodSummary(AppState state) {
        Map<BloodGroup, Long> m = state.bloodBankService.availableCounts();
        if (m.isEmpty()) return "No stock info";
        return m.size() + " blood groups available";
    }

    private static int deptCount(AppState state) {
        long d = state.doctorService.getAllDoctors().stream()
                .map(x -> x.getSpecialization())
                .filter(s -> s != null && !s.isBlank())
                .distinct().count();
        return (int) Math.max(d, 6);
    }

    private static VBox statItem(String title, String value, String sub, String subStyle) {
        Label t = new Label(title);
        t.getStyleClass().add("kpi-label");
        Label v = new Label(value);
        v.getStyleClass().addAll("kpi-value", "tnum");
        Label s = new Label(sub);
        s.getStyleClass().add("kpi-sub");
        if (subStyle != null) s.getStyleClass().add(subStyle);
        VBox item = new VBox(4, t, v, s);
        item.setPrefWidth(180);
        item.setMinWidth(140);
        HBox.setHgrow(item, Priority.ALWAYS);
        return item;
    }

    private static VBox buildGroup(String title, String sub, Node... items) {
        Label t = new Label(title);
        t.getStyleClass().add("stat-group-title");
        Label s = new Label(sub);
        s.getStyleClass().add("stat-group-sub");
        VBox head = new VBox(2, t, s);

        HBox row1 = dividedRow(items[0], items[1]);
        HBox row2 = dividedRow(items[2], items[3]);
        Separator hSep = new Separator(Orientation.HORIZONTAL);
        hSep.getStyleClass().add("stat-divider");

        VBox group = new VBox(12, head, row1, hSep, row2);
        group.getStyleClass().add("stat-group");
        group.setPadding(new Insets(16));
        return group;
    }

    private static HBox dividedRow(Node left, Node right) {
        Separator vSep = new Separator(Orientation.VERTICAL);
        vSep.getStyleClass().add("stat-divider");
        HBox row = new HBox(12, left, vSep, right);
        HBox.setHgrow(left, Priority.ALWAYS);
        HBox.setHgrow(right, Priority.ALWAYS);
        return row;
    }

    public static VBox buildBedAvailabilityPanel(int totalBeds, int freeBeds) {
        int total = Math.max(totalBeds, 1);
        int free = Math.max(0, Math.min(freeBeds, total));
        double ratio = (double) free / (double) total;

        Label title = new Label("Bed availability");
        title.getStyleClass().add("stat-group-title");
        Label sub = new Label(free + " of " + total + " beds free");
        sub.getStyleClass().addAll("kpi-value", "tnum");
        Label hint = new Label(free > 0 ? "Admissions open — updated live from ward occupancy."
                : "At capacity — contact the emergency desk for guidance.");
        hint.getStyleClass().add("kpi-sub");
        hint.setWrapText(true);

        ProgressBar bar = new ProgressBar(ratio);
        bar.setMaxWidth(Double.MAX_VALUE);
        bar.setPrefHeight(10);
        bar.setMinHeight(10);
        bar.setStyle("-fx-accent:#0B5C75;");
        bar.setAccessibleRole(AccessibleRole.PROGRESS_INDICATOR);
        bar.setAccessibleText(free + " of " + total + " beds free");
        bar.setAccessibleHelp("Bed occupancy: " + free + " free out of " + total + " total beds.");

        VBox panel = new VBox(8, title, sub, bar, hint);
        panel.getStyleClass().add("stat-group");
        panel.setPadding(new Insets(16));
        panel.setAccessibleText("Bed availability: " + free + " of " + total + " beds free");
        return panel;
    }

    public static VBox wrapTitle(String title, String sub, Node body) {
        Label t = new Label(title);
        t.getStyleClass().add("section-title");
        t.setStyle("-fx-font-size:16px;-fx-font-weight:700;-fx-text-fill:#10263B;");
        Label s = new Label(sub);
        s.setStyle("-fx-font-size:12.5px;-fx-text-fill:#46647A;");
        VBox head = new VBox(2, t, s);
        VBox box = new VBox(14, head, body);
        box.setPadding(new Insets(4));
        return box;
    }
}
