package com.hospital.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.hospital.enums.AccountStatus;
import com.hospital.enums.StaffRole;

import java.time.LocalDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UserAccount {
    private String id;
    private String username;
    private String salt;
    private String passwordHash;
    private StaffRole role;
    private String displayName;
    private boolean active;
    private String email;
    private String phone;
    private AccountStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime lastLogin;
    private int failedAttempts;
    private boolean mustChangePassword;
    private String staffId;

    public UserAccount() {
        this.active = true;
        this.status = AccountStatus.ACTIVE;
        this.createdAt = LocalDateTime.now();
    }

    public UserAccount(String id, String username, String salt, String passwordHash,
                       StaffRole role, String displayName) {
        this();
        this.id = id;
        this.username = username;
        this.salt = salt;
        this.passwordHash = passwordHash;
        this.role = role;
        this.displayName = displayName;
    }

    public AccountStatus getStatus() {
        if (status != null) return status;
        return active ? AccountStatus.ACTIVE : AccountStatus.INACTIVE;
    }

    public void setStatus(AccountStatus status) {
        this.status = status;
        this.active = status == AccountStatus.ACTIVE;
    }

    public boolean isActive() { return getStatus() == AccountStatus.ACTIVE; }

    public void setActive(boolean active) {
        this.active = active;
        this.status = active ? AccountStatus.ACTIVE : AccountStatus.INACTIVE;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getSalt() { return salt; }
    public void setSalt(String salt) { this.salt = salt; }
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public StaffRole getRole() { return role; }
    public void setRole(StaffRole role) { this.role = role; }
    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getLastLogin() { return lastLogin; }
    public void setLastLogin(LocalDateTime lastLogin) { this.lastLogin = lastLogin; }
    public int getFailedAttempts() { return failedAttempts; }
    public void setFailedAttempts(int failedAttempts) { this.failedAttempts = failedAttempts; }
    public boolean isMustChangePassword() { return mustChangePassword; }
    public void setMustChangePassword(boolean mustChangePassword) { this.mustChangePassword = mustChangePassword; }
    public String getStaffId() { return staffId; }
    public void setStaffId(String staffId) { this.staffId = staffId; }
}
