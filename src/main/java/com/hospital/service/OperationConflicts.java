package com.hospital.service;

import com.hospital.enums.OperationStatus;
import com.hospital.enums.RoomStatus;
import com.hospital.exception.ConflictException;
import com.hospital.exception.InvalidDataException;
import com.hospital.exception.ResourceUnavailableException;
import com.hospital.model.Operation;
import com.hospital.model.OperationRoom;
import com.hospital.repository.OperationRepository;
import com.hospital.repository.OperationRoomRepository;

import java.time.LocalDate;
import java.time.LocalTime;

class OperationConflicts {
    private final OperationRepository operations;
    private final OperationRoomRepository rooms;

    OperationConflicts(OperationRepository operations, OperationRoomRepository rooms) {
        this.operations = operations;
        this.rooms = rooms;
    }

    void removeRoomIfFree(String roomId) {
        OperationRoom room = rooms.findById(roomId)
                .orElseThrow(() -> new InvalidDataException("Room not found: " + roomId));
        if (room.getStatus() == RoomStatus.OCCUPIED) {
            throw new ConflictException("Room occupied, cannot delete: " + roomId);
        }
        rooms.delete(room.getId());
    }

    void validateSchedule(String patientId, String surgeonId, String roomId,
                          LocalDate date, LocalTime start, LocalTime end) {
        if (patientId == null || patientId.isBlank()) {
            throw new InvalidDataException("Patient required for operation.");
        }
        if (surgeonId == null || surgeonId.isBlank()) {
            throw new InvalidDataException("Surgeon required for operation.");
        }
        if (date == null || start == null || end == null) {
            throw new InvalidDataException("Date, start and end time required.");
        }
        if (!start.isBefore(end)) {
            throw new InvalidDataException("Start time must be before end time.");
        }
        OperationRoom room = rooms.findById(roomId)
                .orElseThrow(() -> new InvalidDataException("Room not found: " + roomId));
        if (room.getStatus() == RoomStatus.MAINTENANCE) {
            throw new ResourceUnavailableException("Room under maintenance: " + roomId);
        }
        ensureNoConflict(roomId, surgeonId, patientId, date, start, end);
    }

    void ensureNoConflict(String roomId, String surgeonId, String patientId,
                          LocalDate date, LocalTime start, LocalTime end) {
        for (Operation o : operations.findByDate(date)) {
            if (o.getStatus() == OperationStatus.CANCELLED
                    || o.getStatus() == OperationStatus.COMPLETED) {
                continue;
            }
            boolean overlap = o.overlaps(date, start, end);
            if (overlap && roomId != null && roomId.equals(o.getRoomId())) {
                throw new ConflictException("Room already booked: " + roomId);
            }
            if (overlap && surgeonId != null && surgeonId.equals(o.getSurgeonId())) {
                throw new ConflictException("Surgeon already booked: " + surgeonId);
            }
            if (overlap && patientId != null && patientId.equals(o.getPatientId())) {
                throw new ConflictException("Patient already has overlapping operation: "
                        + patientId);
            }
        }
    }

    boolean isRoomFree(String roomId, LocalDate date, LocalTime start, LocalTime end) {
        return operations.findByDate(date).stream()
                .filter(o -> o.getStatus() != OperationStatus.CANCELLED
                        && o.getStatus() != OperationStatus.COMPLETED)
                .noneMatch(o -> roomId != null && roomId.equals(o.getRoomId())
                        && o.overlaps(date, start, end));
    }

    void requireTransition(Operation op, OperationStatus next) {
        if (!op.getStatus().canTransitionTo(next)) {
            throw new ConflictException("Invalid operation transition "
                    + op.getStatus() + " -> " + next);
        }
    }

    void updateRoomOccupancy(Operation op, OperationStatus prev, OperationStatus next) {
        OperationRoom room = rooms.findById(op.getRoomId()).orElse(null);
        if (room == null) return;
        if ((next == OperationStatus.PREPARING || next == OperationStatus.IN_PROGRESS)
                && room.getStatus() == RoomStatus.AVAILABLE) {
            room.setStatus(RoomStatus.OCCUPIED);
            rooms.update(room);
        }
        if ((next == OperationStatus.COMPLETED || next == OperationStatus.CANCELLED)
                && prev != OperationStatus.SCHEDULED
                && room.getStatus() == RoomStatus.OCCUPIED) {
            room.setStatus(RoomStatus.AVAILABLE);
            rooms.update(room);
        }
    }
}
