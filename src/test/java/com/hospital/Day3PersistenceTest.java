package com.hospital;

import junit.framework.TestCase;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

public class Day3PersistenceTest extends TestCase {
    private File tempDir;

    @Override
    protected void setUp() throws Exception {
        tempDir = File.createTempFile("hospital-day3", "");
        tempDir.delete();
        tempDir.mkdirs();
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

    private void writeFile(String filePath, String content) throws Exception {
        Files.writeString(Path.of(filePath), content);
    }

    private static com.hospital.model.Patient rahim() {
        return new com.hospital.model.Patient("PAT-0001", "Rahim", "01700000000", "Male", "O+", "01900000000");
    }

    public void testPatientIsSavedAutomaticallyAndLoadedAgain() throws Exception {
        String file = path("patients.json");
        com.hospital.service.PatientService service = new com.hospital.service.PatientService(file);
        service.registerPatient(rahim());
        String json = readFile(file);
        assertTrue(json.contains("Rahim"));
        assertTrue(json.contains("PAT-0001"));
        com.hospital.service.PatientService after = new com.hospital.service.PatientService(file);
        assertEquals(1, after.getPatientCount());
        assertNotNull(after.findPatientById("PAT-0001").orElse(null));
        assertEquals("O+", after.findPatientById("PAT-0001").get().getBloodGroup());
        assertEquals(com.hospital.enums.UserRole.PATIENT,
                after.findPatientById("PAT-0001").get().getRole());
    }

    public void testPatientUpdateAndDeleteWriteTheFileAgain() throws Exception {
        String file = path("patients.json");
        com.hospital.service.PatientService service = new com.hospital.service.PatientService(file);
        com.hospital.model.Patient patient = rahim();
        service.registerPatient(patient);
        patient.setPhone("01811111111");
        service.updatePatient(patient);
        assertTrue(readFile(file).contains("01811111111"));
        service.removePatient("PAT-0001");
        assertFalse(readFile(file).contains("Rahim"));
        assertEquals(0, new com.hospital.service.PatientService(file).getPatientCount());
    }

    public void testHandWrittenJsonWithoutTypePropertyIsLoaded() throws Exception {
        String file = path("patients.json");
        writeFile(file, "[ { \"id\" : \"PAT-0001\", \"name\" : \"Rahim\", \"age\" : 25, "
                + "\"gender\" : \"Male\", \"bloodGroup\" : \"O+\", \"phone\" : \"01700000000\" } ]");
        com.hospital.service.PatientService service = new com.hospital.service.PatientService(file);
        assertEquals(1, service.getPatientCount());
        assertEquals("Rahim", service.findPatientById("PAT-0001").get().getName());
        assertEquals("PAT-0001", service.getAllPatients().get(0).getId());
    }

    public void testBrokenJsonFileDoesNotCrashTheProgram() throws Exception {
        String file = path("patients.json");
        writeFile(file, "{ this is not valid json ");
        com.hospital.service.PatientService service = new com.hospital.service.PatientService(file);
        assertEquals(0, service.getPatientCount());
        service.registerPatient(rahim());
        assertEquals(1, new com.hospital.service.PatientService(file).getPatientCount());
    }

    public void testEmptyFileIsHandledSafely() throws Exception {
        String file = path("patients.json");
        writeFile(file, "");
        assertEquals(0, new com.hospital.service.PatientService(file).getPatientCount());
    }
}
