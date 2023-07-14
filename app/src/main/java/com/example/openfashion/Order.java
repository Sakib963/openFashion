package com.example.openfashion;

import java.util.List;

public class Order {
    private String serialNumber;
    private List<String> productNames;
    private double totalPrice;

    public Order(String serialNumber, List<String> productNames, double totalPrice) {
        this.serialNumber = serialNumber;
        this.productNames = productNames;
        this.totalPrice = totalPrice;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public List<String> getProductNames() {
        return productNames;
    }

    public double getTotalPrice() {
        return totalPrice;
    }
}
