package com.hospital;

import com.hospital.enums.AccountStatus;
import com.hospital.enums.StaffRole;
import com.hospital.exception.AccountInactiveException;
import com.hospital.exception.AccountLockedException;
import com.hospital.exception.DuplicateUsernameException;
import com.hospital.exception.WeakPasswordException;
import com.hospital.service.AuditLogService;
import com.hospital.service.AuthenticationService;
import com.hospital.service.SessionManager;
import junit.framework.TestCase;

import java.nio.file.Files;
import java.nio.file.Path;

public class AuthAccountTest extends TestCase {
    private Path dir;
    private AuthenticationService auth;
    private SessionManager session;

    @Override
    protected void setUp() throws Exception {
        dir = Files.createTempDirectory("hms-auth2");
        auth = new AuthenticationService(dir.resolve("users.json").toString(),
                new AuditLogService(dir.resolve("audit.json").toString()));
        session = SessionManager.getInstance();
        session.logout();
    }

    @Override
    protected void tearDown() throws Exception {
        session.logout();
    }

    public void testWeakPasswordRejected() {
        auth.login("admin", "Admin123");
        try {
            auth.createAccount("weak1", "short", StaffRole.NURSE, "Weak User");
            fail("Expected WeakPasswordException");
        } catch (WeakPasswordException e) {
            assertTrue(e.getMessage().contains("security requirements"));
        }
    }

    public void testDuplicateUsernameRejected() {
        auth.login("admin", "Admin123");
        try {
            auth.createAccount("doctor1", "Doctor999x", StaffRole.DOCTOR, "Dup");
            fail("Expected DuplicateUsernameException");
        } catch (DuplicateUsernameException e) {
            assertNotNull(e.getMessage());
        }
    }

    public void testInactiveLoginBlocked() {
        auth.login("admin", "Admin123");
        var acc = auth.findAccount("nurse1").orElseThrow();
        auth.setStatus(acc.getId(), AccountStatus.INACTIVE);
        auth.logout();
        try {
            auth.login("nurse1", "Nurse1234");
            fail("Expected AccountInactiveException");
        } catch (AccountInactiveException e) {
            assertNotNull(e.getMessage());
        }
    }

    public void testLockedLoginBlocked() {
        auth.login("admin", "Admin123");
        var acc = auth.findAccount("lab1").orElseThrow();
        auth.setStatus(acc.getId(), AccountStatus.LOCKED);
        auth.logout();
        try {
            auth.login("lab1", "LabTech123");
            fail("Expected AccountLockedException");
        } catch (AccountLockedException e) {
            assertNotNull(e.getMessage());
        }
    }

    public void testUnlockAllowsLogin() {
        auth.login("admin", "Admin123");
        var acc = auth.findAccount("lab1").orElseThrow();
        auth.setStatus(acc.getId(), AccountStatus.LOCKED);
        auth.unlock(acc.getId());
        auth.logout();
        assertEquals(StaffRole.LAB_TECHNICIAN, auth.login("lab1", "LabTech123").getRole());
    }
}
