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

    // Default constructor required for Firebase
    public Order() {
        this.orderItems = new ArrayList<>(); // Initialize the list to prevent null pointers
        this.orderStatus = new OrderStatus(); // Initialize orderStatus with default values
    }

    // Parameterized constructor
    public Order(String orderId, String businessId, String customerId, long timestamp, List<OrderItem> orderItems) {
        this.orderId = orderId;
        this.businessId = businessId;
        this.customerId = customerId;
        this.timestamp = timestamp;
        this.orderItems = orderItems != null ? orderItems : new ArrayList<>(); // Ensure non-null list
        this.orderStatus = new OrderStatus(); // Initialize orderStatus with default values
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
}
