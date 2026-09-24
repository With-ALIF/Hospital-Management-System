package com.hospital;

import com.hospital.enums.Permission;
import com.hospital.enums.StaffRole;
import com.hospital.exception.UnauthorizedAccessException;
import com.hospital.service.DoctorService;
import com.hospital.service.PermissionService;
import com.hospital.service.SessionManager;
import junit.framework.TestCase;

import java.io.File;
import java.util.List;

public class SchedulePermissionTest extends TestCase {
    private final PermissionService perms = PermissionService.getInstance();

    public void testOnlyAdminHasManageSchedule() {
        assertTrue(perms.hasPermission(StaffRole.ADMIN, Permission.MANAGE_SCHEDULE));
        for (StaffRole role : new StaffRole[]{StaffRole.DOCTOR, StaffRole.NURSE,
                StaffRole.RECEPTIONIST, StaffRole.PHARMACIST, StaffRole.LAB_TECHNICIAN}) {
            assertFalse("Only admin should manage schedules: " + role,
                    perms.hasPermission(role, Permission.MANAGE_SCHEDULE));
        }
    }

    public void testUpdateScheduleRequiresLogin() throws Exception {
        SessionManager.getInstance().logout();
        File temp = File.createTempFile("hospital-sched", ".json");
        temp.delete();
        var service = new DoctorService(temp.getPath());
        var doctor = new com.hospital.model.Doctor("DOC-0001", "Dr. Test",
                "01700000000", "Male", "Cardiology");
        service.registerDoctor(doctor);
        try {
            service.updateSchedule("DOC-0001", false, List.of());
            fail("Expected UnauthorizedAccessException without login");
        } catch (UnauthorizedAccessException expected) {
            assertTrue(expected.getMessage().contains("log in"));
        } finally {
            temp.delete();
            SessionManager.getInstance().logout();
        }
    }
}
