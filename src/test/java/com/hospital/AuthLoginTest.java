package com.hospital;

import com.hospital.enums.StaffRole;
import com.hospital.exception.InvalidCredentialsException;
import com.hospital.model.UserAccount;
import com.hospital.service.AuditLogService;
import com.hospital.service.AuthenticationService;
import com.hospital.service.SessionManager;
import junit.framework.TestCase;

import java.nio.file.Files;
import java.nio.file.Path;

public class AuthLoginTest extends TestCase {
    private Path dir;
    private AuthenticationService auth;
    private SessionManager session;

    @Override
    protected void setUp() throws Exception {
        dir = Files.createTempDirectory("hms-auth");
        auth = new AuthenticationService(dir.resolve("users.json").toString(),
                new AuditLogService(dir.resolve("audit.json").toString()));
        session = SessionManager.getInstance();
        session.logout();
    }

    @Override
    protected void tearDown() throws Exception {
        session.logout();
    }

    public void testAdminLogin() {
        UserAccount u = auth.login("admin", "Admin123");
        assertEquals(StaffRole.ADMIN, u.getRole());
        assertTrue(session.isLoggedIn());
        assertNotNull(session.getLoginTime());
    }

    public void testDoctorLogin() {
        UserAccount u = auth.login("doctor1", "Doctor123");
        assertEquals(StaffRole.DOCTOR, u.getRole());
    }

    public void testNurseLogin() {
        UserAccount u = auth.login("nurse1", "Nurse1234");
        assertEquals(StaffRole.NURSE, u.getRole());
    }

    public void testReceptionistLogin() {
        UserAccount u = auth.login("reception1", "Reception123");
        assertEquals(StaffRole.RECEPTIONIST, u.getRole());
    }

    public void testPharmacistLogin() {
        UserAccount u = auth.login("pharmacist1", "Pharma123");
        assertEquals(StaffRole.PHARMACIST, u.getRole());
    }

    public void testLabLogin() {
        UserAccount u = auth.login("lab1", "LabTech123");
        assertEquals(StaffRole.LAB_TECHNICIAN, u.getRole());
    }

    public void testWrongPassword() {
        try {
            auth.login("admin", "WrongPass1");
            fail("Expected InvalidCredentialsException");
        } catch (InvalidCredentialsException e) {
            assertFalse(session.isLoggedIn());
        }
    }

    public void testUnknownUser() {
        try {
            auth.login("ghost", "Whatever1");
            fail("Expected InvalidCredentialsException");
        } catch (InvalidCredentialsException e) {
            assertFalse(session.isLoggedIn());
        }
    }

    public void testLogoutClearsSession() {
        auth.login("admin", "Admin123");
        assertTrue(session.isLoggedIn());
        auth.logout();
        assertFalse(session.isLoggedIn());
        assertNull(session.getCurrentUser());
    }
}
