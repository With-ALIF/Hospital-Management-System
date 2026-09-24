package com.hospital;

import com.hospital.enums.Permission;
import com.hospital.enums.StaffRole;
import com.hospital.service.PermissionService;
import com.hospital.service.RbacNav;
import com.hospital.util.RbacPolicy;
import junit.framework.TestCase;

public class RbacPermissionTest extends TestCase {
    private final PermissionService perms = PermissionService.getInstance();

    public void testAdminHasAllNav() {
        for (String item : RbacNav.orderedItems()) {
            assertTrue("Admin should view " + item,
                    perms.canViewNav(StaffRole.ADMIN, item));
        }
        assertTrue(perms.hasPermission(StaffRole.ADMIN, Permission.MANAGE_USERS));
        assertTrue(perms.hasPermission(StaffRole.ADMIN, Permission.BACKUP_DATA));
    }

    public void testDoctorPharmacyDenied() {
        assertFalse(perms.hasPermission(StaffRole.DOCTOR, Permission.MANAGE_PHARMACY));
        assertTrue(perms.hasPermission(StaffRole.DOCTOR, Permission.VIEW_PHARMACY));
        assertTrue(perms.hasPermission(StaffRole.DOCTOR, Permission.CREATE_PRESCRIPTION));
        assertFalse(perms.hasPermission(StaffRole.DOCTOR, Permission.MANAGE_USERS));
        assertFalse(perms.hasPermission(StaffRole.DOCTOR, Permission.RESTORE_DATA));
    }

    public void testNurseUserMgmtDenied() {
        assertFalse(perms.hasPermission(StaffRole.NURSE, Permission.MANAGE_USERS));
        assertTrue(perms.hasPermission(StaffRole.NURSE, Permission.UPDATE_VITALS));
        assertFalse(perms.hasPermission(StaffRole.NURSE, Permission.MANAGE_PHARMACY));
    }

    public void testPharmacistAllowed() {
        assertTrue(perms.hasPermission(StaffRole.PHARMACIST, Permission.MANAGE_PHARMACY));
        assertFalse(perms.hasPermission(StaffRole.PHARMACIST, Permission.MANAGE_USERS));
        assertFalse(perms.hasPermission(StaffRole.PHARMACIST, Permission.UPDATE_LAB_RESULT));
    }

    public void testLabAllowed() {
        assertTrue(perms.hasPermission(StaffRole.LAB_TECHNICIAN, Permission.UPDATE_LAB_RESULT));
        assertTrue(perms.hasPermission(StaffRole.LAB_TECHNICIAN, Permission.CREATE_LAB));
        assertFalse(perms.hasPermission(StaffRole.LAB_TECHNICIAN, Permission.MANAGE_PHARMACY));
        assertFalse(perms.hasPermission(StaffRole.LAB_TECHNICIAN, Permission.MANAGE_USERS));
    }

    public void testReceptionistAllowed() {
        assertTrue(perms.hasPermission(StaffRole.RECEPTIONIST, Permission.CREATE_PATIENT));
        assertTrue(perms.hasPermission(StaffRole.RECEPTIONIST, Permission.MANAGE_BILLING));
        assertFalse(perms.hasPermission(StaffRole.RECEPTIONIST, Permission.MANAGE_PHARMACY));
        assertFalse(perms.hasPermission(StaffRole.RECEPTIONIST, Permission.MANAGE_USERS));
    }

    public void testNavProfileAlwaysVisible() {
        for (StaffRole r : StaffRole.values()) {
            assertTrue(perms.canViewNav(r, "Profile"));
            assertTrue(perms.canViewNav(r, "Logout"));
            assertTrue(perms.canViewNav(r, "Dashboard"));
        }
    }

    public void testNavUserMgmtAdminOnly() {
        assertTrue(perms.canViewNav(StaffRole.ADMIN, "User Management"));
        for (StaffRole r : StaffRole.values()) {
            if (r == StaffRole.ADMIN) continue;
            assertFalse(perms.canViewNav(r, "User Management"));
        }
    }

    public void testNavItemsRequirePermission() {
        assertEquals(Permission.VIEW_BLOOD_BANK, RbacNav.requiredPermission("Blood Bank"));
        assertEquals(Permission.MANAGE_USERS, RbacNav.requiredPermission("User Management"));
        assertEquals(Permission.VIEW_DASHBOARD, RbacNav.requiredPermission("Dashboard"));
        assertEquals(Permission.VIEW_PROFILE, RbacNav.requiredPermission("Profile"));
        assertEquals(Permission.BACKUP_DATA, RbacNav.requiredPermission("Backups"));
        assertNull(RbacNav.requiredPermission("Unknown Page"));
    }

    public void testRbacPolicyStringActions() {
        assertTrue(RbacPolicy.can(StaffRole.ADMIN, "delete"));
        assertTrue(RbacPolicy.can(StaffRole.DOCTOR, "create_appointment"));
        assertFalse(RbacPolicy.can(StaffRole.DOCTOR, "pharmacy.sell"));
        assertTrue(RbacPolicy.can(StaffRole.PHARMACIST, "pharmacy.sell"));
        assertFalse(RbacPolicy.can(StaffRole.NURSE, "users.manage"));
        assertTrue(RbacPolicy.can(StaffRole.DOCTOR, "prescription.create"));
        assertFalse(RbacPolicy.can(StaffRole.PHARMACIST, "lab.result"));
    }

    public void testNullRoleDenied() {
        assertFalse(perms.hasPermission(null, Permission.VIEW_DASHBOARD));
        assertFalse(perms.canViewNav(null, "Dashboard"));
        assertFalse(perms.hasPermission(StaffRole.DOCTOR, null));
    }

    public void testDoctorSeesOnlyDoctorModules() {
        for (String item : new String[]{"Dashboard", "Patients", "Appointments",
                "Emergency", "Operations", "Vital Monitoring", "Follow-Ups",
                "Doctors", "Doctor Schedule", "Reports", "Profile"}) {
            assertTrue("Doctor should view " + item,
                    perms.canViewNav(StaffRole.DOCTOR, item));
        }
        for (String item : new String[]{"Blood Bank", "Ambulance", "Staff Shifts",
                "Equipment", "Backups", "Settings", "User Management"}) {
            assertFalse("Doctor should NOT view " + item,
                    perms.canViewNav(StaffRole.DOCTOR, item));
        }
    }

    public void testNurseSeesOnlyWardCareModules() {
        for (String item : new String[]{"Dashboard", "Patients", "Emergency",
                "Vital Monitoring", "Follow-Ups", "Blood Bank",
                "Staff Shifts", "Equipment", "Profile"}) {
            assertTrue("Nurse should view " + item,
                    perms.canViewNav(StaffRole.NURSE, item));
        }
        for (String item : new String[]{"Appointments", "Operations", "Doctors",
                "Doctor Schedule", "Ambulance", "Reports",
                "Backups", "Settings", "User Management"}) {
            assertFalse("Nurse should NOT view " + item,
                    perms.canViewNav(StaffRole.NURSE, item));
        }
    }

    public void testReceptionistSeesOnlyFrontDeskModules() {
        for (String item : new String[]{"Dashboard", "Patients", "Appointments",
                "Emergency", "Doctors", "Doctor Schedule", "Reports", "Profile"}) {
            assertTrue("Receptionist should view " + item,
                    perms.canViewNav(StaffRole.RECEPTIONIST, item));
        }
        for (String item : new String[]{"Operations", "Blood Bank", "Equipment",
                "Staff Shifts", "Backups",
                "Settings", "User Management"}) {
            assertFalse("Receptionist should NOT view " + item,
                    perms.canViewNav(StaffRole.RECEPTIONIST, item));
        }
    }

    public void testPharmacistSeesOnlyDispensaryModules() {
        for (String item : new String[]{"Dashboard", "Blood Bank", "Reports", "Profile"}) {
            assertTrue("Pharmacist should view " + item,
                    perms.canViewNav(StaffRole.PHARMACIST, item));
        }
        for (String item : new String[]{"Patients", "Equipment", "Appointments",
                "Emergency", "Doctors", "Backups",
                "Settings", "User Management"}) {
            assertFalse("Pharmacist should NOT view " + item,
                    perms.canViewNav(StaffRole.PHARMACIST, item));
        }
    }

    public void testLabSeesOnlyLabModules() {
        for (String item : new String[]{"Dashboard", "Reports", "Profile"}) {
            assertTrue("Lab technician should view " + item,
                    perms.canViewNav(StaffRole.LAB_TECHNICIAN, item));
        }
        for (String item : new String[]{"Patients", "Emergency", "Appointments",
                "Doctors", "Blood Bank", "Equipment", "Backups",
                "Settings", "User Management"}) {
            assertFalse("Lab technician should NOT view " + item,
                    perms.canViewNav(StaffRole.LAB_TECHNICIAN, item));
        }
    }
}
