package com.hospital;

import com.hospital.enums.EmergencyCaseStatus;
import com.hospital.enums.EmergencyLevel;
import com.hospital.exception.InvalidDataException;
import com.hospital.model.EmergencyCase;
import com.hospital.service.DoctorService;
import com.hospital.service.EmergencyService;
import com.hospital.service.PatientService;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class Day4EmergencyFlowTest extends Day4EmergencyTriageTest {

    public void testTreatmentFlowAssignsAndFreesDoctor() throws Exception {
        addFiveCases();
        EmergencyCase started = emergencyService.startTreatment();
        assertEquals("PAT-0002", started.getPatientId());
        assertEquals(EmergencyCaseStatus.IN_TREATMENT, started.getStatus());
        assertEquals("DOC-0001", started.getAssignedDoctorId());
        assertFalse(doctorService.findDoctorById("DOC-0001").get().getAvailable());
        assertEquals(4, emergencyService.viewEmergencyQueue().size());
        assertEquals("PAT-0004", emergencyService.getNextPatient().getPatientId());
        String json = readFile(caseFile);
        assertTrue(json.contains("IN_TREATMENT"));
        assertTrue(json.contains("DOC-0001"));
        EmergencyCase finished = emergencyService.completeTreatment(started.getId());
        assertEquals(EmergencyCaseStatus.COMPLETED, finished.getStatus());
        assertTrue(doctorService.findDoctorById("DOC-0001").get().getAvailable());
        assertTrue(readFile(caseFile).contains("COMPLETED"));
        assertTrue(new DoctorService(doctorFile).findDoctorById("DOC-0001").get().getAvailable());
    }

    public void testNoAvailableDoctorKeepsCaseWaiting() {
        emergencyService.addEmergencyCase("PAT-0002", EmergencyLevel.CRITICAL, "Chest pain");
        doctorService.setAvailability("DOC-0001", false);
        try {
            emergencyService.startTreatment();
            fail("Expected InvalidDataException when no doctor is available");
        } catch (InvalidDataException expected) {
            assertTrue(expected.getMessage().contains("No available doctor"));
        }
        EmergencyCase kept = emergencyService.findCaseById("EMG-0001").orElse(null);
        assertNotNull(kept);
        assertEquals(EmergencyCaseStatus.WAITING, kept.getStatus());
        assertNull(kept.getAssignedDoctorId());
        assertEquals(1, emergencyService.viewEmergencyQueue().size());
    }

    public void testCancelWaitingCaseRemovesItFromQueue() {
        addFiveCases();
        String lowCaseId = emergencyService.getCasesForPatient("PAT-0003").get(0).getId();
        EmergencyCase cancelled = emergencyService.cancelEmergencyCase(lowCaseId);
        assertEquals(EmergencyCaseStatus.CANCELLED, cancelled.getStatus());
        assertEquals(4, emergencyService.viewEmergencyQueue().size());
        assertFalse(patientNamesOf(emergencyService.viewEmergencyQueue()).contains("PAT-0003"));
    }

    public void testQueueAndTreatmentSurviveRestart() throws Exception {
        addFiveCases();
        emergencyService.startTreatment();
        PatientService reloadedPatients = new PatientService(patientFile);
        DoctorService reloadedDoctors = new DoctorService(doctorFile);
        EmergencyService restarted = new EmergencyService(reloadedPatients, reloadedDoctors, caseFile);
        assertEquals(5, restarted.getCaseCount());
        assertEquals(List.of("PAT-0004", "PAT-0001", "PAT-0005", "PAT-0003"),
                patientNamesOf(restarted.viewEmergencyQueue()));
        assertEquals(EmergencyCaseStatus.IN_TREATMENT,
                restarted.findCaseById("EMG-0002").get().getStatus());
        assertFalse(reloadedDoctors.findDoctorById("DOC-0001").get().getAvailable());
        restarted.completeTreatment("EMG-0002");
        assertEquals(EmergencyCaseStatus.COMPLETED,
                restarted.findCaseById("EMG-0002").get().getStatus());
        assertTrue(new DoctorService(doctorFile).findDoctorById("DOC-0001").get().getAvailable());
        String json = readFile(caseFile);
        assertTrue(json.contains("COMPLETED"));
        assertTrue(json.contains("arrivalTime"));
        assertTrue(json.contains("assignedDoctorId"));
    }
}
