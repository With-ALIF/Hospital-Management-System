package com.hospital.service.billing;

import com.hospital.model.Bill;

public class GeneralBillingStrategy implements BillingStrategy {
    private static final double TAX_RATE = 0.05;

    @Override
    public double calculate(Bill bill) {
        double sub = bill.getSubtotal();
        double afterDiscount = Math.max(0, sub - bill.getDiscount());
        return afterDiscount + afterDiscount * TAX_RATE;
    }

    @Override
    public String name() { return "GENERAL"; }
}
