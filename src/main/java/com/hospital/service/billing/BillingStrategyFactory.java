package com.hospital.service.billing;

import com.hospital.exception.InvalidDataException;

import java.util.HashMap;
import java.util.Map;

public final class BillingStrategyFactory {
    private static final BillingStrategyFactory INSTANCE = new BillingStrategyFactory();
    private final Map<String, BillingStrategy> registry = new HashMap<>();

    private BillingStrategyFactory() {
        register(new GeneralBillingStrategy());
        register(new EmergencyBillingStrategy());
        register(new InsuranceBillingStrategy());
    }

    public static BillingStrategyFactory getInstance() { return INSTANCE; }

    public void register(BillingStrategy strategy) {
        if (strategy != null) registry.put(strategy.name(), strategy);
    }

    public BillingStrategy get(String name) {
        if (name == null || name.isBlank()) {
            return registry.get("GENERAL");
        }
        BillingStrategy s = registry.get(name.trim().toUpperCase());
        if (s == null) {
            throw new InvalidDataException("Unknown billing strategy: " + name);
        }
        return s;
    }

    public BillingStrategy forEmergency() { return get("EMERGENCY"); }
    public BillingStrategy forGeneral() { return get("GENERAL"); }
    public BillingStrategy forInsurance() { return get("INSURANCE"); }
}
