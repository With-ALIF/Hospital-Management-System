package com.hospital;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class AppTest extends TestCase {
    private java.io.File tempDataDir;

    public AppTest(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
        tempDataDir = java.io.File.createTempFile("hospital-test-data", "");
        tempDataDir.delete();
        tempDataDir.mkdirs();
    }

    protected void tearDown() throws Exception {
        java.io.File[] files = tempDataDir.listFiles();
        if (files != null) {
            for (java.io.File file : files) {
                file.delete();
            }
        }
        tempDataDir.delete();
    }

    private com.hospital.service.HospitalService newService() {
        return new com.hospital.service.HospitalService(tempDataDir.getAbsolutePath());
    }

    public static Test suite() {
        return new TestSuite(AppTest.class);
    }

    public void testApp() {
        assertTrue(true);
    }

    public void testAppointmentWithinDutyHours() {
        com.hospital.service.HospitalService service = newService();
        com.hospital.model.Doctor doctor = new com.hospital.model.Doctor("DOC-0001", "Dr. Rahman", "01700000000", "MALE", "Cardiology");
        doctor.addDutySlot(new com.hospital.model.TimeSlot(java.time.LocalTime.of(9, 0), java.time.LocalTime.of(13, 0)));
        doctor.addDutySlot(new com.hospital.model.TimeSlot(java.time.LocalTime.of(15, 0), java.time.LocalTime.of(18, 0)));
        service.addDoctor(doctor);

        com.hospital.model.Patient patient = new com.hospital.model.Patient("PAT-0001", "Rahim", "01800000000", "MALE", "O+", "01900000000");
        service.addPatient(patient);

        com.hospital.model.Appointment apt = new com.hospital.model.Appointment(
                "APT-0001",
                patient,
                doctor,
                java.time.LocalDate.of(2026, 9, 19),
                java.time.LocalTime.of(10, 30),
                "Chest Pain",
                com.hospital.enums.AppointmentStatus.SCHEDULED
        );
        service.bookAppointment(apt);
        assertEquals(1, service.getAppointments().size());
        assertEquals(com.hospital.enums.AppointmentStatus.SCHEDULED, service.getAppointments().get(0).getStatus());

        service.updateAppointmentStatus("APT-0001", com.hospital.enums.AppointmentStatus.CONFIRMED);
        assertEquals(com.hospital.enums.AppointmentStatus.CONFIRMED, service.getAppointments().get(0).getStatus());
    }

    public void testAppointmentOutsideDutyHoursThrowsException() {
        com.hospital.service.HospitalService service = newService();
        com.hospital.model.Doctor doctor = new com.hospital.model.Doctor("DOC-0001", "Dr. Rahman", "01700000000", "MALE", "Cardiology");
        doctor.addDutySlot(new com.hospital.model.TimeSlot(java.time.LocalTime.of(9, 0), java.time.LocalTime.of(13, 0)));
        doctor.addDutySlot(new com.hospital.model.TimeSlot(java.time.LocalTime.of(15, 0), java.time.LocalTime.of(18, 0)));
        service.addDoctor(doctor);

        com.hospital.model.Patient patient = new com.hospital.model.Patient("PAT-0001", "Rahim", "01800000000", "MALE", "O+", "01900000000");
        service.addPatient(patient);

        com.hospital.model.Appointment apt = new com.hospital.model.Appointment(
                "APT-0002",
                patient,
                doctor,
                java.time.LocalDate.of(2026, 9, 19),
                java.time.LocalTime.of(14, 0),
                "Checkup",
                com.hospital.enums.AppointmentStatus.SCHEDULED
        );
        try {
            service.bookAppointment(apt);
            fail("Expected InvalidDataException when booking outside duty hours");
        } catch (com.hospital.exception.InvalidDataException expected) {
            assertTrue(expected.getMessage().contains("outside"));
        }
    }
}
