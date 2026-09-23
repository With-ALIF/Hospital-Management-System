package com.hospital.service;

import com.hospital.enums.AppointmentStatus;
import com.hospital.exception.InvalidDataException;
import com.hospital.model.Appointment;
import com.hospital.repository.AppointmentRepository;

final class AppointmentTransitions {
    private final AppointmentRepository repository;

    AppointmentTransitions(AppointmentRepository repository) {
        this.repository = repository;
    }

    Appointment complete(String id) {
        Appointment appointment = require(id);
        if (appointment.getStatus() == AppointmentStatus.SCHEDULED) {
            appointment.setStatus(AppointmentStatus.COMPLETED);
            repository.update(appointment);
            return appointment;
        }
        return transition(id, AppointmentStatus.COMPLETED);
    }

    Appointment transition(String id, AppointmentStatus target) {
        Appointment appointment = require(id);
        AppointmentStatus current = appointment.getStatus();
        if (current == target) throw new InvalidDataException("Appointment " + id + " is already " + target + ".");
        if (current == AppointmentStatus.SCHEDULED && target == AppointmentStatus.COMPLETED) {
            appointment.setStatus(target);
            repository.update(appointment);
            return appointment;
        }
        if (!current.canTransitionTo(target)) {
            throw new InvalidDataException("Invalid status transition: " + current + " -> " + target
                    + " for appointment " + id + ".");
        }
        appointment.setStatus(target);
        repository.update(appointment);
        return appointment;
    }

    Appointment require(String id) {
        if (id == null || id.isBlank()) throw new InvalidDataException("Appointment id cannot be empty.");
        return repository.findById(id)
                .orElseThrow(() -> new InvalidDataException("Appointment not found: " + id));
    }
}
