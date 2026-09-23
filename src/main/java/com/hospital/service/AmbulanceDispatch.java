package com.hospital.service;

import com.hospital.enums.AmbulanceStatus;
import com.hospital.enums.HospitalEventType;
import com.hospital.enums.TripStatus;
import com.hospital.exception.ResourceUnavailableException;
import com.hospital.model.Ambulance;
import com.hospital.model.AmbulanceTrip;
import com.hospital.repository.AmbulanceRepository;
import com.hospital.repository.AmbulanceTripRepository;

import java.time.LocalDateTime;
import java.util.List;

class AmbulanceDispatch {
    private final AmbulanceRepository ambulances;
    private final AmbulanceTripRepository trips;
    private final TripRules rules;
    private final NotificationService notifications;
    private final AuditLogService auditLogs;
    private final EventBus eventBus;

    AmbulanceDispatch(AmbulanceRepository ambulances, AmbulanceTripRepository trips) {
        this.ambulances = ambulances;
        this.trips = trips;
        this.rules = new TripRules(ambulances, trips);
        this.notifications = new NotificationService();
        this.auditLogs = new AuditLogService();
        this.eventBus = EventBus.getInstance();
    }

    AmbulanceTrip assign(String ambulanceId, String emergencyCaseId,
                         String patientId, String pickupLocation) {
        Ambulance a = rules.require(ambulanceId);
        if (!a.getStatus().assignable()) {
            throw new ResourceUnavailableException("Ambulance not available: " + ambulanceId
                    + " status=" + a.getStatus());
        }
        AmbulanceTrip trip = new AmbulanceTrip(trips.nextId(), ambulanceId,
                a.getDriverName(), emergencyCaseId, patientId, pickupLocation);
        trips.add(trip);
        a.setStatus(AmbulanceStatus.ON_TRIP);
        a.setAssignedTripId(trip.getId());
        ambulances.update(a);
        notifications.notify(com.hospital.enums.NotificationType.EMERGENCY_ALERT,
                "Ambulance assigned", a.getVehicleNumber() + " assigned to " + trip.getId(), trip.getId());
        auditLogs.record("SYSTEM", com.hospital.enums.AuditAction.AMBULANCE_ASSIGN,
                "Ambulance", ambulanceId, "Assigned to trip " + trip.getId());
        eventBus.publish(HospitalEventType.AMBULANCE_ASSIGNED, ambulanceId, "Ambulance",
                "Ambulance " + ambulanceId + " assigned to trip " + trip.getId());
        return trip;
    }

    AmbulanceTrip startTrip(String tripId) {
        AmbulanceTrip t = rules.requireTrip(tripId);
        rules.ensureTransition(t, TripStatus.EN_ROUTE);
        t.setStatus(TripStatus.EN_ROUTE);
        trips.update(t);
        return t;
    }

    AmbulanceTrip pickUp(String tripId) {
        AmbulanceTrip t = rules.requireTrip(tripId);
        rules.ensureTransition(t, TripStatus.PICKED_UP);
        t.setStatus(TripStatus.PICKED_UP);
        trips.update(t);
        return t;
    }

    AmbulanceTrip completeTrip(String tripId) {
        AmbulanceTrip t = rules.requireTrip(tripId);
        rules.ensureTransition(t, TripStatus.COMPLETED);
        t.setStatus(TripStatus.COMPLETED);
        t.setEndTime(LocalDateTime.now());
        trips.update(t);
        releaseAmbulance(t.getAmbulanceId(), true);
        auditLogs.record("SYSTEM", com.hospital.enums.AuditAction.TRIP_COMPLETE, "AmbulanceTrip", tripId, "Trip completed");
        return t;
    }

    AmbulanceTrip cancelTrip(String tripId) {
        AmbulanceTrip t = rules.requireTrip(tripId);
        rules.ensureTransition(t, TripStatus.CANCELLED);
        t.setStatus(TripStatus.CANCELLED);
        t.setEndTime(LocalDateTime.now());
        trips.update(t);
        releaseAmbulance(t.getAmbulanceId(), false);
        return t;
    }

    AmbulanceTrip requestForEmergency(String emergencyCaseId, String patientId,
                                      String location) {
        List<Ambulance> available = ambulances.findAvailable();
        if (available.isEmpty()) {
            throw new ResourceUnavailableException("No available ambulance for " + emergencyCaseId);
        }
        return assign(available.get(0).getId(), emergencyCaseId, patientId, location);
    }

    List<Ambulance> search(String query) {
        if (query == null || query.isBlank()) return ambulances.findAll();
        String q = query.trim().toLowerCase();
        return ambulances.findAll().stream()
                .filter(a -> (a.getId() != null && a.getId().toLowerCase().contains(q))
                        || (a.getVehicleNumber() != null && a.getVehicleNumber().toLowerCase().contains(q))
                        || (a.getDriverName() != null && a.getDriverName().toLowerCase().contains(q))
                        || (a.getStatus() != null && a.getStatus().name().toLowerCase().contains(q)))
                .toList();
    }

    private void releaseAmbulance(String ambulanceId, boolean resetLocation) {
        Ambulance a = ambulances.findById(ambulanceId).orElse(null);
        if (a == null) return;
        a.setStatus(AmbulanceStatus.AVAILABLE);
        a.setAssignedTripId(null);
        if (resetLocation) a.setCurrentLocation("HOSPITAL");
        ambulances.update(a);
    }
}
