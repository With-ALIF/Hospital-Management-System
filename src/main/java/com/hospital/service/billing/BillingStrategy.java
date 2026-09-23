package com.hospital.service.billing;

import com.hospital.model.Bill;

public interface BillingStrategy {
    double calculate(Bill bill);
    String name();
}
