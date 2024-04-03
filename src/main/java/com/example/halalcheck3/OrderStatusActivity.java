package com.example.halalcheck3;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;


public class OrderStatusActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_status);

        // Get references to the TextViews
        TextView textViewPreparation = findViewById(R.id.textViewPreparation);
        TextView textViewQualityCheck = findViewById(R.id.textViewQualityCheck);
        TextView textViewOutForDelivery = findViewById(R.id.textViewOutForDelivery);
        TextView textViewDelivered = findViewById(R.id.textViewDelivered);

        // Set the status of each stage
        textViewPreparation.setText("Preparation: In progress");
        textViewQualityCheck.setText("Quality Check: Pending");
        textViewOutForDelivery.setText("Out for Delivery: Pending");
        textViewDelivered.setText("Delivered: Pending");

        // Simulate order status update (you can replace this with actual data)
        simulateOrderStatusUpdate();
    }

    // Simulate order status update (for demonstration purpose)
    private void simulateOrderStatusUpdate() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Simulate preparation time (15 minutes)
                    Thread.sleep(900000); // 15 minutes in milliseconds

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            TextView textViewQualityCheck = findViewById(R.id.textViewQualityCheck);
                            textViewQualityCheck.setText("Quality Check: In progress");
                        }
                    });

                    // Simulate quality check time (5 minutes)
                    Thread.sleep(300000); // 5 minutes in milliseconds

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            TextView textViewOutForDelivery = findViewById(R.id.textViewOutForDelivery);
                            textViewOutForDelivery.setText("Out for Delivery: In progress");
                        }
                    });

                    // Simulate delivery time (10 minutes)
                    Thread.sleep(600000); // 10 minutes in milliseconds

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            TextView textViewDelivered = findViewById(R.id.textViewDelivered);
                            textViewDelivered.setText("Delivered: Yes");
                        }
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}

