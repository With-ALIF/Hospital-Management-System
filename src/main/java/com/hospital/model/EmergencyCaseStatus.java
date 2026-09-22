package com.hospital.model;

/**
 * Life cycle of an emergency case (the case itself, not the patient).
 *
 * Allowed transitions:
 * <pre>
 * WAITING ──► IN_TREATMENT ──► COMPLETED
 *    │
 *    └──────► CANCELLED
 * </pre>
 * COMPLETED and CANCELLED are terminal states.
 */
public enum EmergencyCaseStatus {
    WAITING,
    IN_TREATMENT,
    COMPLETED,
    CANCELLED;

    /** Whether this status is allowed to move to {@code next}. */
    public boolean canTransitionTo(EmergencyCaseStatus next) {
        if (next == null || next == this) {
            return false;
        }
        return switch (this) {
            case WAITING -> next == IN_TREATMENT || next == CANCELLED;
            case IN_TREATMENT -> next == COMPLETED;
            case COMPLETED, CANCELLED -> false;
        };
    }
}
