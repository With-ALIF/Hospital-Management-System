package com.hospital.service.billing;

import com.hospital.model.Bill;

public class InsuranceBillingStrategy implements BillingStrategy {
    private static final double PATIENT_SHARE = 0.20;
    private static final double TAX_RATE = 0.0;

    @Override
    public double calculate(Bill bill) {
        double sub = bill.getSubtotal();
        double afterDiscount = Math.max(0, sub - bill.getDiscount());
        return afterDiscount * PATIENT_SHARE + afterDiscount * TAX_RATE;
    }

    @Override
    public String name() { return "INSURANCE"; }
}
