package com.hospital.service;

import com.hospital.exception.InvalidDataException;
import com.hospital.model.Bed;
import com.hospital.model.Ward;
import com.hospital.repository.BedRepository;
import com.hospital.repository.WardRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class BedService {
    private final WardRepository wards;
    private final BedRepository beds;

    public BedService() {
        this(WardRepository.DEFAULT_FILE, BedRepository.DEFAULT_FILE);
    }

    public BedService(String wardFile, String bedFile) {
        this.wards = new WardRepository(wardFile);
        this.beds = new BedRepository(bedFile);
    }

    public Ward addWard(Ward ward) {
        if (ward == null || ward.getName() == null || ward.getName().isBlank()) {
            throw new InvalidDataException("Ward name cannot be empty.");
        }
        ward.setId(wards.nextWardId());
        wards.add(ward);
        return ward;
    }

    public Bed addBed(String wardId, com.hospital.enums.BedType type) {
        Ward ward = wards.findById(wardId)
                .orElseThrow(() -> new InvalidDataException("Ward not found: " + wardId));
        Bed bed = new Bed(beds.nextBedId(), ward.getId(), type);
        beds.add(bed);
        return bed;
    }

    public Bed assignBed(String bedId, String patientId) {
        Bed bed = requireBed(bedId);
        if (bed.getStatus() != com.hospital.enums.BedStatus.AVAILABLE) {
            throw new InvalidDataException("Bed is not available: " + bedId);
        }
        bed.setStatus(com.hospital.enums.BedStatus.OCCUPIED);
        bed.setPatientId(patientId);
        bed.setAssignedDate(LocalDate.now());
        beds.update(bed);
        return bed;
    }

    public Bed releaseBed(String bedId) {
        Bed bed = requireBed(bedId);
        if (bed.getStatus() == com.hospital.enums.BedStatus.AVAILABLE) {
            throw new InvalidDataException("Bed is already available: " + bedId);
        }
        bed.setStatus(com.hospital.enums.BedStatus.AVAILABLE);
        bed.setPatientId(null);
        bed.setAssignedDate(null);
        beds.update(bed);
        return bed;
    }

    public List<Ward> getAllWards() { return wards.findAll(); }
    public List<Bed> getAllBeds() { return beds.findAll(); }
    public List<Bed> getAvailableBeds() { return beds.findAvailable(); }
    public Optional<Bed> findBed(String id) { return beds.findById(id); }
    public Optional<Ward> findWard(String id) { return wards.findById(id); }
    public int getBedCount() { return beds.count(); }
    public int getWardCount() { return wards.count(); }
    public String nextBedId() { return beds.nextBedId(); }
    public String nextWardId() { return wards.nextWardId(); }

    private Bed requireBed(String bedId) {
        if (bedId == null || bedId.isBlank()) {
            throw new InvalidDataException("Bed id cannot be empty.");
        }
        return beds.findById(bedId)
                .orElseThrow(() -> new InvalidDataException("Bed not found: " + bedId));
    }
}
