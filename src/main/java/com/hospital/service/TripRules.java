package com.hospital.service;

import com.hospital.enums.AmbulanceStatus;
import com.hospital.enums.TripStatus;
import com.hospital.exception.ConflictException;
import com.hospital.exception.InvalidDataException;
import com.hospital.model.Ambulance;
import com.hospital.model.AmbulanceTrip;
import com.hospital.repository.AmbulanceRepository;
import com.hospital.repository.AmbulanceTripRepository;

class TripRules {
    private final AmbulanceRepository ambulances;
    private final AmbulanceTripRepository trips;

    TripRules(AmbulanceRepository ambulances, AmbulanceTripRepository trips) {
        this.ambulances = ambulances;
        this.trips = trips;
    }

    Ambulance require(String id) {
        if (id == null || id.isBlank()) throw new InvalidDataException("Ambulance id required.");
        return ambulances.findById(id)
                .orElseThrow(() -> new InvalidDataException("Ambulance not found: " + id));
    }

    AmbulanceTrip requireTrip(String id) {
        if (id == null || id.isBlank()) throw new InvalidDataException("Trip id required.");
        return trips.findById(id)
                .orElseThrow(() -> new InvalidDataException("Trip not found: " + id));
    }

    void ensureTransition(AmbulanceTrip trip, TripStatus next) {
        if (!trip.getStatus().canTransitionTo(next)) {
            throw new ConflictException("Invalid trip transition "
                    + trip.getStatus() + " -> " + next);
        }
    }

    void validateVehicle(String vehicleNumber, String driverName) {
        if (vehicleNumber == null || vehicleNumber.isBlank()) {
            throw new InvalidDataException("Vehicle number required.");
        }
        if (driverName == null || driverName.isBlank()) {
            throw new InvalidDataException("Driver name required.");
        }
    }

    void validateUpdate(Ambulance ambulance) {
        if (ambulance == null || ambulance.getId() == null) {
            throw new InvalidDataException("Ambulance required.");
        }
    }

    void ensureRemovable(Ambulance a) {
        if (a.getStatus() == AmbulanceStatus.ON_TRIP) {
            throw new ConflictException("Cannot remove ambulance on trip: " + a.getId());
        }
    }

    void ensureEnterMaintenance(Ambulance a) {
        if (a.getStatus() == AmbulanceStatus.ON_TRIP) {
            throw new ConflictException("Ambulance on trip cannot enter maintenance: "
                    + a.getId());
        }
    }

    void ensureExitMaintenance(Ambulance a) {
        if (a.getStatus() != AmbulanceStatus.MAINTENANCE) {
            throw new InvalidDataException("Ambulance not in maintenance: " + a.getId());
        }
    }
}
