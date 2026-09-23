package com.hospital.service;

import com.hospital.enums.Permission;
import com.hospital.enums.StaffRole;
import com.hospital.exception.UnauthorizedAccessException;

public final class PermissionService {
    private static PermissionService instance;

    private PermissionService() {}

    public static PermissionService getInstance() {
        if (instance == null) instance = new PermissionService();
        return instance;
    }

    public boolean hasPermission(Permission permission) {
        return hasPermission(SessionManager.getInstance().getCurrentRole(), permission);
    }

    public boolean hasPermission(StaffRole role, Permission permission) {
        if (role == null || permission == null) return false;
        if (role == StaffRole.ADMIN) return true;
        return RolePermissions.forRole(role).contains(permission);
    }

    public void require(Permission permission) {
        UserGuard.ensureLoggedIn();
        if (!hasPermission(permission)) {
            throw new UnauthorizedAccessException(
                    "You don't have permission to perform this action.");
        }
    }

    public boolean canViewNav(String navItem) {
        return canViewNav(SessionManager.getInstance().getCurrentRole(), navItem);
    }

    public boolean canViewNav(StaffRole role, String navItem) {
        if (role == null) return false;
        if (role == StaffRole.ADMIN) return true;
        if ("Profile".equals(navItem) || "Logout".equals(navItem)) return true;
        Permission required = RbacNav.requiredPermission(navItem);
        if (required == null) return false;
        return hasPermission(role, required);
    }
}
