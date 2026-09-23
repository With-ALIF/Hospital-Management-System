package com.hospital.service;

import com.hospital.enums.BloodUnitStatus;
import com.hospital.enums.EquipmentStatus;
import com.hospital.enums.FollowUpStatus;
import com.hospital.enums.OperationStatus;
import com.hospital.enums.RoomStatus;
import com.hospital.enums.VitalStatus;
import com.hospital.model.BloodUnit;
import com.hospital.model.Equipment;
import com.hospital.model.FollowUp;
import com.hospital.model.Operation;
import com.hospital.model.OperationRoom;
import com.hospital.model.VitalRecord;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

final class ReportMaps {
    private ReportMaps() {}

    static Map<String, Object> bloodMap(List<BloodUnit> all) {
        Map<String, Object> r = new LinkedHashMap<>();
        r.put("title", "Blood Bank Report");
        r.put("total", all.size());
        r.put("available", BloodReportHelper.countBlood(all, BloodUnitStatus.AVAILABLE));
        r.put("reserved", BloodReportHelper.countBlood(all, BloodUnitStatus.RESERVED));
        r.put("used", BloodReportHelper.countBlood(all, BloodUnitStatus.USED));
        r.put("expired", BloodReportHelper.countBlood(all, BloodUnitStatus.EXPIRED));
        Map<String, Long> byGroup = new LinkedHashMap<>();
        for (BloodUnit u : all) {
            String g = u.getBloodGroup() != null ? u.getBloodGroup().name() : "UNKNOWN";
            byGroup.merge(g, 1L, Long::sum);
        }
        r.put("byGroup", byGroup);
        return r;
    }

    static Map<String, Object> operationMap(List<Operation> all, List<OperationRoom> rooms,
                                            int today, long completed, long cancelled,
                                            long emergency) {
        Map<String, Object> r = new LinkedHashMap<>();
        r.put("title", "Operation Report");
        r.put("total", all.size());
        r.put("today", today);
        r.put("completed", completed);
        r.put("cancelled", cancelled);
        r.put("emergency", emergency);
        long occupied = rooms.stream()
                .filter(x -> x.getStatus() == RoomStatus.OCCUPIED).count();
        r.put("rooms", rooms.size());
        r.put("roomsOccupied", occupied);
        r.put("roomUtilization", rooms.isEmpty() ? 0.0 : (occupied * 100.0 / rooms.size()));
        return r;
    }

    static Map<String, Object> vitalMap(List<VitalRecord> all) {
        Map<String, Object> r = new LinkedHashMap<>();
        r.put("title", "Vital Monitoring Report");
        r.put("patientsMonitored", all.stream().map(VitalRecord::getPatientId)
                .filter(java.util.Objects::nonNull).distinct().count());
        r.put("records", all.size());
        r.put("warnings", all.stream()
                .filter(v -> v.getOverallStatus() == VitalStatus.WARNING).count());
        r.put("critical", all.stream()
                .filter(v -> v.getOverallStatus() == VitalStatus.CRITICAL).count());
        return r;
    }

    static Map<String, Object> followUpMap(List<FollowUp> all, int upcoming, int today) {
        Map<String, Object> r = new LinkedHashMap<>();
        r.put("title", "Follow-Up Report");
        r.put("upcoming", upcoming);
        r.put("today", today);
        r.put("completed", all.stream()
                .filter(f -> f.getStatus() == FollowUpStatus.COMPLETED).count());
        r.put("missed", all.stream()
                .filter(f -> f.getStatus() == FollowUpStatus.MISSED).count());
        r.put("cancelled", all.stream()
                .filter(f -> f.getStatus() == FollowUpStatus.CANCELLED).count());
        r.put("scheduled", all.stream()
                .filter(f -> f.getStatus() == FollowUpStatus.SCHEDULED).count());
        return r;
    }

    static Map<String, Object> equipmentMap(List<Equipment> all) {
        Map<String, Object> r = new LinkedHashMap<>();
        r.put("title", "Equipment Report");
        r.put("total", all.size());
        r.put("available", countEquip(all, EquipmentStatus.AVAILABLE));
        r.put("inUse", countEquip(all, EquipmentStatus.IN_USE));
        r.put("maintenance", countEquip(all, EquipmentStatus.MAINTENANCE));
        r.put("damaged", countEquip(all, EquipmentStatus.DAMAGED));
        r.put("retired", countEquip(all, EquipmentStatus.RETIRED));
        return r;
    }

    static long countEquip(List<Equipment> all, EquipmentStatus status) {
        return all.stream().filter(e -> e.getStatus() == status).count();
    }

    static boolean operationDone(Operation o) {
        return o.getStatus() == OperationStatus.COMPLETED;
    }
}
