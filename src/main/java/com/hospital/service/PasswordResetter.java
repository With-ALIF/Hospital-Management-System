package com.hospital.service;

import com.hospital.enums.AuditAction;
import com.hospital.exception.InvalidCredentialsException;
import com.hospital.model.UserAccount;
import com.hospital.repository.UserAccountRepository;
import com.hospital.util.PasswordHasher;
import com.hospital.util.PasswordRules;

class PasswordResetter {
    private final UserAccountRepository repository;
    private final AuditLogService audit;

    PasswordResetter(UserAccountRepository repository, AuditLogService audit) {
        this.repository = repository;
        this.audit = audit;
    }

    UserAccount reset(String accountId, String temporaryPassword,
                      SessionManager session, AuditLogService log) {
        UserAccount account = repository.findById(accountId)
                .orElseThrow(() -> new InvalidCredentialsException(
                        "Account not found: " + accountId));
        PasswordRules.validate(temporaryPassword);
        applyHash(account, temporaryPassword);
        account.setMustChangePassword(false);
        account.setFailedAttempts(0);
        if (account.getStatus() == com.hospital.enums.AccountStatus.LOCKED) {
            account.setStatus(com.hospital.enums.AccountStatus.ACTIVE);
        }
        repository.update(account);
        log.record(session.getCurrentUsername(), AuditAction.PASSWORD_CHANGED,
                "UserAccount", account.getId(), "Password reset by admin");
        return account;
    }

    UserAccount changePassword(UserAccount account, String oldPassword,
                               String newPassword, AuditLogService log) {
        if (!PasswordHasher.matches(oldPassword, account.getSalt(),
                account.getPasswordHash())) {
            throw new InvalidCredentialsException("Current password is incorrect.");
        }
        PasswordRules.validate(newPassword);
        applyHash(account, newPassword);
        account.setMustChangePassword(false);
        repository.update(account);
        log.record(account.getUsername(), AuditAction.PASSWORD_CHANGED,
                "UserAccount", account.getId(), "Password updated");
        return account;
    }

    private void applyHash(UserAccount account, String password) {
        String salt = PasswordHasher.newSalt();
        account.setSalt(salt);
        account.setPasswordHash(PasswordHasher.hash(password, salt));
    }
}
