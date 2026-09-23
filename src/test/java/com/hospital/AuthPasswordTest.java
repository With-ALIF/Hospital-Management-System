package com.hospital;

import com.hospital.enums.AccountStatus;
import com.hospital.exception.InvalidCredentialsException;
import com.hospital.exception.WeakPasswordException;
import com.hospital.service.AuditLogService;
import com.hospital.service.AuthenticationService;
import com.hospital.service.SessionManager;
import junit.framework.TestCase;

import java.nio.file.Files;
import java.nio.file.Path;

public class AuthPasswordTest extends TestCase {
    private Path dir;
    private AuthenticationService auth;
    private SessionManager session;

    @Override
    protected void setUp() throws Exception {
        dir = Files.createTempDirectory("hms-auth3");
        auth = new AuthenticationService(dir.resolve("users.json").toString(),
                new AuditLogService(dir.resolve("audit.json").toString()));
        session = SessionManager.getInstance();
        session.logout();
    }

    @Override
    protected void tearDown() throws Exception {
        session.logout();
    }

    public void testChangeOwnPassword() {
        auth.login("doctor1", "Doctor123");
        auth.changeOwnPassword("Doctor123", "Doctor999x");
        auth.logout();
        assertEquals("doctor1", auth.login("doctor1", "Doctor999x").getUsername());
    }

    public void testWrongCurrentPasswordRejected() {
        auth.login("doctor1", "Doctor123");
        try {
            auth.changeOwnPassword("Nope12345", "Doctor999x");
            fail("Expected InvalidCredentialsException");
        } catch (InvalidCredentialsException e) {
            assertNotNull(e.getMessage());
        }
    }

    public void testWeakNewPasswordRejected() {
        auth.login("doctor1", "Doctor123");
        try {
            auth.changeOwnPassword("Doctor123", "short");
            fail("Expected WeakPasswordException");
        } catch (WeakPasswordException e) {
            assertNotNull(e.getMessage());
        }
    }

    public void testFailedAttemptsLockNonAdmin() throws Exception {
        Path d = Files.createTempDirectory("hms-lock");
        AuthenticationService a = new AuthenticationService(
                d.resolve("users.json").toString(),
                new AuditLogService(d.resolve("audit.json").toString()));
        a.login("admin", "Admin123");
        a.createAccount("victim1", "Victim123", com.hospital.enums.StaffRole.NURSE, "Victim Nurse");
        a.logout();
        for (int i = 0; i < 3; i++) {
            try { a.login("victim1", "BadPass11"); } catch (Exception ignored) { }
        }
        assertEquals(AccountStatus.LOCKED, a.findAccount("victim1").orElseThrow().getStatus());
    }

    public void testSingleAdminNotLockedOut() {
        auth.login("admin", "Admin123");
        auth.logout();
        for (int i = 0; i < 4; i++) {
            try { auth.login("admin", "BadPass11"); } catch (Exception ignored) { }
        }
        assertEquals(AccountStatus.ACTIVE, auth.findAccount("admin").orElseThrow().getStatus());
        assertNotNull(auth.login("admin", "Admin123"));
    }
}
