package com.hospital.service.factory;

import com.hospital.enums.Gender;
import com.hospital.enums.UserRole;
import com.hospital.exception.InvalidDataException;
import com.hospital.model.Admin;
import com.hospital.model.Doctor;
import com.hospital.model.LabTechnician;
import com.hospital.model.Nurse;
import com.hospital.model.Pharmacist;
import com.hospital.model.Receptionist;
import com.hospital.model.Staff;

public final class StaffFactory {
    private StaffFactory() {
    }

    public static Staff create(UserRole role, String id, String name,
                               String phone, String gender) {
        if (role == null) throw new InvalidDataException("Staff role required.");
        if (name == null || name.isBlank()) {
            throw new InvalidDataException("Staff name required.");
        }
        Gender g = Gender.from(gender);
        return switch (role) {
            case DOCTOR -> new Doctor(id, name, phone, gender, "General");
            case NURSE -> new Nurse(id, name, phone, gender);
            case RECEPTIONIST -> new Receptionist(id, name, phone, gender);
            case PHARMACIST -> new Pharmacist(id, name, phone, gender);
            case LAB_TECHNICIAN -> new LabTechnician(id, name, phone, gender);
            case ADMIN -> new Admin(id, name, phone, gender);
            default -> throw new InvalidDataException("Unsupported staff role: " + role);
        };
    }
}
