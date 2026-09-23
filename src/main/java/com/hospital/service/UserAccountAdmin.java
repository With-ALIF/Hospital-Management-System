package com.hospital.service;

import com.hospital.enums.AccountStatus;
import com.hospital.enums.AuditAction;
import com.hospital.enums.Permission;
import com.hospital.enums.StaffRole;
import com.hospital.exception.DuplicateUsernameException;
import com.hospital.exception.InvalidCredentialsException;
import com.hospital.exception.UnauthorizedAccessException;
import com.hospital.model.UserAccount;
import com.hospital.repository.UserAccountRepository;
import com.hospital.util.PasswordHasher;
import com.hospital.util.PasswordRules;

import java.util.List;
import java.util.Optional;

public class UserAccountAdmin {
    private final UserAccountRepository repository;
    private final SessionManager session;
    private final AuditLogService audit;
    private final PasswordResetter resetter;
    private final AdminGuard guard;
    private final AccountMutator mutator;

    UserAccountAdmin(UserAccountRepository repository, SessionManager session,
                     AuditLogService audit) {
        this.repository = repository;
        this.session = session;
        this.audit = audit;
        this.resetter = new PasswordResetter(repository, audit);
        this.guard = new AdminGuard(repository, session);
        this.mutator = new AccountMutator(repository, session, audit, guard);
    }

    public UserAccount createAccount(String username, String password,
                                     StaffRole role, String fullName) {
        requireAdmin();
        validateCreate(username, password, fullName);
        String salt = PasswordHasher.newSalt();
        UserAccount account = new UserAccount(repository.nextAccountId(),
                username.trim(), salt, PasswordHasher.hash(password, salt),
                role, fullName.trim());
        repository.add(account);
        audit.record(session.getCurrentUsername(), AuditAction.USER_CREATED,
                "UserAccount", account.getId(),
                "Created " + username + " as " + role);
        return account;
    }

    private void validateCreate(String u, String p, String n) {
        if (u == null || u.isBlank()) {
            throw new InvalidCredentialsException("Username cannot be empty.");
        }
        if (repository.findByUsername(u).isPresent()) {
            throw new DuplicateUsernameException("Username already exists: " + u);
        }
        if (n == null || n.isBlank()) {
            throw new InvalidCredentialsException("Full name cannot be empty.");
        }
        PasswordRules.validate(p);
    }

    public UserAccount updateAccount(UserAccount a, String n, String e, String ph) {
        requireAdmin();
        return mutator.update(a, n, e, ph);
    }

    public UserAccount setStatus(String id, AccountStatus s) {
        requireAdmin();
        return mutator.setStatus(requireAccount(id), s);
    }

    public UserAccount unlock(String id) { return setStatus(id, AccountStatus.ACTIVE); }

    public UserAccount resetPassword(String id, String tmp) {
        return resetter.reset(id, tmp, session, audit);
    }

    public UserAccount changeRole(String id, StaffRole r) {
        requireAdmin();
        return mutator.changeRole(requireAccount(id), r);
    }

    public UserAccount changeOwnProfile(String email, String phone) {
        UserAccount account = session.getCurrentUser();
        if (account == null) {
            throw new UnauthorizedAccessException("Please log in to continue.");
        }
        account.setEmail(email);
        account.setPhone(phone);
        repository.update(account);
        audit.record(account.getUsername(), AuditAction.USER_UPDATED,
                "UserAccount", account.getId(), "Own profile updated");
        return account;
    }

    public UserAccount changeOwnPassword(String oldP, String newP) {
        UserAccount account = session.getCurrentUser();
        if (account == null) {
            throw new UnauthorizedAccessException("Please log in to continue.");
        }
        return resetter.changePassword(account, oldP, newP, audit);
    }

    private void requireAdmin() {
        PermissionService.getInstance().require(Permission.MANAGE_USERS);
    }

    private UserAccount requireAccount(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new InvalidCredentialsException(
                        "Account not found: " + id));
    }

    List<UserAccount> findAll() { return repository.findAll(); }
    Optional<UserAccount> findByUsername(String u) { return repository.findByUsername(u); }
}
