package com.hospital.service;

import com.hospital.enums.HospitalEventType;
import com.hospital.exception.InvalidDataException;
import com.hospital.model.HospitalEvent;
import com.hospital.repository.HospitalEventRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public final class EventBus {
    private static final EventBus INSTANCE = new EventBus();
    private final HospitalEventRepository events;
    private final NotificationService notifications;
    private final AuditLogService auditLogs;
    private final Map<HospitalEventType, List<Consumer<HospitalEvent>>> listeners =
            new ConcurrentHashMap<>();

    private EventBus() {
        this.events = new HospitalEventRepository();
        this.notifications = new NotificationService();
        this.auditLogs = new AuditLogService();
    }

    public static EventBus getInstance() { return INSTANCE; }

    public void subscribe(HospitalEventType type, Consumer<HospitalEvent> listener) {
        if (type == null || listener == null) {
            throw new InvalidDataException("Event type and listener required.");
        }
        listeners.computeIfAbsent(type, k -> new ArrayList<>()).add(listener);
    }

    public HospitalEvent publish(HospitalEventType type, String entityId,
                                 String entityLabel, String message) {
        if (type == null) {
            throw new InvalidDataException("Event type required.");
        }
        String actor = "SYSTEM";
        HospitalEvent event = new HospitalEvent(events.nextId(), type, entityId,
                entityLabel, message, actor);
        events.add(event);
        List<Consumer<HospitalEvent>> subs = listeners.getOrDefault(type, List.of());
        for (Consumer<HospitalEvent> sub : subs) {
            try {
                sub.accept(event);
            } catch (Exception ignored) {
            }
        }
        return event;
    }

    public void notifyAndAudit(HospitalEventType type, String entityId, String entityLabel,
                               String message, com.hospital.enums.NotificationType nType,
                               String title, com.hospital.enums.AuditAction action) {
        publish(type, entityId, entityLabel, message);
        if (nType != null && title != null) {
            notifications.notify(nType, title, message, entityId);
        }
        if (action != null) {
            auditLogs.record("SYSTEM", action, entityLabel, entityId, message);
        }
    }

    public List<HospitalEvent> getAll() { return events.findAll(); }
    public List<HospitalEvent> getByType(HospitalEventType type) {
        return events.findByType(type);
    }
    public int getCount() { return events.count(); }
    public String nextId() { return events.nextId(); }
}
