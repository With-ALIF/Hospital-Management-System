package com.hospital.service;

import com.hospital.enums.AccountStatus;
import com.hospital.enums.AuditAction;
import com.hospital.enums.StaffRole;
import com.hospital.exception.InvalidCredentialsException;
import com.hospital.model.UserAccount;
import com.hospital.repository.UserAccountRepository;
import com.hospital.util.PasswordHasher;

import java.time.LocalDateTime;

class AccountSeeder {
    private final UserAccountRepository repository;

    AccountSeeder(UserAccountRepository repository) {
        this.repository = repository;
    }

    void ensureDefaults() {
        seed("admin", "Admin123", StaffRole.ADMIN, "System Administrator");
        seed("doctor1", "Doctor123", StaffRole.DOCTOR, "Duty Doctor");
        seed("reception1", "Reception123", StaffRole.RECEPTIONIST, "Front Desk");
        seed("nurse1", "Nurse1234", StaffRole.NURSE, "Ward Nurse");
        seed("pharmacist1", "Pharma123", StaffRole.PHARMACIST, "Counter Pharmacist");
        seed("lab1", "LabTech123", StaffRole.LAB_TECHNICIAN, "Lab Technician");
    }

    private void seed(String username, String password, StaffRole role,
                      String displayName) {
        if (repository.findByUsername(username).isPresent()) return;
        String salt = PasswordHasher.newSalt();
        UserAccount account = new UserAccount(repository.nextAccountId(), username, salt,
                PasswordHasher.hash(password, salt), role, displayName);
        account.setMustChangePassword(false);
        account.setCreatedAt(LocalDateTime.now());
        repository.add(account);
    }

    UserAccount login(UserAccount account, SessionManager session, AuditLogService audit) {
        account.setFailedAttempts(0);
        account.setLastLogin(LocalDateTime.now());
        account.setStatus(AccountStatus.ACTIVE);
        repository.update(account);
        session.login(account);
        audit.record(account.getUsername(), AuditAction.LOGIN_SUCCESS,
                "UserAccount", account.getId(), "Role: " + account.getRole());
        return account;
    }

    void handleFailedLogin(UserAccount account, AuditLogService audit, int maxFailed) {
        int attempts = account.getFailedAttempts() + 1;
        account.setFailedAttempts(attempts);
        boolean onlyAdmin = account.getRole() == StaffRole.ADMIN
                && countActiveAdmins() <= 1;
        if (attempts >= maxFailed && !onlyAdmin) {
            account.setStatus(AccountStatus.LOCKED);
            audit.record(account.getUsername(), AuditAction.USER_LOCKED,
                    "UserAccount", account.getId(),
                    "Locked after " + attempts + " failed attempts");
        }
        repository.update(account);
        audit.record(account.getUsername(), AuditAction.LOGIN_FAILED,
                "UserAccount", account.getId(),
                "Invalid password, attempts: " + attempts);
    }

    long countActiveAdmins() {
        return repository.findAll().stream()
                .filter(a -> a.getRole() == StaffRole.ADMIN)
                .filter(a -> a.getStatus() == AccountStatus.ACTIVE)
                .count();
    }

    InvalidCredentialsException notFound(String message) {
        return new InvalidCredentialsException(message);
    }
}
