package com.example.halalcheck3.model;

import java.util.ArrayList;
import java.util.List;

public class Order {
    private String orderId;
    private String businessId;
    private String customerId;
    private long timestamp;
    private List<OrderItem> orderItems;
    private OrderStatus orderStatus;  // Grouped OrderStatus object
    private String driverId; // New field for driver ID
    private String driverName; // New field for driver name
    private String orderReference; // New field for the order reference number
    private String currentStatus;  // Add this field
    private boolean selected; // New field to keep track of selection

    // Default constructor required for Firebase
    public Order() {
        this.orderItems = new ArrayList<>(); // Initialize the list to prevent null pointers
        this.orderStatus = new OrderStatus(); // Initialize orderStatus with default values
    }

    // Parameterized constructor
    public Order(String orderId, String businessId, String customerId, long timestamp, List<OrderItem> orderItems,String orderReference) {
        this.orderId = orderId;
        this.businessId = businessId;
        this.customerId = customerId;
        this.timestamp = timestamp;
        this.orderItems = orderItems != null ? orderItems : new ArrayList<>(); // Ensure non-null list
        this.orderStatus = new OrderStatus(); // Initialize orderStatus with default values
        this.orderReference = orderReference; // Set the order reference
    }



    // Getter and Setter for orderId
    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    // Getter and Setter for businessId
    public String getBusinessId() {
        return businessId;
    }

    public void setBusinessId(String businessId) {
        this.businessId = businessId;
    }

    // Getter and Setter for customerId
    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    // Getter and Setter for timestamp
    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    // Getter and Setter for orderItems
    public List<OrderItem> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }

    // Method to calculate the total amount of the order
    public double getTotalAmount() {
        double total = 0;
        for (OrderItem item : orderItems) {
            total += item.getTotalPrice();
        }
        return total;
    }

    // Getter and Setter for orderStatus
    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    // Getter and Setter for driverId
    public String getDriverId() {
        return driverId;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }

    // Getter and Setter for driverName
    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    // Getter and Setter for orderReference
    public String getOrderReference() {
        return orderReference;
    }

    public void setOrderReference(String orderReference) {
        this.orderReference = orderReference;
    }

    // Getter and Setter for currentStatus
    public String getCurrentStatus() {
        return currentStatus;
    }

    public void setCurrentStatus(String currentStatus) {
        this.currentStatus = currentStatus;
    }

    // Getter and Setter for selected
    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

}
