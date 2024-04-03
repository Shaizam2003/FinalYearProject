package com.example.halalcheck3;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.snackbar.Snackbar;
import com.stripe.android.PaymentConfiguration;
import com.stripe.android.model.PaymentMethodCreateParams;
import com.stripe.android.view.CardInputWidget;

public class PaymentActivity extends AppCompatActivity {

    private CardInputWidget cardInputWidget;
    private Button btnProcessPayment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        // Initialize your publishable key
        PaymentConfiguration.init(getApplicationContext(), "pk_live_51OeshEKHtWE6coC9MUIWrf1IDpdQHeonWM9beKM6PTx2vk2wfOgQ8IFftfhpRgL4PtNbT9zZkEZlYk3LyV61DQeB00EJw0xGdf");

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

            // Start the OrderStatusActivity
            startActivity(new Intent(PaymentActivity.this, OrderStatusActivity.class));

            Snackbar.make(cardInputWidget, "Payment successful! Redirecting to order status...", Snackbar.LENGTH_LONG).show();
        }
    }
}
