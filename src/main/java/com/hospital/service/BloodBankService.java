package com.hospital.service;

import com.hospital.enums.AuditAction;
import com.hospital.enums.BloodGroup;
import com.hospital.enums.BloodUnitStatus;
import com.hospital.model.BloodDonor;
import com.hospital.model.BloodUnit;
import com.hospital.repository.BloodDonorRepository;
import com.hospital.repository.BloodUnitRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class BloodBankService {
    private final BloodUnitRepository units;
    private final BloodDonorRepository donors;
    private final NotificationService notifications;
    private final AuditLogService auditLogs;

    public BloodBankService() {
        this(BloodUnitRepository.DEFAULT_FILE, BloodDonorRepository.DEFAULT_FILE);
    }

    public BloodBankService(String unitFile, String donorFile) {
        this.units = new BloodUnitRepository(unitFile);
        this.donors = new BloodDonorRepository(donorFile);
        this.notifications = new NotificationService();
        this.auditLogs = new AuditLogService();
    }

    public BloodDonor registerDonor(String name, String phone, String bloodGroup,
                                    String rhType, int age, String gender) {
        return BloodMutations.register(donors, name, phone, bloodGroup, rhType, age, gender);
    }

    public BloodUnit addDonation(String donorId, String bloodGroup, String location) {
        BloodUnit unit = BloodMutations.donation(units, donors, donorId, bloodGroup, location);
        auditLogs.record("SYSTEM", AuditAction.BLOOD_DONATION, "BloodUnit", unit.getId(),
                "Blood unit collected from donor " + donorId);
        return unit;
    }

    public BloodUnit reserve(String unitId, String patientId) {
        return BloodMutations.reserve(units, unitId, patientId);
    }

    public BloodUnit issue(String unitId, String patientId) {
        BloodUnit unit = BloodMutations.issue(units, unitId, patientId);
        notifications.notify(com.hospital.enums.NotificationType.GENERAL,
                "Blood issued", unit.getId() + " issued to " + patientId, unit.getId());
        auditLogs.record("SYSTEM", AuditAction.BLOOD_ISSUE, "BloodUnit", unit.getId(),
                "Blood unit issued to patient " + patientId);
        return unit;
    }

    public BloodUnit markUsed(String unitId, String patientId) {
        return issue(unitId, patientId);
    }

    public List<BloodUnit> markExpired() {
        List<BloodUnit> expired = units.findExpired();
        for (BloodUnit u : expired) {
            if (u.getStatus() != BloodUnitStatus.EXPIRED && u.getStatus() != BloodUnitStatus.USED) {
                u.setStatus(BloodUnitStatus.EXPIRED);
                units.update(u);
            }
        }
        return expired;
    }

    public Map<BloodGroup, Long> availableCounts() {
        return BloodMutations.availableCounts(units);
    }

    public Map<BloodGroup, Long> lowStockGroups(int threshold) {
        return availableCounts().entrySet().stream().filter(e -> e.getValue() < threshold)
                .collect(java.util.stream.Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public List<BloodUnit> findCompatible(BloodGroup required) {
        return units.findAvailable(null).stream()
                .filter(u -> u.getBloodGroup() != null && u.getBloodGroup().compatibleDonorTo(required))
                .toList();
    }

    public List<BloodUnit> search(String query) {
        if (query == null || query.isBlank()) return units.findAll();
        String q = query.trim().toLowerCase();
        return units.findAll().stream()
                .filter(u -> (u.getId() != null && u.getId().toLowerCase().contains(q))
                        || (u.getDonorId() != null && u.getDonorId().toLowerCase().contains(q))
                        || (u.getStorageLocation() != null
                            && u.getStorageLocation().toLowerCase().contains(q)))
                .toList();
    }

    public List<BloodUnit> filterByGroup(BloodGroup group) {
        if (group == null) return units.findAll();
        return units.findAll().stream().filter(u -> u.getBloodGroup() == group).toList();
    }

    public List<BloodUnit> getAllUnits() { return units.findAll(); }
    public List<BloodDonor> getAllDonors() { return donors.findAll(); }
    public Optional<BloodUnit> findUnit(String id) { return units.findById(id); }
    public Optional<BloodDonor> findDonor(String id) { return donors.findById(id); }
    public int getUnitCount() { return units.count(); }
    public int getDonorCount() { return donors.count(); }
    public String nextUnitId() { return units.nextId(); }
    public String nextDonorId() { return donors.nextId(); }

    public void updateUnit(BloodUnit unit) {
        if (unit == null) throw new com.hospital.exception.InvalidDataException("Blood unit required.");
        units.update(unit);
    }

    public void removeUnit(String unitId) {
        units.delete(BloodMutations.requireUnit(units, unitId).getId());
    }
}
