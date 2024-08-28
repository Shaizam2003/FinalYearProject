package com.example.halalcheck3.model;

public class Review {

    private float rating;
    private String comment;
    private long timestamp;
    private String userEmail; // New field for user email

    // Default constructor required for calls to DataSnapshot.getValue(Review.class)
    public Review() {
    }

    public Review(float rating, String comment, long timestamp, String userEmail) {
        this.rating = rating;
        this.comment = comment;
        this.timestamp = timestamp;
        this.userEmail = userEmail; // Initialize the new field
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }
}
