package com.example.openfashion;

public class CartItem {
    private String id; // Unique identifier for the cart item
    private String productName;
    private double price;
    private String userEmail;
    private String userUid;

    public CartItem() {
        // Default constructor required for Firebase
    }

    public CartItem(String id, String productName, double price, String userEmail, String userUid) {
        this.id = id;
        this.productName = productName;
        this.price = price;
        this.userEmail = userEmail;
        this.userUid = userUid;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserUid() {
        return userUid;
    }

    public void setUserUid(String userUid) {
        this.userUid = userUid;
    }
}

