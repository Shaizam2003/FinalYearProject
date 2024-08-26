package com.example.halalcheck3.model;

public class OrderStatus {
    private String preparationStatus = "Pending";
    private String qualityCheckStatus = "Pending";
    private String outForDeliveryStatus = "Pending";
    private String deliveredStatus = "Pending";

    // Default constructor required for Firebase
    public OrderStatus() {}

    // Getters and setters for each status
    public String getPreparationStatus() {
        return preparationStatus;
    }

    public void setPreparationStatus(String preparationStatus) {
        this.preparationStatus = preparationStatus;
    }

    public String getQualityCheckStatus() {
        return qualityCheckStatus;
    }

    public void setQualityCheckStatus(String qualityCheckStatus) {
        this.qualityCheckStatus = qualityCheckStatus;
    }

    public String getOutForDeliveryStatus() {
        return outForDeliveryStatus;
    }

    public void setOutForDeliveryStatus(String outForDeliveryStatus) {
        this.outForDeliveryStatus = outForDeliveryStatus;
    }

    public String getDeliveredStatus() {
        return deliveredStatus;
    }

    public void setDeliveredStatus(String deliveredStatus) {
        this.deliveredStatus = deliveredStatus;
    }

    // Method to update a status based on the index
    public void updateStatus(int index, String status) {
        switch (index) {
            case 0:
                setPreparationStatus(status);
                break;
            case 1:
                setQualityCheckStatus(status);
                break;
            case 2:
                setOutForDeliveryStatus(status);
                break;
            case 3:
                setDeliveredStatus(status);
                break;
            default:
                throw new IllegalArgumentException("Invalid status index");
        }
    }

    // Check if a specific status is "In progress"
    public boolean isInProgress(String status) {
        return "In progress".equals(status);
    }

    // Check if a specific status is "Delivered"
    public boolean isDelivered(String status) {
        return "Delivered".equals(status);
    }

    // Convenience methods to get current status
    public String getCurrentStatus() {
        if (isDelivered(getDeliveredStatus())) {
            return "Delivered";
        } else if (isInProgress(getOutForDeliveryStatus())) {
            return "Out for Delivery";
        } else if (isInProgress(getQualityCheckStatus())) {
            return "Quality Check";
        } else if (isInProgress(getPreparationStatus())) {
            return "Preparation";
        } else {
            return "Pending";
        }
    }
}
