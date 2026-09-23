package com.hospital;

import com.hospital.enums.Permission;
import com.hospital.enums.StaffRole;
import com.hospital.exception.UnauthorizedAccessException;
import com.hospital.model.UserAccount;
import com.hospital.service.PermissionService;
import com.hospital.service.SessionManager;
import junit.framework.TestCase;

public class AuthSessionGuardTest extends TestCase {
    private final PermissionService perms = PermissionService.getInstance();
    private final SessionManager session = SessionManager.getInstance();

    @Override
    protected void tearDown() throws Exception {
        session.logout();
    }

    public void testUnauthorizedAccessMessage() {
        session.login(new UserAccount(
                "ACC-X", "doc", "s", "h", StaffRole.DOCTOR, "Doc"));
        assertFalse(perms.hasPermission(Permission.MANAGE_USERS));
        try {
            perms.require(Permission.MANAGE_USERS);
            fail("Expected UnauthorizedAccessException");
        } catch (UnauthorizedAccessException e) {
            assertTrue(e.getMessage().toLowerCase().contains("permission"));
        }
    }

    public void testNoSessionRequireFails() {
        session.logout();
        try {
            perms.require(Permission.VIEW_DASHBOARD);
            fail("Expected UnauthorizedAccessException");
        } catch (UnauthorizedAccessException e) {
            assertTrue(e.getMessage().toLowerCase().contains("log in"));
        }
    }

    public void testSessionRoleUsedWhenLoggedIn() {
        session.logout();
        session.login(new UserAccount(
                "ACC-R", "ph", "s", "h", StaffRole.PHARMACIST, "Ph"));
        assertTrue(perms.hasPermission(Permission.MANAGE_PHARMACY));
        assertFalse(perms.hasPermission(Permission.MANAGE_USERS));
        session.logout();
        assertFalse(perms.hasPermission(Permission.MANAGE_PHARMACY));
    }
}
