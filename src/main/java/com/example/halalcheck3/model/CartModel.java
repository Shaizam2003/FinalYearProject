package com.example.halalcheck3.model;

public class CartModel {

    private String key, name, image;
    private double price; // Changed from String to double
    private int quantity;
    private double totalPrice; // Changed from float to double

    public CartModel() {
        // Default constructor required for FirebaseUtil
    }

    // Getters and setters

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    // Other methods, if needed

    public Object toMap() {
        // Convert object to map, if needed
        return null;
    }
}
