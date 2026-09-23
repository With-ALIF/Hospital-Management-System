package com.hospital.service;

import com.hospital.enums.BloodUnitStatus;
import com.hospital.model.BloodUnit;

import java.time.LocalDate;
import java.util.List;

final class BloodReportHelper {
    private BloodReportHelper() {}

    static List<BloodUnit> filterBlood(List<BloodUnit> units, LocalDate from, LocalDate to) {
        return units.stream()
                .filter(u -> inRange(u.getCollectionDate(), from, to))
                .toList();
    }

    static long countBlood(List<BloodUnit> units, BloodUnitStatus status) {
        return units.stream().filter(u -> u.getStatus() == status).count();
    }

    static boolean inRange(LocalDate d, LocalDate from, LocalDate to) {
        if (d == null) return true;
        if (from != null && d.isBefore(from)) return false;
        if (to != null && d.isAfter(to)) return false;
        return true;
    }
}
