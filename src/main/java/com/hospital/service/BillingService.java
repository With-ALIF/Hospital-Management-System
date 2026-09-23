package com.hospital.service;

import com.hospital.enums.PaymentMethod;
import com.hospital.enums.PaymentStatus;
import com.hospital.exception.InvalidDataException;
import com.hospital.model.Bill;
import com.hospital.model.BillItem;
import com.hospital.model.Payment;
import com.hospital.repository.BillRepository;
import com.hospital.repository.PaymentRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class BillingService {
    private final BillRepository bills;
    private final PaymentRepository payments;

    public BillingService() {
        this(BillRepository.DEFAULT_FILE, PaymentRepository.DEFAULT_FILE);
    }

    public BillingService(String billFile, String paymentFile) {
        this.bills = new BillRepository(billFile);
        this.payments = new PaymentRepository(paymentFile);
    }

    public Bill createBill(String patientId) {
        if (patientId == null || patientId.isBlank()) {
            throw new InvalidDataException("Patient id cannot be empty.");
        }
        Bill bill = new Bill(bills.nextBillId(), patientId);
        bill.setDate(LocalDate.now());
        bills.add(bill);
        return bill;
    }

    public Bill addService(String billId, String description, String category, double amount) {
        Bill bill = requireBill(billId);
        if (description == null || description.isBlank()) {
            throw new InvalidDataException("Bill item description cannot be empty.");
        }
        if (amount <= 0) {
            throw new InvalidDataException("Bill item amount must be positive.");
        }
        bill.addItem(new BillItem(description, category, amount));
        bills.update(bill);
        return bill;
    }

    public Bill applyDiscount(String billId, double discount) {
        Bill bill = requireBill(billId);
        if (discount < 0) {
            throw new InvalidDataException("Discount cannot be negative.");
        }
        bill.setDiscount(discount);
        bills.update(bill);
        return bill;
    }

    public Payment pay(String billId, double amount, PaymentMethod method) {
        Bill bill = requireBill(billId);
        if (amount <= 0) {
            throw new InvalidDataException("Payment amount must be positive.");
        }
        if (amount > bill.getAmountDue() + 0.001) {
            throw new InvalidDataException("Payment exceeds amount due.");
        }
        Payment payment = new Payment(payments.nextPaymentId(), billId, amount, method);
        payments.add(payment);
        bill.applyPayment(amount);
        bills.update(bill);
        return payment;
    }

    public Optional<Bill> findBill(String id) { return bills.findById(id); }
    public List<Bill> getAllBills() { return bills.findAll(); }
    public List<Bill> getBillsForPatient(String patientId) { return bills.findByPatient(patientId); }
    public List<Payment> getAllPayments() { return payments.findAll(); }
    public int getBillCount() { return bills.count(); }
    public int getPaymentCount() { return payments.count(); }
    public double getTotalRevenue() {
        return payments.findAll().stream().mapToDouble(Payment::getAmount).sum();
    }
    public double getOutstanding() {
        return bills.findAll().stream()
                .filter(b -> b.getStatus() != PaymentStatus.PAID)
                .mapToDouble(Bill::getAmountDue).sum();
    }
    public String nextBillId() { return bills.nextBillId(); }
    public String nextPaymentId() { return payments.nextPaymentId(); }

    private Bill requireBill(String id) {
        if (id == null || id.isBlank()) {
            throw new InvalidDataException("Bill id cannot be empty.");
        }
        return bills.findById(id)
                .orElseThrow(() -> new InvalidDataException("Bill not found: " + id));
    }
}
