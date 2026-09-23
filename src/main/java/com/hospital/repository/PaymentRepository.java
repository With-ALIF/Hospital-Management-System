package com.hospital.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.hospital.model.Payment;

import java.util.List;

public class PaymentRepository extends AbstractJsonRepository<Payment> {
    public static final String DEFAULT_FILE = "data/payments.json";

    public PaymentRepository() { this(DEFAULT_FILE); }
    public PaymentRepository(String filePath) {
        super(filePath, new TypeReference<List<Payment>>() {}, "Payment");
    }

    @Override
    protected String idOf(Payment entity) { return entity.getId(); }

    public String nextPaymentId() { return nextId("PAY-", 1); }
}
