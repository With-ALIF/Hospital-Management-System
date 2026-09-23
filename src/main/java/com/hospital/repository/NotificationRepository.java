package com.hospital.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.hospital.model.Notification;

import java.util.List;

public class NotificationRepository extends AbstractJsonRepository<Notification> {
    public static final String DEFAULT_FILE = "data/notifications.json";

    public NotificationRepository() { this(DEFAULT_FILE); }
    public NotificationRepository(String filePath) {
        super(filePath, new TypeReference<List<Notification>>() {}, "Notification");
    }

    @Override
    protected String idOf(Notification entity) { return entity.getId(); }

    public String nextNotificationId() { return nextId("NTF-", 1); }
}
