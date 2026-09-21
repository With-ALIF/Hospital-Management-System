package com.hospital.ui;

import com.hospital.model.Doctor;
import com.hospital.model.Patient;
import javafx.util.StringConverter;

public class EntityConverters {
    public static StringConverter<Patient> patient() {
        return new StringConverter<Patient>() {
            @Override
            public String toString(Patient p) {
                return p != null ? p.getId() + " - " + p.getName() : "";
            }
            @Override
            public Patient fromString(String string) { return null; }
        };
    }

    public static StringConverter<Doctor> doctor() {
        return new StringConverter<Doctor>() {
            @Override
            public String toString(Doctor d) {
                return d != null ? d.getId() + " - " + d.getName() + " (" + d.getSpecialization() + ")" : "";
            }
            @Override
            public Doctor fromString(String string) { return null; }
        };
    }
}
