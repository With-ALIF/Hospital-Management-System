package com.hospital.service;

import com.hospital.enums.AccountStatus;
import com.hospital.enums.AuditAction;
import com.hospital.exception.AccountInactiveException;
import com.hospital.exception.AccountLockedException;
import com.hospital.exception.InvalidCredentialsException;
import com.hospital.model.UserAccount;
import com.hospital.repository.UserAccountRepository;
import com.hospital.util.PasswordHasher;

import java.util.Optional;

class CredentialAuthenticator {
    private static final int MAX_FAILED = 3;

    private final UserAccountRepository repository;
    private final AccountSeeder seeder;
    private final AuditLogService audit;

    CredentialAuthenticator(UserAccountRepository repository, AccountSeeder seeder,
                            AuditLogService audit) {
        this.repository = repository;
        this.seeder = seeder;
        this.audit = audit;
    }

    UserAccount authenticate(String username, String password) {
        if (username == null || username.isBlank()) {
            throw new InvalidCredentialsException("Username cannot be empty.");
        }
        if (password == null || password.isBlank()) {
            throw new InvalidCredentialsException("Password cannot be empty.");
        }
        Optional<UserAccount> found = repository.findByUsername(username.trim());
        if (found.isEmpty()) {
            audit.record(username, AuditAction.LOGIN_FAILED,
                    "UserAccount", "-", "Invalid username");
            throw new InvalidCredentialsException("Invalid username or password.");
        }
        UserAccount account = found.get();
        checkStatus(account);
        if (!PasswordHasher.matches(password, account.getSalt(), account.getPasswordHash())) {
            seeder.handleFailedLogin(account, audit, MAX_FAILED);
            throw new InvalidCredentialsException("Invalid username or password.");
        }
        return account;
    }

    private void checkStatus(UserAccount account) {
        if (account.getStatus() == AccountStatus.LOCKED) {
            audit.record(account.getUsername(), AuditAction.LOGIN_FAILED,
                    "UserAccount", account.getId(), "Account locked");
            throw new AccountLockedException(
                    "Account temporarily locked. Please contact administrator.");
        }
        if (account.getStatus() == AccountStatus.INACTIVE) {
            audit.record(account.getUsername(), AuditAction.LOGIN_FAILED,
                    "UserAccount", account.getId(), "Account inactive");
            throw new AccountInactiveException(
                    "Account is inactive. Please contact administrator.");
        }
    }
}
