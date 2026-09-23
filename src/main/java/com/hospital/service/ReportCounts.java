package com.hospital.service;

import com.hospital.enums.AmbulanceStatus;
import com.hospital.enums.OperationPriority;
import com.hospital.model.Ambulance;
import com.hospital.model.Operation;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

final class ReportCounts {
    private ReportCounts() {}

    static Map<String, Object> ambulanceMap(List<Ambulance> all, long trips, int active) {
        Map<String, Object> r = new LinkedHashMap<>();
        r.put("title", "Ambulance Report");
        r.put("total", all.size());
        r.put("available", count(all, AmbulanceStatus.AVAILABLE));
        r.put("onTrip", count(all, AmbulanceStatus.ON_TRIP));
        r.put("maintenance", count(all, AmbulanceStatus.MAINTENANCE));
        r.put("trips", trips);
        r.put("activeTrips", active);
        return r;
    }

    static long count(List<Ambulance> all, AmbulanceStatus status) {
        return all.stream().filter(a -> a.getStatus() == status).count();
    }

    static boolean emergencyOp(Operation o) {
        return o.getPriority() == OperationPriority.EMERGENCY;
    }

    static boolean cancelledOp(Operation o) {
        return o.getStatus() == com.hospital.enums.OperationStatus.CANCELLED;
    }

    static boolean inRange(LocalDate d, LocalDate from, LocalDate to) {
        return BloodReportHelper.inRange(d, from, to);
    }
}
