package com.hospital.service;

import com.hospital.exception.InvalidDataException;
import com.hospital.model.Staff;
import com.hospital.repository.StaffRepository;

import java.util.List;
import java.util.Optional;

public class StaffService {
    private final StaffRepository repository;

    public StaffService() {
        this(StaffRepository.DEFAULT_FILE);
    }

    public StaffService(String filePath) {
        this.repository = new StaffRepository(filePath);
    }

    public Staff hire(Staff staff) {
        if (staff == null || staff.getName() == null || staff.getName().isBlank()) {
            throw new InvalidDataException("Staff name cannot be empty.");
        }
        if (staff.getId() == null || staff.getId().isBlank()) {
            staff.setId(repository.nextStaffId());
        }
        if (staff.getEmail() != null && repository.findByEmail(staff.getEmail()).isPresent()) {
            throw new InvalidDataException("Staff email already exists: " + staff.getEmail());
        }
        repository.add(staff);
        return staff;
    }

    public Staff update(Staff staff) {
        if (staff == null || staff.getId() == null) {
            throw new InvalidDataException("Staff id cannot be empty.");
        }
        repository.update(staff);
        return staff;
    }

    public void remove(String staffId) {
        if (staffId == null || staffId.isBlank()) {
            throw new InvalidDataException("Staff id cannot be empty.");
        }
        repository.delete(staffId);
    }

    public Optional<Staff> find(String id) { return repository.findById(id); }
    public List<Staff> getAllStaff() { return repository.findAll(); }
    public int getStaffCount() { return repository.count(); }
    public String nextStaffId() { return repository.nextStaffId(); }
}
