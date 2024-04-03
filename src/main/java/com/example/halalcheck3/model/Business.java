// Business.java
package com.example.halalcheck3.model;

public class Business {

    private String email;
    private String phone;
    private String address;
    private String password;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Business() {
        // Default constructor required for Firebase
    }

    public Business(String email, String phone, String address, String password) {
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.password = password;
    }
}
