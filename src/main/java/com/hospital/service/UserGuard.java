package com.hospital.service;

import com.hospital.enums.Permission;
import com.hospital.exception.UnauthorizedAccessException;

public final class UserGuard {
    private UserGuard() {}

    public static void ensureLoggedIn() {
        if (!SessionManager.getInstance().isLoggedIn()) {
            throw new UnauthorizedAccessException("Please log in to continue.");
        }
    }

    public static void ensurePermission(Permission permission) {
        PermissionService.getInstance().require(permission);
    }
}
