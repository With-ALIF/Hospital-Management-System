package com.hospital.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.hospital.enums.StockStatus;

import java.time.LocalDate;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Medicine {
    private String id;
    private String name;
    private String category;
    private String manufacturer;
    private double price;
    private int quantity;
    private LocalDate expiryDate;
    private int minStockLevel;

    public Medicine() {
        this.minStockLevel = 10;
    }

    public Medicine(String id, String name, String category, String manufacturer,
                    double price, int quantity, LocalDate expiryDate, int minStockLevel) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.manufacturer = manufacturer;
        this.price = price;
        this.quantity = quantity;
        this.expiryDate = expiryDate;
        this.minStockLevel = minStockLevel;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getManufacturer() { return manufacturer; }
    public void setManufacturer(String manufacturer) { this.manufacturer = manufacturer; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public LocalDate getExpiryDate() { return expiryDate; }
    public void setExpiryDate(LocalDate expiryDate) { this.expiryDate = expiryDate; }
    public int getMinStockLevel() { return minStockLevel; }
    public void setMinStockLevel(int minStockLevel) { this.minStockLevel = minStockLevel; }

    public StockStatus getStockStatus() {
        if (expiryDate != null && expiryDate.isBefore(LocalDate.now())) {
            return StockStatus.EXPIRED;
        }
        if (quantity <= 0) {
            return StockStatus.OUT_OF_STOCK;
        }
        if (quantity <= minStockLevel) {
            return StockStatus.LOW_STOCK;
        }
        if (expiryDate != null && expiryDate.isBefore(LocalDate.now().plusDays(30))) {
            return StockStatus.EXPIRING_SOON;
        }
        return StockStatus.AVAILABLE;
    }

    public boolean isExpired() {
        return expiryDate != null && expiryDate.isBefore(LocalDate.now());
    }

    @Override
    public String toString() {
        return name + " (" + quantity + " in stock)";
    }
}
