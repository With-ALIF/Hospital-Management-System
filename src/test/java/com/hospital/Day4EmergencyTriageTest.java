package com.hospital;

import com.hospital.exception.InvalidDataException;
import com.hospital.model.Doctor;
import com.hospital.model.EmergencyCase;
import com.hospital.model.EmergencyCaseStatus;
import com.hospital.model.EmergencyPriority;
import com.hospital.model.Patient;
import com.hospital.service.DoctorService;
import com.hospital.service.EmergencyService;
import com.hospital.service.PatientService;

import junit.framework.TestCase;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Day 4 tests: emergency triage, priority queue and doctor assignment.
 *
 * Every test works in a temporary directory, so the real files inside
 * {@code data/} are never modified by a test run.
 */
public class Day4EmergencyTriageTest extends TestCase {

    private File tempDir;
    private String patientFile;
    private String doctorFile;
    private String caseFile;
    private PatientService patientService;
    private DoctorService doctorService;
    private EmergencyService emergencyService;

    @Override
    protected void setUp() throws Exception {
        tempDir = File.createTempFile("hospital-day4", "");
        tempDir.delete();
        tempDir.mkdirs();
        patientFile = path("patients.json");
        doctorFile = path("doctors.json");
        caseFile = path("emergency_cases.json");

        patientService = new PatientService(patientFile);
        doctorService = new DoctorService(doctorFile);
        emergencyService = new EmergencyService(patientService, doctorService, caseFile);

        // Test data: 5 patients
        register("P-1001", "Rahim");
        register("P-1002", "Karim");
        register("P-1003", "Hasan");
        register("P-1004", "Sakib");
        register("P-1005", "Nayeem");

        // One available doctor
        doctorService.registerDoctor(
                new Doctor("D-1001", "Dr. Karim", "01700000001", "Male", "Cardiology"));
    }

    @Override
    protected void tearDown() throws Exception {
        File[] files = tempDir.listFiles();
        if (files != null) {
            for (File file : files) {
                file.delete();
            }
        }
        tempDir.delete();
    }

    private String path(String fileName) {
        return new File(tempDir, fileName).getPath();
    }

    private String readFile(String filePath) throws Exception {
        return Files.readString(Path.of(filePath));
    }

    private void register(String id, String name) {
        patientService.registerPatient(
                new Patient(id, name, "01700000000", "Male", "O+", "01900000000"));
    }

    private void addFiveCases() {
        emergencyService.addEmergencyCase("P-1001", EmergencyPriority.HIGH, "High fever");
        emergencyService.addEmergencyCase("P-1002", EmergencyPriority.CRITICAL, "Chest pain");
        emergencyService.addEmergencyCase("P-1003", EmergencyPriority.LOW, "Small cut");
        emergencyService.addEmergencyCase("P-1004", EmergencyPriority.CRITICAL, "Road accident");
        emergencyService.addEmergencyCase("P-1005", EmergencyPriority.MEDIUM, "Fracture");
    }

    private static List<String> patientNamesOf(List<EmergencyCase> cases) {
        return cases.stream().map(EmergencyCase::getPatientId).toList();
    }

    // ----------------------------------------------------- priority queue order

    public void testQueueOrdersByPriorityThenArrival() {
        addFiveCases();

        List<EmergencyCase> queue = emergencyService.viewEmergencyQueue();
        assertEquals(5, queue.size());
        assertEquals("CRITICAL cases first, arrival order inside the same priority",
                List.of("P-1002", "P-1004", "P-1001", "P-1005", "P-1003"),
                patientNamesOf(queue));
        assertEquals(EmergencyPriority.CRITICAL, queue.get(0).getPriority());
        assertEquals(EmergencyPriority.LOW, queue.get(4).getPriority());
    }

    public void testGetNextPatientReturnsKarimWithoutRemoving() {
        addFiveCases();

        EmergencyCase next = emergencyService.getNextPatient();
        assertEquals("P-1002", next.getPatientId());
        assertEquals(EmergencyPriority.CRITICAL, next.getPriority());
        assertEquals(EmergencyCaseStatus.WAITING, next.getStatus());
        assertEquals("peek must not remove the case from the queue",
                5, emergencyService.viewEmergencyQueue().size());
    }

    public void testEmptyQueueThrowsMeaningfulError() {
        try {
            emergencyService.getNextPatient();
            fail("Expected InvalidDataException for an empty queue");
        } catch (InvalidDataException expected) {
            assertTrue(expected.getMessage().contains("empty"));
        }
    }

    // ------------------------------------------------ treatment + doctor assign

    public void testTreatmentFlowAssignsAndFreesDoctor() throws Exception {
        addFiveCases();

        EmergencyCase started = emergencyService.startTreatment();
        assertEquals("Karim must be treated first", "P-1002", started.getPatientId());
        assertEquals(EmergencyCaseStatus.IN_TREATMENT, started.getStatus());
        assertEquals("D-1001", started.getAssignedDoctorId());
        assertFalse("the assigned doctor must be busy",
                doctorService.findDoctorById("D-1001").get().getAvailable());
        assertEquals("the treated case leaves the waiting queue",
                4, emergencyService.viewEmergencyQueue().size());
        assertEquals("the next CRITICAL patient is Sakib",
                "P-1004", emergencyService.getNextPatient().getPatientId());

        String json = readFile(caseFile);
        assertTrue("IN_TREATMENT must be saved automatically", json.contains("IN_TREATMENT"));
        assertTrue("assignedDoctorId must be saved automatically", json.contains("D-1001"));

        EmergencyCase finished = emergencyService.completeTreatment(started.getId());
        assertEquals(EmergencyCaseStatus.COMPLETED, finished.getStatus());
        assertTrue("the doctor must be available again",
                doctorService.findDoctorById("D-1001").get().getAvailable());
        assertTrue("COMPLETED must be saved automatically",
                readFile(caseFile).contains("COMPLETED"));
        assertTrue("the freed doctor must be saved automatically",
                new DoctorService(doctorFile).findDoctorById("D-1001").get().getAvailable());
    }

    public void testNoAvailableDoctorKeepsCaseWaiting() {
        emergencyService.addEmergencyCase("P-1002", EmergencyPriority.CRITICAL, "Chest pain");
        doctorService.setAvailability("D-1001", false);

        try {
            emergencyService.startTreatment();
            fail("Expected InvalidDataException when no doctor is available");
        } catch (InvalidDataException expected) {
            assertTrue(expected.getMessage().contains("No available doctor"));
        }

        EmergencyCase kept = emergencyService.findCaseById("E-1001").orElse(null);
        assertNotNull(kept);
        assertEquals("the case must stay WAITING", EmergencyCaseStatus.WAITING, kept.getStatus());
        assertNull("no doctor may be assigned", kept.getAssignedDoctorId());
        assertEquals("the case must stay in the queue", 1, emergencyService.viewEmergencyQueue().size());
    }

    public void testCancelWaitingCaseRemovesItFromQueue() {
        addFiveCases();
        String lowCaseId = emergencyService.getCasesForPatient("P-1003").get(0).getId();

        EmergencyCase cancelled = emergencyService.cancelEmergencyCase(lowCaseId);
        assertEquals(EmergencyCaseStatus.CANCELLED, cancelled.getStatus());
        assertEquals(4, emergencyService.viewEmergencyQueue().size());
        assertFalse("cancelled case must leave the queue",
                patientNamesOf(emergencyService.viewEmergencyQueue()).contains("P-1003"));
    }

    // ------------------------------------------------------ status transitions

    public void testInvalidTransitionsAndFinishedCasesAreRejected() throws Exception {
        emergencyService.addEmergencyCase("P-1002", EmergencyPriority.CRITICAL, "Chest pain");
        String caseId = "E-1001";

        // WAITING -> COMPLETED is not allowed
        try {
            emergencyService.updateStatus(caseId, EmergencyCaseStatus.COMPLETED);
            fail("Expected InvalidDataException for WAITING -> COMPLETED");
        } catch (InvalidDataException expected) {
            assertTrue(expected.getMessage().contains("Invalid status transition"));
        }

        EmergencyCase started = emergencyService.startTreatment(caseId);

        // IN_TREATMENT -> CANCELLED is not allowed
        try {
            emergencyService.cancelEmergencyCase(caseId);
            fail("Expected InvalidDataException for IN_TREATMENT -> CANCELLED");
        } catch (InvalidDataException expected) {
            assertTrue(expected.getMessage().contains("Invalid status transition"));
        }

        // starting twice is not allowed
        try {
            emergencyService.startTreatment(caseId);
            fail("Expected InvalidDataException when treatment already started");
        } catch (InvalidDataException expected) {
            assertTrue(expected.getMessage().contains("already in treatment"));
        }

        emergencyService.completeTreatment(started.getId());

        // COMPLETED is terminal
        try {
            emergencyService.completeTreatment(caseId);
            fail("Expected InvalidDataException for an already completed case");
        } catch (InvalidDataException expected) {
            assertTrue(expected.getMessage().contains("already completed"));
        }
        try {
            emergencyService.startTreatment(caseId);
            fail("Expected InvalidDataException for an already completed case");
        } catch (InvalidDataException expected) {
            assertTrue(expected.getMessage().contains("already completed"));
        }

        // cancelled cases are terminal too
        emergencyService.addEmergencyCase("P-1003", EmergencyPriority.LOW, "Small cut");
        emergencyService.cancelEmergencyCase("E-1002");
        try {
            emergencyService.startTreatment("E-1002");
            fail("Expected InvalidDataException for an already cancelled case");
        } catch (InvalidDataException expected) {
            assertTrue(expected.getMessage().contains("already cancelled"));
        }
    }

    public void testUnknownPatientAndUnknownCaseAreRejected() {
        try {
            emergencyService.addEmergencyCase("P-9999", EmergencyPriority.HIGH, "Ghost");
            fail("Expected InvalidDataException for an unknown patient");
        } catch (InvalidDataException expected) {
            assertTrue(expected.getMessage().contains("patient not found"));
        }

        try {
            emergencyService.startTreatment("E-4040");
            fail("Expected InvalidDataException for an unknown case");
        } catch (InvalidDataException expected) {
            assertTrue(expected.getMessage().contains("Emergency case not found"));
        }

        try {
            emergencyService.completeTreatment("E-4040");
            fail("Expected InvalidDataException for an unknown case");
        } catch (InvalidDataException expected) {
            assertTrue(expected.getMessage().contains("Emergency case not found"));
        }

        try {
            emergencyService.cancelEmergencyCase("E-4040");
            fail("Expected InvalidDataException for an unknown case");
        } catch (InvalidDataException expected) {
            assertTrue(expected.getMessage().contains("Emergency case not found"));
        }
    }

    // ------------------------------------------------------------- JSON sync

    public void testQueueAndTreatmentSurviveRestart() throws Exception {
        addFiveCases();
        emergencyService.startTreatment();                 // Karim: WAITING -> IN_TREATMENT (doctor busy)

        // "restart" while Karim is still being treated
        PatientService reloadedPatients = new PatientService(patientFile);
        DoctorService reloadedDoctors = new DoctorService(doctorFile);
        EmergencyService restarted = new EmergencyService(reloadedPatients, reloadedDoctors, caseFile);

        assertEquals(5, restarted.getCaseCount());
        assertEquals("only 4 cases are still waiting",
                List.of("P-1004", "P-1001", "P-1005", "P-1003"),
                patientNamesOf(restarted.viewEmergencyQueue()));
        assertEquals(EmergencyCaseStatus.IN_TREATMENT,
                restarted.findCaseById("E-1002").get().getStatus());
        assertFalse("the busy doctor must stay busy after a restart",
                reloadedDoctors.findDoctorById("D-1001").get().getAvailable());

        // finish the treatment through the restarted services
        restarted.completeTreatment("E-1002");
        assertEquals(EmergencyCaseStatus.COMPLETED,
                restarted.findCaseById("E-1002").get().getStatus());
        assertTrue("the doctor must be available again after completion",
                new DoctorService(doctorFile).findDoctorById("D-1001").get().getAvailable());

        String json = readFile(caseFile);
        assertTrue(json.contains("COMPLETED"));
        assertTrue("arrivalTime is part of the Day 4 schema", json.contains("arrivalTime"));
        assertTrue("assignedDoctorId is part of the Day 4 schema", json.contains("assignedDoctorId"));
    }

    public void testLegacyJsonWithoutNewFieldsStillLoads() throws Exception {
        Files.writeString(Path.of(caseFile),
                "[ { \"id\" : \"E-1001\", \"patientId\" : \"P-1001\","
                        + " \"priority\" : \"HIGH\", \"description\" : \"Chest pain\","
                        + " \"status\" : \"WAITING\" } ]");

        EmergencyService service = new EmergencyService(patientService, doctorService, caseFile);
        assertEquals(1, service.getCaseCount());
        assertEquals("the old case must be waiting in the queue",
                1, service.viewEmergencyQueue().size());
        assertNull(service.findCaseById("E-1001").get().getArrivalTime());
        assertNull(service.findCaseById("E-1001").get().getAssignedDoctorId());
    }
}
