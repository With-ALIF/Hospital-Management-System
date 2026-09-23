package com.hospital;

import junit.framework.TestCase;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

public class Day3ReferencePersistenceTest extends TestCase {
    private File tempDir;

    @Override
    protected void setUp() throws Exception {
        tempDir = File.createTempFile("hospital-day3ref", "");
        tempDir.delete();
        tempDir.mkdirs();
    }

    @Override
    protected void tearDown() throws Exception {
        File[] files = tempDir.listFiles();
        if (files != null) {
            for (File file : files) file.delete();
        }
        tempDir.delete();
    }

    private String path(String fileName) {
        return new File(tempDir, fileName).getPath();
    }

    private String readFile(String filePath) throws Exception {
        return Files.readString(Path.of(filePath));
    }

    private static com.hospital.model.Patient rahim() {
        return new com.hospital.model.Patient("PAT-0001", "Rahim", "01700000000", "Male", "O+", "01900000000");
    }

    public void testDoctorDutySlotsSurviveTheRoundTrip() throws Exception {
        String file = path("doctors.json");
        var service = new com.hospital.service.DoctorService(file);
        var doctor = new com.hospital.model.Doctor("DOC-0001", "Dr. Karim", "01700000001", "Male", "Cardiology");
        doctor.addDutySlot(new com.hospital.model.TimeSlot(java.time.LocalTime.of(9, 0), java.time.LocalTime.of(13, 0)));
        doctor.addDutySlot(new com.hospital.model.TimeSlot(java.time.LocalTime.of(15, 0), java.time.LocalTime.of(18, 0)));
        service.registerDoctor(doctor);
        var reloaded = new com.hospital.service.DoctorService(file).findDoctorById("DOC-0001").orElse(null);
        assertNotNull(reloaded);
        assertEquals("Dr. Karim", reloaded.getName());
        assertEquals("Cardiology", reloaded.getSpecialization());
        assertEquals(2, reloaded.getDutySlots().size());
        assertTrue(reloaded.isAvailableAt(java.time.LocalTime.of(10, 0)));
        assertFalse(reloaded.isAvailableAt(java.time.LocalTime.of(14, 0)));
        service.setAvailability("DOC-0001", false);
        assertFalse(new com.hospital.service.DoctorService(file).findDoctorById("DOC-0001").get().getAvailable());
    }

    public void testEmergencyCaseNeedsAnExistingPatientAndIsSavedAutomatically() throws Exception {
        String patientFile = path("patients.json");
        String caseFile = path("emergency_cases.json");
        var patientService = new com.hospital.service.PatientService(patientFile);
        patientService.registerPatient(rahim());
        var emergencyService = new com.hospital.service.EmergencyService(patientService, caseFile);
        try {
            emergencyService.registerCase("PAT-9999", com.hospital.enums.EmergencyLevel.SERIOUS, "Unknown patient");
            fail("Expected InvalidDataException for an unknown patient");
        } catch (com.hospital.exception.InvalidDataException expected) {
            assertTrue(expected.getMessage().contains("patient not found"));
        }
        var low = emergencyService.registerCase("PAT-0001", com.hospital.enums.EmergencyLevel.LOW, "Small cut");
        var critical = emergencyService.registerCase("PAT-0001", com.hospital.enums.EmergencyLevel.CRITICAL, "Chest pain");
        assertTrue(readFile(caseFile).contains("Chest pain"));
        var queue = emergencyService.getWaitingQueue();
        assertEquals(2, queue.size());
        assertEquals(critical.getId(), queue.get(0).getId());
        assertEquals(low.getId(), queue.get(1).getId());
        emergencyService.updateStatus(critical.getId(), com.hospital.enums.EmergencyCaseStatus.IN_TREATMENT);
        var after = new com.hospital.service.EmergencyService(new com.hospital.service.PatientService(patientFile), caseFile);
        assertEquals(2, after.getCaseCount());
        assertEquals(com.hospital.enums.EmergencyCaseStatus.IN_TREATMENT,
                after.findCaseById(critical.getId()).get().getStatus());
        assertEquals(1, after.getWaitingQueue().size());
        assertEquals(2, after.getCasesForPatient("PAT-0001").size());
    }

    public void testAppointmentRoundTripKeepsIdAndReferences() throws Exception {
        String dir = tempDir.getAbsolutePath();
        var service = new com.hospital.service.HospitalService(dir);
        var doctor = new com.hospital.model.Doctor("DOC-0001", "Dr. Karim", "01700000001", "Male", "Cardiology");
        doctor.addDutySlot(new com.hospital.model.TimeSlot(java.time.LocalTime.of(9, 0), java.time.LocalTime.of(13, 0)));
        service.addDoctor(doctor);
        service.addPatient(rahim());
        service.bookAppointment(new com.hospital.model.Appointment("APT-0001", rahim(), doctor,
                java.time.LocalDate.of(2026, 9, 19), java.time.LocalTime.of(10, 30), "Checkup",
                com.hospital.enums.AppointmentStatus.SCHEDULED));
        var after = new com.hospital.service.HospitalService(dir);
        assertEquals(1, after.getAppointments().size());
        var reloaded = after.getAppointments().get(0);
        assertEquals("APT-0001", reloaded.getId());
        assertEquals("Rahim", reloaded.getPatientName());
        assertEquals("Dr. Karim", reloaded.getDoctorName());
        assertEquals(java.time.LocalDate.of(2026, 9, 19), reloaded.getDate());
        assertEquals(java.time.LocalTime.of(10, 30), reloaded.getTime());
        assertEquals(com.hospital.enums.AppointmentStatus.SCHEDULED, reloaded.getStatus());
    }
}
