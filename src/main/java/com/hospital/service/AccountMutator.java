package com.hospital.service;

import com.hospital.enums.AccountStatus;
import com.hospital.enums.AuditAction;
import com.hospital.enums.StaffRole;
import com.hospital.exception.UnauthorizedAccessException;
import com.hospital.model.UserAccount;

class AccountMutator {
    private final com.hospital.repository.UserAccountRepository repository;
    private final SessionManager session;
    private final AuditLogService audit;
    private final AdminGuard guard;

    AccountMutator(com.hospital.repository.UserAccountRepository repository,
                   SessionManager session, AuditLogService audit, AdminGuard guard) {
        this.repository = repository;
        this.session = session;
        this.audit = audit;
        this.guard = guard;
    }

    UserAccount update(UserAccount account, String fullName,
                       String email, String phone) {
        if (fullName != null && !fullName.isBlank()) account.setDisplayName(fullName);
        account.setEmail(email);
        account.setPhone(phone);
        repository.update(account);
        audit.record(session.getCurrentUsername(), AuditAction.USER_UPDATED,
                "UserAccount", account.getId(), "Profile updated");
        return account;
    }

    UserAccount setStatus(UserAccount account, AccountStatus status) {
        if (status == AccountStatus.ACTIVE) account.setFailedAttempts(0);
        account.setStatus(status);
        repository.update(account);
        AuditAction action = switch (status) {
            case ACTIVE -> AuditAction.USER_ACTIVATED;
            case INACTIVE -> AuditAction.USER_DEACTIVATED;
            case LOCKED -> AuditAction.USER_LOCKED;
        };
        audit.record(session.getCurrentUsername(), action,
                "UserAccount", account.getId(), "Status -> " + status);
        return account;
    }

    UserAccount changeRole(UserAccount account, StaffRole newRole) {
        if (account.getRole() == StaffRole.ADMIN && newRole != StaffRole.ADMIN
                && guard.countActiveAdmins() <= 1) {
            throw new UnauthorizedAccessException(
                    "Cannot demote the last administrator.");
        }
        account.setRole(newRole);
        repository.update(account);
        audit.record(session.getCurrentUsername(), AuditAction.ROLE_CHANGED,
                "UserAccount", account.getId(), "Role -> " + newRole);
        return account;
    }
}
