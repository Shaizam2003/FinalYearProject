package com.example.halalcheck3.model;

public class OrderStatus {
    private String currentStatus = "Preparation";

    // Default constructor required for Firebase
    public OrderStatus() {}

    // Getter and Setter for currentStatus
    public String getCurrentStatus() {
        return currentStatus;
    }

    public void setCurrentStatus(String currentStatus) {
        this.currentStatus = currentStatus;
    }
}
