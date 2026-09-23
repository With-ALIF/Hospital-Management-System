package com.hospital.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.hospital.enums.PaymentMethod;

import java.time.LocalDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Payment {
    private String id;
    private String billId;
    private double amount;
    private PaymentMethod method;
    private LocalDateTime date;
    private String receiptNumber;

    public Payment() {
        this.date = LocalDateTime.now();
        this.method = PaymentMethod.CASH;
    }

    public Payment(String id, String billId, double amount, PaymentMethod method) {
        this.id = id;
        this.billId = billId;
        this.amount = amount;
        this.method = method != null ? method : PaymentMethod.CASH;
        this.date = LocalDateTime.now();
        this.receiptNumber = id;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getBillId() { return billId; }
    public void setBillId(String billId) { this.billId = billId; }
    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
    public PaymentMethod getMethod() { return method; }
    public void setMethod(PaymentMethod method) { this.method = method; }
    public LocalDateTime getDate() { return date; }
    public void setDate(LocalDateTime date) { this.date = date; }
    public String getReceiptNumber() { return receiptNumber; }
    public void setReceiptNumber(String receiptNumber) { this.receiptNumber = receiptNumber; }
}
