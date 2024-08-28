package com.example.halalcheck3.model;

public class DriverInfo {
    private String email;
    private String password;
    private String phone;
    private String name;
    private boolean isAvailable;

    public DriverInfo() {
        // Default constructor required for calls to DataSnapshot.getValue(DriverInfo.class)
    }

    public DriverInfo(String email, String password, String phone, String name, boolean isAvailable) {
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.name = name;
        this.isAvailable = isAvailable;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getPhone() {
        return phone;
    }

    public String getName() {
        return name;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }
}

