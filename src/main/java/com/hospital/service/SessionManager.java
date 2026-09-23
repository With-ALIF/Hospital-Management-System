package com.hospital.service;

import com.hospital.enums.StaffRole;
import com.hospital.model.UserAccount;

import java.time.LocalDateTime;

public final class SessionManager {
    private static SessionManager instance;
    private UserAccount currentUser;
    private LocalDateTime loginTime;

    private SessionManager() {}

    public static SessionManager getInstance() {
        if (instance == null) instance = new SessionManager();
        return instance;
    }

    public void login(UserAccount user) {
        this.currentUser = user;
        this.loginTime = LocalDateTime.now();
    }

    public void logout() {
        this.currentUser = null;
        this.loginTime = null;
    }

    public UserAccount getCurrentUser() { return currentUser; }

    public StaffRole getCurrentRole() {
        return currentUser == null ? null : currentUser.getRole();
    }

    public LocalDateTime getLoginTime() { return loginTime; }

    public boolean isLoggedIn() { return currentUser != null; }

    public String getCurrentUsername() {
        return currentUser == null ? "" : currentUser.getUsername();
    }
}
