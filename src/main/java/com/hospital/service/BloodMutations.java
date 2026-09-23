package com.hospital.service;

import com.hospital.enums.BloodUnitStatus;
import com.hospital.enums.RhType;
import com.hospital.enums.BloodGroup;
import com.hospital.exception.ExpiredResourceException;
import com.hospital.exception.InvalidDataException;
import com.hospital.exception.ResourceUnavailableException;
import com.hospital.model.BloodDonor;
import com.hospital.model.BloodUnit;
import com.hospital.repository.BloodDonorRepository;
import com.hospital.repository.BloodUnitRepository;

import java.time.LocalDate;
import java.util.Map;
import java.util.stream.Collectors;

final class BloodMutations {
    private BloodMutations() {}

    static BloodDonor register(BloodDonorRepository donors, String name, String phone,
                               String bloodGroup, String rhType, int age, String gender) {
        if (name == null || name.isBlank()) throw new InvalidDataException("Donor name required.");
        if (phone == null || phone.isBlank()) throw new InvalidDataException("Donor phone required.");
        if (age < 18 || age > 65) throw new InvalidDataException("Donor age must be 18-65.");
        BloodDonor donor = new BloodDonor(donors.nextId(), name, phone,
                BloodGroup.from(bloodGroup), RhType.from(rhType));
        donor.setAge(age);
        donor.setGender(gender);
        donors.add(donor);
        return donor;
    }

    static BloodUnit donation(BloodUnitRepository units, BloodDonorRepository donors,
                              String donorId, String bloodGroup, String location) {
        BloodDonor donor = donors.findById(donorId)
                .orElseThrow(() -> new InvalidDataException("Donor not found: " + donorId));
        LocalDate today = LocalDate.now();
        if (donor.getLastDonationDate() != null
                && donor.getLastDonationDate().plusDays(90).isAfter(today)) {
            throw new InvalidDataException("Donor must wait 90 days between donations.");
        }
        BloodGroup group = BloodGroup.from(bloodGroup);
        BloodUnit unit = new BloodUnit(units.nextId(), group,
                RhType.from(group.name().endsWith("NEG") ? "NEGATIVE" : "POSITIVE"),
                today, today.plusDays(42), donorId);
        unit.setStorageLocation(location != null && !location.isBlank() ? location : "BANK-A1");
        units.add(unit);
        donor.setLastDonationDate(today);
        donor.setTotalDonations(donor.getTotalDonations() + 1);
        donors.update(donor);
        return unit;
    }

    static BloodUnit reserve(BloodUnitRepository units, String unitId, String patientId) {
        BloodUnit unit = requireUnit(units, unitId);
        refreshExpiry(units, unit);
        if (unit.getStatus() != BloodUnitStatus.AVAILABLE) {
            throw new ResourceUnavailableException("Blood unit not available: " + unitId);
        }
        unit.setStatus(BloodUnitStatus.RESERVED);
        unit.setReservedForPatientId(patientId);
        units.update(unit);
        return unit;
    }

    static BloodUnit issue(BloodUnitRepository units, String unitId, String patientId) {
        BloodUnit unit = requireUnit(units, unitId);
        refreshExpiry(units, unit);
        if (unit.getStatus() == BloodUnitStatus.EXPIRED || unit.isExpired(LocalDate.now())) {
            throw new ExpiredResourceException("Cannot issue expired blood unit: " + unitId);
        }
        if (unit.getStatus() != BloodUnitStatus.AVAILABLE
                && unit.getStatus() != BloodUnitStatus.RESERVED) {
            throw new ResourceUnavailableException("Blood unit not issuable: " + unitId
                    + " status=" + unit.getStatus());
        }
        unit.setStatus(BloodUnitStatus.USED);
        unit.setIssuedToPatientId(patientId);
        unit.setIssueDate(LocalDate.now());
        units.update(unit);
        return unit;
    }

    static Map<BloodGroup, Long> availableCounts(BloodUnitRepository units) {
        return units.findAvailable(null).stream()
                .collect(Collectors.groupingBy(BloodUnit::getBloodGroup, Collectors.counting()));
    }

    static BloodUnit requireUnit(BloodUnitRepository units, String unitId) {
        if (unitId == null || unitId.isBlank()) {
            throw new InvalidDataException("Blood unit id required.");
        }
        return units.findById(unitId)
                .orElseThrow(() -> new InvalidDataException("Blood unit not found: " + unitId));
    }

    static void refreshExpiry(BloodUnitRepository units, BloodUnit unit) {
        if (unit.isExpired(LocalDate.now()) && unit.getStatus() != BloodUnitStatus.USED
                && unit.getStatus() != BloodUnitStatus.EXPIRED) {
            unit.setStatus(BloodUnitStatus.EXPIRED);
            units.update(unit);
        }
    }
}
