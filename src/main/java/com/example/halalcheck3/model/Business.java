// Business.java
package com.example.halalcheck3.model;

public class Business {

    private String address;
    private String email;
    private String password;
    private String phone;
    private String businessId; // Add business ID field

    public Business() {
        // Default constructor required for Firebase
    }

    public Business(String address, String email, String password, String phone) {
        this.address = address;
        this.email = email;
        this.password = password;
        this.phone = phone;

    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }


}
