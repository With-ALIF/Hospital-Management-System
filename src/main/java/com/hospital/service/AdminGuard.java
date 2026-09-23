package com.hospital.service;

import com.hospital.enums.AccountStatus;
import com.hospital.enums.StaffRole;
import com.hospital.model.UserAccount;
import com.hospital.repository.UserAccountRepository;

class AdminGuard {
    private final UserAccountRepository repository;
    private final SessionManager session;

    AdminGuard(UserAccountRepository repository, SessionManager session) {
        this.repository = repository;
        this.session = session;
    }

    long countActiveAdmins() {
        return repository.findAll().stream()
                .filter(a -> a.getRole() == StaffRole.ADMIN)
                .filter(a -> a.getStatus() == AccountStatus.ACTIVE)
                .count();
    }

    boolean isLastAdmin(UserAccount account) {
        return account.getRole() == StaffRole.ADMIN && countActiveAdmins() <= 1;
    }

    SessionManager session() { return session; }
}
