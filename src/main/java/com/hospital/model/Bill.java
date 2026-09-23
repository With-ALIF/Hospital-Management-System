package com.hospital.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.hospital.enums.PaymentStatus;
import com.hospital.repository.Payable;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Bill implements Payable {
    private String id;
    private String patientId;
    private LocalDate date;
    private List<BillItem> items;
    private double discount;
    private double tax;
    private double paidAmount;
    private PaymentStatus status;

    public Bill() {
        this.items = new ArrayList<>();
        this.status = PaymentStatus.UNPAID;
        this.date = LocalDate.now();
    }

    public Bill(String id, String patientId) {
        this();
        this.id = id;
        this.patientId = patientId;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getPatientId() { return patientId; }
    public void setPatientId(String patientId) { this.patientId = patientId; }
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    public double getDiscount() { return discount; }
    public void setDiscount(double discount) { this.discount = discount; }
    public double getTax() { return tax; }
    public void setTax(double tax) { this.tax = tax; }
    public double getPaidAmount() { return paidAmount; }
    public void setPaidAmount(double paidAmount) { this.paidAmount = paidAmount; }
    public PaymentStatus getStatus() { return status != null ? status : PaymentStatus.UNPAID; }
    public void setStatus(PaymentStatus status) { this.status = status; }

    public List<BillItem> getItems() {
        if (items == null) items = new ArrayList<>();
        return items;
    }

    public void setItems(List<BillItem> items) {
        this.items = items != null ? items : new ArrayList<>();
    }

    public void addItem(BillItem item) {
        if (item != null) getItems().add(item);
    }

    @JsonIgnore
    public double getSubtotal() {
        return getItems().stream().mapToDouble(BillItem::getAmount).sum();
    }

    @JsonIgnore
    public double getTotal() {
        double afterDiscount = getSubtotal() - discount;
        return Math.max(0, afterDiscount) + tax;
    }

    @Override
    public double getAmountDue() {
        return Math.max(0, getTotal() - paidAmount);
    }

    @Override
    public void applyPayment(double amount) {
        this.paidAmount += amount;
        if (paidAmount + 0.001 >= getTotal() && getTotal() > 0) {
            this.status = PaymentStatus.PAID;
        } else if (paidAmount > 0) {
            this.status = PaymentStatus.PARTIAL;
        }
    }
}
