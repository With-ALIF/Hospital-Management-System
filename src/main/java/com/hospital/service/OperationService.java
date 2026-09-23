package com.hospital.service;

import com.hospital.enums.OperationPriority;
import com.hospital.enums.OperationStatus;
import com.hospital.exception.InvalidDataException;
import com.hospital.model.Operation;
import com.hospital.model.OperationRoom;
import com.hospital.repository.OperationRepository;
import com.hospital.repository.OperationRoomRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public class OperationService {
    private final OperationRepository operations;
    private final OperationRoomRepository rooms;
    private final OperationConflicts conflicts;
    private final OperationQueries queries;
    private final OperationEvents events;

    public OperationService() {
        this(OperationRepository.DEFAULT_FILE, OperationRoomRepository.DEFAULT_FILE);
    }

    public OperationService(String opFile, String roomFile) {
        this.operations = new OperationRepository(opFile);
        this.rooms = new OperationRoomRepository(roomFile);
        this.conflicts = new OperationConflicts(operations, rooms);
        this.queries = new OperationQueries(operations);
        this.events = new OperationEvents();
    }

    public OperationRoom addRoom(String name, String type) {
        if (name == null || name.isBlank()) {
            throw new InvalidDataException("Room name required.");
        }
        OperationRoom room = new OperationRoom(rooms.nextId(), name, type);
        rooms.add(room);
        return room;
    }

    public OperationRoom updateRoom(OperationRoom room) {
        if (room == null || room.getId() == null) {
            throw new InvalidDataException("Room required.");
        }
        rooms.update(room);
        return room;
    }

    public void removeRoom(String roomId) {
        conflicts.removeRoomIfFree(roomId);
    }

    public Operation schedule(String patientId, String surgeonId, String assistantId,
                              String roomId, LocalDate date, LocalTime start, LocalTime end,
                              String type, OperationPriority priority) {
        conflicts.validateSchedule(patientId, surgeonId, roomId, date, start, end);
        Operation op = new Operation(operations.nextId(), patientId, surgeonId, roomId,
                date, start, end, type, priority);
        op.setAssistantDoctorId(assistantId);
        operations.add(op);
        events.notifyScheduled(op, priority, type, date, roomId);
        return op;
    }

    public Operation updateStatus(String operationId, OperationStatus next) {
        Operation op = queries.require(operationId);
        conflicts.requireTransition(op, next);
        OperationStatus prev = op.getStatus();
        op.setStatus(next);
        operations.update(op);
        conflicts.updateRoomOccupancy(op, prev, next);
        events.publishStatusChange(op, next);
        return op;
    }

    public Operation start(String operationId) { return updateStatus(operationId, OperationStatus.IN_PROGRESS); }
    public Operation complete(String operationId) { return updateStatus(operationId, OperationStatus.COMPLETED); }
    public Operation cancel(String operationId) { return updateStatus(operationId, OperationStatus.CANCELLED); }

    public void ensureNoConflict(String roomId, String surgeonId, String patientId,
                                 LocalDate date, LocalTime start, LocalTime end) {
        conflicts.ensureNoConflict(roomId, surgeonId, patientId, date, start, end);
    }

    public boolean isRoomFree(String roomId, LocalDate date, LocalTime start, LocalTime end) {
        return conflicts.isRoomFree(roomId, date, start, end);
    }

    public List<Operation> search(String query) { return queries.search(query); }

    public List<Operation> getAllOperations() { return operations.findAll(); }
    public List<Operation> getToday() { return operations.findByDate(LocalDate.now()); }
    public List<OperationRoom> getAllRooms() { return rooms.findAll(); }
    public List<OperationRoom> getAvailableRooms() { return rooms.findAvailable(); }
    public Optional<Operation> find(String id) { return operations.findById(id); }
    public Optional<OperationRoom> findRoom(String id) { return rooms.findById(id); }
    public int getOperationCount() { return operations.count(); }
    public int getRoomCount() { return rooms.count(); }
    public String nextId() { return operations.nextId(); }
    public String nextRoomId() { return rooms.nextId(); }

    public void updateOperation(Operation op) {
        if (op == null) throw new InvalidDataException("Operation required.");
        operations.update(op);
    }
}
