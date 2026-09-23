package com.hospital.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.hospital.enums.RoomStatus;
import com.hospital.model.OperationRoom;

import java.util.List;

public class OperationRoomRepository extends AbstractJsonRepository<OperationRoom> {
    public static final String DEFAULT_FILE = "data/operation_rooms.json";

    public OperationRoomRepository() { this(DEFAULT_FILE); }
    public OperationRoomRepository(String filePath) {
        super(filePath, new TypeReference<List<OperationRoom>>() {}, "OperationRoom");
    }

    @Override
    protected String idOf(OperationRoom e) { return e.getId(); }

    public String nextId() { return nextId("ORM-", 1); }

    public List<OperationRoom> findAvailable() {
        return findAll().stream().filter(r -> r.getStatus() == RoomStatus.AVAILABLE).toList();
    }
}
