package com.hospital.service;

import com.hospital.enums.HospitalEventType;
import com.hospital.enums.OperationPriority;
import com.hospital.enums.OperationStatus;
import com.hospital.model.Operation;

import java.time.LocalDate;

class OperationEvents {
    private final NotificationService notifications;
    private final AuditLogService auditLogs;
    private final EventBus eventBus;

    OperationEvents() {
        this.notifications = new NotificationService();
        this.auditLogs = new AuditLogService();
        this.eventBus = EventBus.getInstance();
    }

    void notifyScheduled(Operation op, OperationPriority priority, String type,
                         LocalDate date, String roomId) {
        if (priority == OperationPriority.EMERGENCY) {
            notifications.notify(com.hospital.enums.NotificationType.EMERGENCY_ALERT,
                    "Emergency operation scheduled", type + " on " + date, op.getId());
        }
        auditLogs.record("SYSTEM", com.hospital.enums.AuditAction.OPERATION_SCHEDULE,
                "Operation", op.getId(),
                "Scheduled " + type + " in " + roomId);
    }

    void publishStatusChange(Operation op, OperationStatus next) {
        if (next == OperationStatus.IN_PROGRESS) {
            eventBus.publish(HospitalEventType.OPERATION_STARTED, op.getId(),
                    "Operation", "Operation started: " + op.getOperationType());
            notifications.notify(com.hospital.enums.NotificationType.GENERAL,
                    "Operation started", op.getOperationType(), op.getId());
        }
        if (next == OperationStatus.COMPLETED) {
            eventBus.publish(HospitalEventType.OPERATION_COMPLETED, op.getId(),
                    "Operation", "Operation completed: " + op.getOperationType());
            auditLogs.record("SYSTEM", com.hospital.enums.AuditAction.OPERATION_COMPLETE,
                    "Operation", op.getId(),
                    "Operation completed");
        }
        if (next == OperationStatus.CANCELLED) {
            auditLogs.record("SYSTEM", com.hospital.enums.AuditAction.OPERATION_CANCEL,
                    "Operation", op.getId(),
                    "Operation cancelled");
        }
    }
}
