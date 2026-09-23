package com.hospital.service;

import com.hospital.enums.NotificationType;
import com.hospital.exception.InvalidDataException;
import com.hospital.model.Notification;
import com.hospital.repository.NotificationRepository;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class NotificationService {
    private final NotificationRepository repository;

    public NotificationService() {
        this(NotificationRepository.DEFAULT_FILE);
    }

    public NotificationService(String filePath) {
        this.repository = new NotificationRepository(filePath);
    }

    public Notification notify(NotificationType type, String title, String message, String relatedId) {
        if (title == null || title.isBlank()) {
            throw new InvalidDataException("Notification title cannot be empty.");
        }
        if (message == null || message.isBlank()) {
            throw new InvalidDataException("Notification message cannot be empty.");
        }
        Notification n = new Notification(repository.nextNotificationId(), type, title, message);
        n.setRelatedId(relatedId);
        repository.add(n);
        return n;
    }

    public Notification markRead(String notificationId) {
        Notification n = require(notificationId);
        n.setRead(true);
        repository.update(n);
        return n;
    }

    public void remove(String notificationId) {
        repository.delete(require(notificationId).getId());
    }

    public List<Notification> getAll() {
        return repository.findAll().stream()
                .sorted(Comparator.comparing(Notification::getCreatedAt,
                        Comparator.nullsLast(Comparator.reverseOrder())))
                .toList();
    }

    public List<Notification> getUnread() {
        return getAll().stream().filter(n -> !n.isRead()).toList();
    }

    public Optional<Notification> find(String id) { return repository.findById(id); }
    public int getUnreadCount() { return getUnread().size(); }
    public int getCount() { return repository.count(); }
    public String nextId() { return repository.nextNotificationId(); }

    private Notification require(String id) {
        if (id == null || id.isBlank()) {
            throw new InvalidDataException("Notification id cannot be empty.");
        }
        return repository.findById(id)
                .orElseThrow(() -> new InvalidDataException("Notification not found: " + id));
    }
}
