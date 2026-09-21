package com.hospital;

import com.hospital.exception.InvalidDataException;
import com.hospital.model.Appointment;
import com.hospital.model.AppointmentStatus;
import com.hospital.model.Doctor;
import com.hospital.model.EmergencyCase;
import com.hospital.model.EmergencyCaseStatus;
import com.hospital.model.EmergencyPriority;
import com.hospital.model.Patient;
import com.hospital.model.TimeSlot;
import com.hospital.model.UserRole;
import com.hospital.service.DoctorService;
import com.hospital.service.EmergencyService;
import com.hospital.service.HospitalService;
import com.hospital.service.PatientService;

import junit.framework.TestCase;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * Day 3 tests: JSON persistence and auto synchronization.
 *
 * Every test works in a temporary directory, so the real files inside
 * {@code data/} are never modified by a test run.
 */
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

    private static Patient rahim() {
        return new Patient("P-1001", "Rahim", "01700000000", "Male", "O+", "01900000000");
    }

    // ------------------------------------------------ startup loading / auto sync

    public void testPatientIsSavedAutomaticallyAndLoadedAgain() throws Exception {
        String file = path("patients.json");
        PatientService service = new PatientService(file);
        service.registerPatient(rahim());

        String json = readFile(file);
        assertTrue("patients.json must contain the new patient", json.contains("Rahim"));
        assertTrue("the id must be stored in the JSON file", json.contains("P-1001"));

        // "restart" of the program: a brand new service reads the same file
        PatientService afterRestart = new PatientService(file);
        assertEquals(1, afterRestart.getPatientCount());
        assertNotNull("the patient must survive a restart", afterRestart.findPatientById("P-1001").orElse(null));
        assertEquals("O+", afterRestart.findPatientById("P-1001").get().getBloodGroup());
        assertEquals(UserRole.PATIENT, afterRestart.findPatientById("P-1001").get().getRole());
    }

    public void testPatientUpdateAndDeleteWriteTheFileAgain() throws Exception {
        String file = path("patients.json");
        PatientService service = new PatientService(file);
        Patient patient = rahim();
        service.registerPatient(patient);

        patient.setPhone("01811111111");
        service.updatePatient(patient);
        assertTrue("the updated phone must be inside the file", readFile(file).contains("01811111111"));
        assertEquals("01811111111", new PatientService(file).findPatientById("P-1001").get().getPhone());

        service.removePatient("P-1001");
        assertFalse("the deleted patient must be gone from the file", readFile(file).contains("Rahim"));
        assertEquals(0, new PatientService(file).getPatientCount());
    }

    public void testDoctorDutySlotsSurviveTheRoundTrip() throws Exception {
        String file = path("doctors.json");
        DoctorService service = new DoctorService(file);
        Doctor doctor = new Doctor("D-1001", "Dr. Karim", "01700000001", "Male", "Cardiology");
        doctor.addDutySlot(new TimeSlot(LocalTime.of(9, 0), LocalTime.of(13, 0)));
        doctor.addDutySlot(new TimeSlot(LocalTime.of(15, 0), LocalTime.of(18, 0)));
        service.registerDoctor(doctor);

        Doctor reloaded = new DoctorService(file).findDoctorById("D-1001").orElse(null);
        assertNotNull("the doctor must survive a restart", reloaded);
        assertEquals("Dr. Karim", reloaded.getName());
        assertEquals("Cardiology", reloaded.getSpecialization());
        assertEquals(2, reloaded.getDutySlots().size());
        assertTrue(reloaded.isAvailableAt(LocalTime.of(10, 0)));
        assertFalse(reloaded.isAvailableAt(LocalTime.of(14, 0)));

        service.setAvailability("D-1001", false);
        assertFalse("availability changes must be saved", new DoctorService(file).findDoctorById("D-1001").get().getAvailable());
    }

    // ------------------------------------------------------- emergency cases

    public void testEmergencyCaseNeedsAnExistingPatientAndIsSavedAutomatically() throws Exception {
        String patientFile = path("patients.json");
        String caseFile = path("emergency_cases.json");
        PatientService patientService = new PatientService(patientFile);
        patientService.registerPatient(rahim());
        EmergencyService emergencyService = new EmergencyService(patientService, caseFile);

        try {
            emergencyService.registerCase("P-9999", EmergencyPriority.HIGH, "Unknown patient");
            fail("Expected InvalidDataException for an unknown patient");
        } catch (InvalidDataException expected) {
            assertTrue(expected.getMessage().contains("patient not found"));
        }

        EmergencyCase low = emergencyService.registerCase("P-1001", EmergencyPriority.LOW, "Small cut");
        EmergencyCase critical = emergencyService.registerCase("P-1001", EmergencyPriority.CRITICAL, "Chest pain");
        assertTrue("the case must be in the JSON file", readFile(caseFile).contains("Chest pain"));

        List<EmergencyCase> queue = emergencyService.getWaitingQueue();
        assertEquals(2, queue.size());
        assertEquals("the most severe case comes first", critical.getId(), queue.get(0).getId());
        assertEquals(low.getId(), queue.get(1).getId());

        emergencyService.updateStatus(critical.getId(), EmergencyCaseStatus.IN_TREATMENT);

        EmergencyService afterRestart = new EmergencyService(new PatientService(patientFile), caseFile);
        assertEquals(2, afterRestart.getCaseCount());
        assertEquals(EmergencyCaseStatus.IN_TREATMENT,
                afterRestart.findCaseById(critical.getId()).get().getStatus());
        assertEquals("only the low priority case is still waiting", 1, afterRestart.getWaitingQueue().size());
        assertEquals(2, afterRestart.getCasesForPatient("P-1001").size());
    }

    // --------------------------------------------- hospital service (appointments)

    public void testAppointmentRoundTripKeepsIdAndReferences() throws Exception {
        String dir = tempDir.getAbsolutePath();
        HospitalService service = new HospitalService(dir);
        Doctor doctor = new Doctor("D-1001", "Dr. Karim", "01700000001", "Male", "Cardiology");
        doctor.addDutySlot(new TimeSlot(LocalTime.of(9, 0), LocalTime.of(13, 0)));
        service.addDoctor(doctor);
        Patient patient = rahim();
        service.addPatient(patient);
        service.bookAppointment(new Appointment("A-1001", patient, doctor,
                LocalDate.of(2026, 9, 19), LocalTime.of(10, 30), "Checkup", AppointmentStatus.SCHEDULED));

        // restart: the appointment is read back from appointments.json
        HospitalService afterRestart = new HospitalService(dir);
        assertEquals(1, afterRestart.getAppointments().size());
        Appointment reloaded = afterRestart.getAppointments().get(0);
        assertEquals("the appointment id must be saved", "A-1001", reloaded.getId());
        assertEquals("Rahim", reloaded.getPatientName());
        assertEquals("Dr. Karim", reloaded.getDoctorName());
        assertEquals(LocalDate.of(2026, 9, 19), reloaded.getDate());
        assertEquals(LocalTime.of(10, 30), reloaded.getTime());
        assertEquals(AppointmentStatus.SCHEDULED, reloaded.getStatus());
    }

    // --------------------------------------------- hand written / broken JSON files

    public void testHandWrittenJsonWithoutTypePropertyIsLoaded() throws Exception {
        String file = path("patients.json");
        writeFile(file, "[ { \"id\" : \"P-1001\", \"name\" : \"Rahim\", \"age\" : 25, "
                + "\"gender\" : \"Male\", \"bloodGroup\" : \"O+\", \"phone\" : \"01700000000\" } ]");

        PatientService service = new PatientService(file);
        assertEquals(1, service.getPatientCount());
        assertEquals("Rahim", service.findPatientById("P-1001").get().getName());
        assertEquals("unknown fields such as 'age' are ignored", "P-1001", service.getAllPatients().get(0).getId());
    }

    public void testBrokenJsonFileDoesNotCrashTheProgram() throws Exception {
        String file = path("patients.json");
        writeFile(file, "{ this is not valid json ");

        PatientService service = new PatientService(file);
        assertEquals("a broken file must not stop the program", 0, service.getPatientCount());

        service.registerPatient(rahim());
        assertEquals(1, new PatientService(file).getPatientCount());
    }

    public void testEmptyFileIsHandledSafely() throws Exception {
        String file = path("patients.json");
        writeFile(file, "");

        assertEquals(0, new PatientService(file).getPatientCount());
    }
}