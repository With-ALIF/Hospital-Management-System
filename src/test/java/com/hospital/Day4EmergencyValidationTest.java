package com.hospital;

import com.hospital.enums.EmergencyCaseStatus;
import com.hospital.enums.EmergencyLevel;
import com.hospital.exception.InvalidDataException;
import com.hospital.model.EmergencyCase;
import com.hospital.service.EmergencyService;

import java.nio.file.Files;
import java.nio.file.Path;

public class Day4EmergencyValidationTest extends Day4EmergencyTriageTest {

    public void testInvalidTransitionsAndFinishedCasesAreRejected() throws Exception {
        emergencyService.addEmergencyCase("PAT-0002", EmergencyLevel.CRITICAL, "Chest pain");
        String caseId = "EMG-0001";
        try {
            emergencyService.updateStatus(caseId, EmergencyCaseStatus.COMPLETED);
            fail("Expected InvalidDataException for WAITING -> COMPLETED");
        } catch (InvalidDataException expected) {
            assertTrue(expected.getMessage().contains("Invalid status transition"));
        }
        EmergencyCase started = emergencyService.startTreatment(caseId);
        try {
            emergencyService.cancelEmergencyCase(caseId);
            fail("Expected InvalidDataException for IN_TREATMENT -> CANCELLED");
        } catch (InvalidDataException expected) {
            assertTrue(expected.getMessage().contains("Invalid status transition"));
        }
        try {
            emergencyService.startTreatment(caseId);
            fail("Expected InvalidDataException when treatment already started");
        } catch (InvalidDataException expected) {
            assertTrue(expected.getMessage().contains("already in treatment"));
        }
        emergencyService.completeTreatment(started.getId());
        expectFailure(() -> emergencyService.completeTreatment(caseId), "already completed");
        expectFailure(() -> emergencyService.startTreatment(caseId), "already completed");
        emergencyService.addEmergencyCase("PAT-0003", EmergencyLevel.LOW, "Small cut");
        emergencyService.cancelEmergencyCase("EMG-0002");
        expectFailure(() -> emergencyService.startTreatment("EMG-0002"), "already cancelled");
    }

    public void testUnknownPatientAndUnknownCaseAreRejected() {
        try {
            emergencyService.addEmergencyCase("PAT-9999", EmergencyLevel.SERIOUS, "Ghost");
            fail("Expected InvalidDataException for an unknown patient");
        } catch (InvalidDataException expected) {
            assertTrue(expected.getMessage().contains("patient not found"));
        }
        expectFailure(() -> emergencyService.startTreatment("EMG-4040"), "Emergency case not found");
        expectFailure(() -> emergencyService.completeTreatment("EMG-4040"), "Emergency case not found");
        expectFailure(() -> emergencyService.cancelEmergencyCase("EMG-4040"), "Emergency case not found");
    }

    public void testLegacyJsonWithoutNewFieldsStillLoads() throws Exception {
        Files.writeString(Path.of(caseFile),
                "[ { \"id\" : \"EMG-0001\", \"patientId\" : \"PAT-0001\","
                        + " \"priority\" : \"HIGH\", \"description\" : \"Chest pain\","
                        + " \"status\" : \"WAITING\" } ]");
        EmergencyService service = new EmergencyService(patientService, doctorService, caseFile);
        assertEquals(1, service.getCaseCount());
        assertEquals(1, service.viewEmergencyQueue().size());
        assertNull(service.findCaseById("EMG-0001").get().getArrivalTime());
        assertNull(service.findCaseById("EMG-0001").get().getAssignedDoctorId());
    }

    private void expectFailure(Runnable action, String fragment) {
        try {
            action.run();
            fail("Expected InvalidDataException containing: " + fragment);
        } catch (InvalidDataException expected) {
            assertTrue(expected.getMessage().contains(fragment));
        }
    }
}
