package com.hospital.service;

import com.hospital.enums.EquipmentStatus;
import com.hospital.model.Equipment;

import java.util.List;

final class EquipmentQueries {
    private EquipmentQueries() {}

    static List<Equipment> search(List<Equipment> all, String query) {
        if (query == null || query.isBlank()) return all;
        String q = query.trim().toLowerCase();
        return all.stream()
                .filter(e -> matches(e, q))
                .toList();
    }

    private static boolean matches(Equipment e, String q) {
        return (e.getId() != null && e.getId().toLowerCase().contains(q))
                || (e.getName() != null && e.getName().toLowerCase().contains(q))
                || (e.getCategory() != null && e.getCategory().toLowerCase().contains(q))
                || (e.getDepartment() != null && e.getDepartment().toLowerCase().contains(q))
                || (e.getStatus() != null && e.getStatus().name().toLowerCase().contains(q));
    }

    static long countByStatus(List<Equipment> all, EquipmentStatus status) {
        return all.stream().filter(e -> e.getStatus() == status).count();
    }
}
