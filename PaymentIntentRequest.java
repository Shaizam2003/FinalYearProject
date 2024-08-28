package com.example.halalcheck3;

public class PaymentIntentRequest {
    private final int amount;
    private final String currency;

    public PaymentIntentRequest(int amount, String currency) {
        this.amount = amount;
        this.currency = currency;
    }

    public int getAmount() {
        return amount;
    }

    public String getCurrency() {
        return currency;
    }
}

