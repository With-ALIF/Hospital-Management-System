package com.hospital.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.hospital.model.UserAccount;

import java.util.List;
import java.util.Optional;

public class UserAccountRepository extends AbstractJsonRepository<UserAccount> {
    public static final String DEFAULT_FILE = "data/user_accounts.json";

    public UserAccountRepository() { this(DEFAULT_FILE); }
    public UserAccountRepository(String filePath) {
        super(filePath, new TypeReference<List<UserAccount>>() {}, "UserAccount");
    }

    @Override
    protected String idOf(UserAccount entity) { return entity.getId(); }

    public String nextAccountId() { return nextId("ACC-", 1); }

    public Optional<UserAccount> findByUsername(String username) {
        if (username == null) return Optional.empty();
        return findAll().stream()
                .filter(a -> username.equalsIgnoreCase(a.getUsername()))
                .findFirst();
    }
}
