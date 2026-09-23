package com.hospital.service;

import com.hospital.model.Ambulance;
import com.hospital.model.BloodUnit;
import com.hospital.model.Equipment;
import com.hospital.model.FollowUp;
import com.hospital.model.Operation;
import com.hospital.model.StaffShift;
import com.hospital.model.VitalRecord;

import java.util.ArrayList;
import java.util.List;

public class AdvancedSearchService {
    public static final class Hit {
        public final String entityType;
        public final String id;
        public final String title;
        public final String status;
        public final String detail;

        public Hit(String entityType, String id, String title,
                   String status, String detail) {
            this.entityType = entityType;
            this.id = id;
            this.title = title;
            this.status = status;
            this.detail = detail;
        }
    }

    private final BloodBankService bloodBank;
    private final AmbulanceService ambulances;
    private final OperationService operations;
    private final VitalMonitoringService vitals;
    private final FollowUpService followUps;
    private final EquipmentService equipment;
    private final StaffShiftService shifts;

    public AdvancedSearchService() {
        this.bloodBank = new BloodBankService();
        this.ambulances = new AmbulanceService();
        this.operations = new OperationService();
        this.vitals = new VitalMonitoringService();
        this.followUps = new FollowUpService();
        this.equipment = new EquipmentService();
        this.shifts = new StaffShiftService();
    }

    public List<Hit> search(String query) {
        List<Hit> hits = new ArrayList<>();
        if (query == null || query.isBlank()) return hits;
        String q = query.trim().toLowerCase();
        for (BloodUnit u : bloodBank.search(q)) {
            hits.add(new Hit("Blood Unit", u.getId(),
                    u.getBloodGroup() != null ? u.getBloodGroup().name() : "-",
                    u.getStatus() != null ? u.getStatus().name() : "-",
                    u.getStorageLocation()));
        }
        for (Ambulance a : ambulances.search(q)) {
            hits.add(new Hit("Ambulance", a.getId(), a.getVehicleNumber(),
                    a.getStatus() != null ? a.getStatus().name() : "-", a.getDriverName()));
        }
        for (Operation o : operations.search(q)) {
            hits.add(new Hit("Operation", o.getId(), o.getOperationType(),
                    o.getStatus() != null ? o.getStatus().name() : "-", o.getRoomId()));
        }
        for (VitalRecord v : vitals.search(q)) {
            hits.add(new Hit("Vital", v.getId(), v.getPatientId(),
                    v.getOverallStatus() != null ? v.getOverallStatus().name() : "-",
                    "SpO2 " + v.getSpo2()));
        }
        for (FollowUp f : followUps.search(q)) {
            hits.add(new Hit("Follow-Up", f.getId(), f.getReason(),
                    f.getStatus() != null ? f.getStatus().name() : "-",
                    String.valueOf(f.getFollowUpDate())));
        }
        for (Equipment e : equipment.search(q)) {
            hits.add(new Hit("Equipment", e.getId(), e.getName(),
                    e.getStatus() != null ? e.getStatus().name() : "-", e.getDepartment()));
        }
        for (StaffShift s : shifts.search(q)) {
            hits.add(new Hit("Shift", s.getId(), s.getStaffName(),
                    s.getStatus() != null ? s.getStatus().name() : "-",
                    s.getShiftType() != null ? s.getShiftType().name() : "-"));
        }
        return hits;
    }
}
