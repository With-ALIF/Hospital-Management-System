package com.hospital.repository;

public interface Payable {
    double getAmountDue();

    void applyPayment(double amount);
}
