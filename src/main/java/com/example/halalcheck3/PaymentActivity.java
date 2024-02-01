package com.example.halalcheck3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;


import com.stripe.android.PaymentConfiguration;
import com.stripe.android.model.PaymentMethodCreateParams;
import com.stripe.android.view.CardInputWidget;


import com.google.android.material.snackbar.Snackbar;

public class PaymentActivity extends AppCompatActivity {

    private CardInputWidget cardInputWidget;
    private Button btnProcessPayment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        // Initialize your publishable key
        PaymentConfiguration.init(getApplicationContext(), "your_publishable_key");

        cardInputWidget = findViewById(R.id.cardInputWidget);
        btnProcessPayment = findViewById(R.id.btnProcessPayment);

        btnProcessPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processPayment();
            }
        });
    }

    private void processPayment() {
        // Retrieve payment method details from the CardInputWidget
        PaymentMethodCreateParams params = cardInputWidget.getPaymentMethodCreateParams();

        if (params != null) {
            // Use the params for further processing
            // You can send this to your server for processing and complete the payment on the server side.
            // For demonstration purposes, we'll just show a Snackbar.
            Snackbar.make(cardInputWidget, "Payment successful!", Snackbar.LENGTH_LONG).show();
        }
    }
}
