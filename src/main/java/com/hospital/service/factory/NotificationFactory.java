package com.hospital.service.factory;

import com.hospital.enums.NotificationType;
import com.hospital.exception.InvalidDataException;
import com.hospital.model.Notification;

public final class NotificationFactory {
    private NotificationFactory() {
    }

    public static Notification create(String id, NotificationType type,
                                      String title, String message, String relatedId) {
        if (type == null) throw new InvalidDataException("Notification type required.");
        if (title == null || title.isBlank()) {
            throw new InvalidDataException("Notification title required.");
        }
        if (message == null || message.isBlank()) {
            throw new InvalidDataException("Notification message required.");
        }
        Notification n = new Notification(id, type, title, message);
        n.setRelatedId(relatedId);
        return n;
    }

    public static Notification emergency(String id, String title, String message,
                                         String relatedId) {
        return create(id, NotificationType.EMERGENCY_ALERT, title, message, relatedId);
    }

    public static Notification general(String id, String title, String message,
                                       String relatedId) {
        return create(id, NotificationType.GENERAL, title, message, relatedId);
    }
}
