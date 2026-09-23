package com.hospital.service;

import com.hospital.enums.AmbulanceStatus;
import com.hospital.enums.OperationStatus;
import com.hospital.model.Ambulance;
import com.hospital.model.Operation;
import com.hospital.model.OperationRoom;
import com.hospital.model.StaffShift;
import com.hospital.model.VitalRecord;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class AdvancedReportService {
    private final BloodBankService bloodBank;
    private final AmbulanceService ambulances;
    private final OperationService operations;
    private final VitalMonitoringService vitals;
    private final FollowUpService followUps;
    private final EquipmentService equipment;
    private final StaffShiftService shifts;

    public AdvancedReportService() {
        this.bloodBank = new BloodBankService();
        this.ambulances = new AmbulanceService();
        this.operations = new OperationService();
        this.vitals = new VitalMonitoringService();
        this.followUps = new FollowUpService();
        this.equipment = new EquipmentService();
        this.shifts = new StaffShiftService();
    }

    public Map<String, Object> bloodBankReport(LocalDate from, LocalDate to) {
        return ReportMaps.bloodMap(
                BloodReportHelper.filterBlood(bloodBank.getAllUnits(), from, to));
    }

    public Map<String, Object> ambulanceReport(LocalDate from, LocalDate to) {
        List<Ambulance> all = ambulances.getAllAmbulances();
        long trips = ambulances.getAllTrips().stream()
                .filter(t -> ReportCounts.inRange(t.getStartTime() != null
                        ? t.getStartTime().toLocalDate() : null, from, to))
                .count();
        return ReportCounts.ambulanceMap(all, (int) trips, ambulances.getActiveTrips().size());
    }

    public Map<String, Object> operationReport(LocalDate from, LocalDate to) {
        List<Operation> all = operations.getAllOperations().stream()
                .filter(o -> ReportCounts.inRange(o.getDate(), from, to))
                .toList();
        long completed = all.stream().filter(o -> o.getStatus() == OperationStatus.COMPLETED).count();
        long cancelled = all.stream().filter(ReportCounts::cancelledOp).count();
        long emergency = all.stream().filter(ReportCounts::emergencyOp).count();
        return ReportMaps.operationMap(all, operations.getAllRooms(), operations.getToday().size(),
                completed, cancelled, emergency);
    }

    public Map<String, Object> vitalReport(LocalDate from, LocalDate to) {
        List<VitalRecord> all = vitals.getAll().stream()
                .filter(v -> ReportCounts.inRange(v.getRecordedAt() != null
                        ? v.getRecordedAt().toLocalDate() : null, from, to))
                .toList();
        return ReportMaps.vitalMap(all);
    }

    public Map<String, Object> followUpReport(LocalDate from, LocalDate to) {
        List<com.hospital.model.FollowUp> all = followUps.getAll().stream()
                .filter(f -> ReportCounts.inRange(f.getFollowUpDate(), from, to))
                .toList();
        return ReportMaps.followUpMap(all, followUps.getUpcoming().size(),
                followUps.getToday().size());
    }

    public Map<String, Object> equipmentReport(LocalDate from, LocalDate to) {
        return ReportMaps.equipmentMap(equipment.getAll());
    }

    public List<StaffShift> shiftsInRange(LocalDate from, LocalDate to) {
        return shifts.getAll().stream()
                .filter(s -> ReportCounts.inRange(s.getDate(), from, to))
                .toList();
    }
}
