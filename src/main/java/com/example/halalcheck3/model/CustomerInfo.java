package com.example.halalcheck3.model;

public class CustomerInfo {
        private String email;
        private String password;
        private String phoneNumber;
        private String address;

        public CustomerInfo() {
        }

        public CustomerInfo(String email, String password, String phoneNumber, String address) {
            this.email = email;
            this.password = password;
            this.phoneNumber = phoneNumber;
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

        public String getPhoneNumber() {
            return phoneNumber;
        }

        public void setPhoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }
    }


