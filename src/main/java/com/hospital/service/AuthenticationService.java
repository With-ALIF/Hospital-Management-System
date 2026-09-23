package com.hospital.service;

import com.hospital.enums.AccountStatus;
import com.hospital.enums.AuditAction;
import com.hospital.enums.StaffRole;
import com.hospital.exception.InvalidCredentialsException;
import com.hospital.model.UserAccount;
import com.hospital.repository.UserAccountRepository;
import com.hospital.util.PasswordHasher;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class AuthenticationService {
    private final UserAccountRepository repository;
    private final SessionManager session;
    private final AuditLogService audit;
    private final UserAccountAdmin admin;
    private final AccountSeeder seeder;
    private final CredentialAuthenticator credentials;

    public AuthenticationService() {
        this(UserAccountRepository.DEFAULT_FILE, new AuditLogService());
    }

    public AuthenticationService(String filePath, AuditLogService audit) {
        this.repository = new UserAccountRepository(filePath);
        this.session = SessionManager.getInstance();
        this.audit = audit;
        this.admin = new UserAccountAdmin(repository, session, audit);
        this.seeder = new AccountSeeder(repository);
        this.credentials = new CredentialAuthenticator(repository, seeder, audit);
        seeder.ensureDefaults();
    }

    public UserAccount login(String u, String p) {
        return seeder.login(authenticate(u, p), session, audit);
    }

    public UserAccount authenticate(String u, String p) {
        return credentials.authenticate(u, p);
    }

    public void logout() {
        UserAccount user = session.getCurrentUser();
        if (user != null) {
            audit.record(user.getUsername(), AuditAction.LOGOUT,
                    "UserAccount", user.getId(), "Session ended");
        }
        session.logout();
    }

    public String hashPassword(String p, String s) { return PasswordHasher.hash(p, s); }

    public boolean verifyPassword(String p, String s, String h) {
        return PasswordHasher.matches(p, s, h);
    }

    public void updateLastLogin(UserAccount a) {
        a.setLastLogin(LocalDateTime.now());
        repository.update(a);
    }

    public boolean isAccountActive(UserAccount a) {
        return a != null && a.getStatus() == AccountStatus.ACTIVE;
    }

    public UserAccount changePassword(String u, String oldP, String newP) {
        UserAccount account = repository.findByUsername(u)
                .orElseThrow(() -> new InvalidCredentialsException("Account not found."));
        return new PasswordResetter(repository, audit)
                .changePassword(account, oldP, newP, audit);
    }

    public UserAccount createAccount(String u, String p, StaffRole r, String n) {
        return admin.createAccount(u, p, r, n);
    }

    public UserAccount updateAccount(UserAccount a, String n, String e, String ph) {
        return admin.updateAccount(a, n, e, ph);
    }

    public UserAccount setStatus(String id, AccountStatus s) { return admin.setStatus(id, s); }
    public UserAccount unlock(String id) { return admin.unlock(id); }
    public UserAccount resetPassword(String id, String t) { return admin.resetPassword(id, t); }
    public UserAccount changeRole(String id, StaffRole r) { return admin.changeRole(id, r); }
    public UserAccount changeOwnProfile(String e, String p) { return admin.changeOwnProfile(e, p); }
    public UserAccount changeOwnPassword(String o, String n) { return admin.changeOwnPassword(o, n); }
    public UserAccount getCurrent() { return session.getCurrentUser(); }
    public boolean isLoggedIn() { return session.isLoggedIn(); }
    public List<UserAccount> getAllAccounts() { return admin.findAll(); }
    public Optional<UserAccount> findAccount(String u) { return admin.findByUsername(u); }
    public int getAccountCount() { return repository.count(); }
    public SessionManager getSession() { return session; }
}
