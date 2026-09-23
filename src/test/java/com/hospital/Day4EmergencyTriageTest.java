package com.hospital;

import com.hospital.enums.EmergencyCaseStatus;
import com.hospital.enums.EmergencyLevel;
import com.hospital.exception.InvalidDataException;
import com.hospital.model.EmergencyCase;
import com.hospital.service.DoctorService;
import com.hospital.service.EmergencyService;
import com.hospital.service.PatientService;

import junit.framework.TestCase;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class Day4EmergencyTriageTest extends TestCase {
    protected File tempDir;
    protected String patientFile;
    protected String doctorFile;
    protected String caseFile;
    protected PatientService patientService;
    protected DoctorService doctorService;
    protected EmergencyService emergencyService;

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
        register("PAT-0001", "Rahim");
        register("PAT-0002", "Karim");
        register("PAT-0003", "Hasan");
        register("PAT-0004", "Sakib");
        register("PAT-0005", "Nayeem");
        doctorService.registerDoctor(
                new com.hospital.model.Doctor("DOC-0001", "Dr. Karim", "01700000001", "Male", "Cardiology"));
    }

    @Override
    protected void tearDown() throws Exception {
        File[] files = tempDir.listFiles();
        if (files != null) {
            for (File file : files) file.delete();
        }
        tempDir.delete();
    }

    protected String path(String fileName) {
        return new File(tempDir, fileName).getPath();
    }

    protected String readFile(String filePath) throws Exception {
        return Files.readString(Path.of(filePath));
    }

    protected void register(String id, String name) {
        patientService.registerPatient(
                new com.hospital.model.Patient(id, name, "01700000000", "Male", "O+", "01900000000"));
    }

    protected void addFiveCases() {
        emergencyService.addEmergencyCase("PAT-0001", EmergencyLevel.SERIOUS, "High fever");
        emergencyService.addEmergencyCase("PAT-0002", EmergencyLevel.CRITICAL, "Chest pain");
        emergencyService.addEmergencyCase("PAT-0003", EmergencyLevel.LOW, "Small cut");
        emergencyService.addEmergencyCase("PAT-0004", EmergencyLevel.CRITICAL, "Road accident");
        emergencyService.addEmergencyCase("PAT-0005", EmergencyLevel.MODERATE, "Fracture");
    }

    protected static List<String> patientNamesOf(List<EmergencyCase> cases) {
        return cases.stream().map(EmergencyCase::getPatientId).toList();
    }

    public void testQueueOrdersByPriorityThenArrival() {
        addFiveCases();
        List<EmergencyCase> queue = emergencyService.viewEmergencyQueue();
        assertEquals(5, queue.size());
        assertEquals(List.of("PAT-0002", "PAT-0004", "PAT-0001", "PAT-0005", "PAT-0003"),
                patientNamesOf(queue));
        assertEquals(EmergencyLevel.CRITICAL, queue.get(0).getPriority());
        assertEquals(EmergencyLevel.LOW, queue.get(4).getPriority());
    }

    public void testGetNextPatientReturnsKarimWithoutRemoving() {
        addFiveCases();
        EmergencyCase next = emergencyService.getNextPatient();
        assertEquals("PAT-0002", next.getPatientId());
        assertEquals(EmergencyLevel.CRITICAL, next.getPriority());
        assertEquals(EmergencyCaseStatus.WAITING, next.getStatus());
        assertEquals(5, emergencyService.viewEmergencyQueue().size());
    }

    public void testEmptyQueueThrowsMeaningfulError() {
        try {
            emergencyService.getNextPatient();
            fail("Expected InvalidDataException for an empty queue");
        } catch (InvalidDataException expected) {
            assertTrue(expected.getMessage().contains("empty"));
        }
    }
}
