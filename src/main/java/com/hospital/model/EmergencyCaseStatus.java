package com.hospital.model;

/** Life cycle of an emergency case (the case itself, not the patient). */
public enum EmergencyCaseStatus {
    WAITING,
    IN_TREATMENT,
    TREATED,
    DISCHARGED
}