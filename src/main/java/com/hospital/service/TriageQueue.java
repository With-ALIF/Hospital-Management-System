package com.hospital.service;

import com.hospital.enums.EmergencyCaseStatus;
import com.hospital.enums.EmergencyLevel;
import com.hospital.exception.InvalidDataException;
import com.hospital.model.EmergencyCase;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.PriorityQueue;

public class TriageQueue {

    private static final Comparator<EmergencyCase> TRIAGE_ORDER =
            Comparator.comparingInt(TriageQueue::scoreOf).reversed()
                    .thenComparing(EmergencyCase::getPriority)
                    .thenComparing(EmergencyCase::getArrivalTime,
                            Comparator.nullsFirst(Comparator.naturalOrder()))
                    .thenComparing(EmergencyCase::getId,
                            Comparator.nullsLast(Comparator.naturalOrder()));

    private final PriorityQueue<EmergencyCase> waitingQueue = new PriorityQueue<>(TRIAGE_ORDER);

    public void loadWaiting(List<EmergencyCase> allCases) {
        waitingQueue.clear();
        for (EmergencyCase emergencyCase : allCases) {
            if (emergencyCase.getStatus() == EmergencyCaseStatus.WAITING) {
                waitingQueue.offer(emergencyCase);
            }
        }
    }

    public void offer(EmergencyCase emergencyCase) {
        if (emergencyCase != null) {
            waitingQueue.offer(emergencyCase);
        }
    }

    public void remove(EmergencyCase emergencyCase) {
        waitingQueue.remove(emergencyCase);
    }

    public EmergencyCase peek() {
        EmergencyCase next = waitingQueue.peek();
        if (next == null) {
            throw new InvalidDataException("Emergency queue is empty. No patient is waiting.");
        }
        return next;
    }

    public List<EmergencyCase> orderedView() {
        List<EmergencyCase> ordered = new ArrayList<>(waitingQueue);
        ordered.sort(TRIAGE_ORDER);
        return List.copyOf(ordered);
    }

    public EmergencyCase createCase(String id, String patientId, EmergencyLevel level,
                                    String description) {
        EmergencyCase emergencyCase = new EmergencyCase(id, patientId, level,
                description, EmergencyCaseStatus.WAITING, LocalDateTime.now(), null);
        emergencyCase.setPriorityScore(scoreFor(level));
        return emergencyCase;
    }

    public static int scoreOf(EmergencyCase emergencyCase) {
        if (emergencyCase.getPriorityScore() > 0) {
            return emergencyCase.getPriorityScore();
        }
        return scoreFor(emergencyCase.getPriority());
    }

    public static int scoreFor(EmergencyLevel level) {
        return level != null ? level.getSeverity() * 25 : 0;
    }

    public static Optional<EmergencyLevel> parseLevel(String raw) {
        if (raw == null || raw.isBlank()) {
            return Optional.empty();
        }
        try {
            return Optional.of(EmergencyLevel.from(raw));
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }
}
