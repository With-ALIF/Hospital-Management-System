package com.hospital.service;

import com.hospital.enums.AmbulanceStatus;
import com.hospital.model.Ambulance;
import com.hospital.model.AmbulanceTrip;
import com.hospital.repository.AmbulanceRepository;
import com.hospital.repository.AmbulanceTripRepository;

import java.util.List;
import java.util.Optional;

public class AmbulanceService {
    private final AmbulanceRepository ambulances;
    private final AmbulanceTripRepository trips;
    private final TripRules rules;
    private final AmbulanceDispatch dispatch;

    public AmbulanceService() {
        this(AmbulanceRepository.DEFAULT_FILE, AmbulanceTripRepository.DEFAULT_FILE);
    }

    public AmbulanceService(String ambulanceFile, String tripFile) {
        this.ambulances = new AmbulanceRepository(ambulanceFile);
        this.trips = new AmbulanceTripRepository(tripFile);
        this.rules = new TripRules(ambulances, trips);
        this.dispatch = new AmbulanceDispatch(ambulances, trips);
    }

    public Ambulance add(String vehicleNumber, String driverName, String driverPhone,
                         com.hospital.enums.AmbulanceType type) {
        rules.validateVehicle(vehicleNumber, driverName);
        Ambulance a = new Ambulance(ambulances.nextId(), vehicleNumber,
                driverName, driverPhone, type);
        ambulances.add(a);
        return a;
    }

    public Ambulance update(Ambulance ambulance) {
        rules.validateUpdate(ambulance);
        ambulances.update(ambulance);
        return ambulance;
    }

    public void remove(String ambulanceId) {
        Ambulance a = rules.require(ambulanceId);
        rules.ensureRemovable(a);
        ambulances.delete(a.getId());
    }

    public Ambulance setMaintenance(String ambulanceId) {
        Ambulance a = rules.require(ambulanceId);
        rules.ensureEnterMaintenance(a);
        a.setStatus(AmbulanceStatus.MAINTENANCE);
        ambulances.update(a);
        return a;
    }

    public Ambulance completeMaintenance(String ambulanceId) {
        Ambulance a = rules.require(ambulanceId);
        rules.ensureExitMaintenance(a);
        a.setStatus(AmbulanceStatus.AVAILABLE);
        a.setLastServiceDate(java.time.LocalDate.now());
        ambulances.update(a);
        return a;
    }

    public AmbulanceTrip assign(String ambulanceId, String emergencyCaseId,
                                String patientId, String pickupLocation) {
        return dispatch.assign(ambulanceId, emergencyCaseId, patientId, pickupLocation);
    }

    public AmbulanceTrip startTrip(String tripId) { return dispatch.startTrip(tripId); }
    public AmbulanceTrip pickUp(String tripId) { return dispatch.pickUp(tripId); }
    public AmbulanceTrip completeTrip(String tripId) { return dispatch.completeTrip(tripId); }
    public AmbulanceTrip cancelTrip(String tripId) { return dispatch.cancelTrip(tripId); }

    public AmbulanceTrip requestForEmergency(String emergencyCaseId, String patientId,
                                             String location) {
        return dispatch.requestForEmergency(emergencyCaseId, patientId, location);
    }

    public Ambulance assignDriver(String ambulanceId, String driverName, String driverPhone) {
        Ambulance a = rules.require(ambulanceId);
        a.setDriverName(driverName);
        a.setDriverPhone(driverPhone);
        ambulances.update(a);
        return a;
    }

    public List<Ambulance> search(String query) { return dispatch.search(query); }

    public List<Ambulance> getAllAmbulances() { return ambulances.findAll(); }
    public List<Ambulance> getAvailable() { return ambulances.findAvailable(); }
    public List<AmbulanceTrip> getAllTrips() { return trips.findAll(); }
    public List<AmbulanceTrip> getActiveTrips() { return trips.findActive(); }
    public Optional<Ambulance> find(String id) { return ambulances.findById(id); }
    public Optional<AmbulanceTrip> findTrip(String id) { return trips.findById(id); }
    public int getAmbulanceCount() { return ambulances.count(); }
    public int getTripCount() { return trips.count(); }
    public String nextId() { return ambulances.nextId(); }
    public String nextTripId() { return trips.nextId(); }
}
