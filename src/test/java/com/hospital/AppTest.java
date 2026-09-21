package com.hospital;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class AppTest 
    extends TestCase
{
    /** Temporary data directory so that a test run never changes the real data/ files. */
    private java.io.File tempDataDir;

    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest( String testName )
    {
        super( testName );
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

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( AppTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp()
    {
        assertTrue( true );
    }

    public void testAppointmentWithinDutyHours() {
        com.hospital.service.HospitalService service = newService();
        com.hospital.model.Doctor doctor = new com.hospital.model.Doctor("D-1001", "Dr. Rahman", "01700000000", "MALE", "Cardiology");
        doctor.addDutySlot(new com.hospital.model.TimeSlot(java.time.LocalTime.of(9, 0), java.time.LocalTime.of(13, 0)));
        doctor.addDutySlot(new com.hospital.model.TimeSlot(java.time.LocalTime.of(15, 0), java.time.LocalTime.of(18, 0)));
        service.addDoctor(doctor);

        com.hospital.model.Patient patient = new com.hospital.model.Patient("P-1001", "Rahim", "01800000000", "MALE", "O+", "01900000000");
        service.addPatient(patient);

        // Booking at 10:30 (inside 09:00 - 13:00) should succeed
        com.hospital.model.Appointment apt = new com.hospital.model.Appointment(
                "A-1001",
                patient,
                doctor,
                java.time.LocalDate.of(2026, 9, 19),
                java.time.LocalTime.of(10, 30),
                "Chest Pain",
                com.hospital.model.AppointmentStatus.SCHEDULED
        );
        service.bookAppointment(apt);
        assertEquals(1, service.getAppointments().size());
        assertEquals(com.hospital.model.AppointmentStatus.SCHEDULED, service.getAppointments().get(0).getStatus());

        // Update status to CONFIRMED
        service.updateAppointmentStatus("A-1001", com.hospital.model.AppointmentStatus.CONFIRMED);
        assertEquals(com.hospital.model.AppointmentStatus.CONFIRMED, service.getAppointments().get(0).getStatus());
    }

    public void testAppointmentOutsideDutyHoursThrowsException() {
        com.hospital.service.HospitalService service = newService();
        com.hospital.model.Doctor doctor = new com.hospital.model.Doctor("D-1001", "Dr. Rahman", "01700000000", "MALE", "Cardiology");
        doctor.addDutySlot(new com.hospital.model.TimeSlot(java.time.LocalTime.of(9, 0), java.time.LocalTime.of(13, 0)));
        doctor.addDutySlot(new com.hospital.model.TimeSlot(java.time.LocalTime.of(15, 0), java.time.LocalTime.of(18, 0)));
        service.addDoctor(doctor);

        com.hospital.model.Patient patient = new com.hospital.model.Patient("P-1001", "Rahim", "01800000000", "MALE", "O+", "01900000000");
        service.addPatient(patient);

        // Booking at 14:00 (outside 09:00-13:00 and 15:00-18:00) should throw InvalidDataException
        com.hospital.model.Appointment apt = new com.hospital.model.Appointment(
                "A-1002",
                patient,
                doctor,
                java.time.LocalDate.of(2026, 9, 19),
                java.time.LocalTime.of(14, 0),
                "Checkup",
                com.hospital.model.AppointmentStatus.SCHEDULED
        );
        try {
            service.bookAppointment(apt);
            fail("Expected InvalidDataException when booking outside duty hours");
        } catch (com.hospital.exception.InvalidDataException expected) {
            assertTrue(expected.getMessage().contains("outside"));
        }
    }
}
