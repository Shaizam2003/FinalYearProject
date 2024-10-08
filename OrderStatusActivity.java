package com.example.halalcheck3;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class OrderStatusActivity extends AppCompatActivity {

    private DatabaseReference orderRef;
    private FirebaseAuth mAuth;
    private String businessId;
    private String driverId;  // Added to store the driver ID

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_status);

        // Get references to the TextViews
        TextView textViewCustomerEmail = findViewById(R.id.textViewCustomerEmail);
        TextView textViewOrderItems = findViewById(R.id.textViewOrderItems);
        TextView textViewCurrentStatus = findViewById(R.id.textViewCurrentStatus);
        TextView textViewOrderReferenceNumber = findViewById(R.id.textViewOrderReferenceNumber);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Get the currently logged-in user
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            // Retrieve and display the email address of the logged-in user
            String customerEmail = currentUser.getEmail();
            textViewCustomerEmail.setText("Customer Email: " + customerEmail);
        } else {
            // Handle the case where there is no logged-in user
            Toast.makeText(this, "No user is logged in", Toast.LENGTH_SHORT).show();
        }

        // Get order ID from Intent
        String orderId = getIntent().getStringExtra("orderId");

        // Reference to the specific order
        orderRef = FirebaseDatabase.getInstance().getReference()
                .child("orders")
                .child(orderId);

        // Listen for changes in the order status and details
        orderRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Get and display the order details
                    DataSnapshot orderDetailsSnapshot = snapshot.child("orderDetails");
                    Double totalAmount = orderDetailsSnapshot.child("totalAmount").getValue(Double.class);

                    // Retrieve and display the order reference number
                    String orderReferenceNumber = orderDetailsSnapshot.child("orderReference").getValue(String.class);
                    textViewOrderReferenceNumber.setText("Order Reference Number: " + (orderReferenceNumber != null ? orderReferenceNumber : "Not Available"));

                    // Get and display the order items
                    StringBuilder orderItemsText = new StringBuilder();
                    DataSnapshot orderItemsSnapshot = snapshot.child("orderItems");
                    for (DataSnapshot itemSnapshot : orderItemsSnapshot.getChildren()) {
                        String itemName = itemSnapshot.child("itemName").getValue(String.class);
                        Double itemPrice = itemSnapshot.child("itemPrice").getValue(Double.class);
                        Long quantity = itemSnapshot.child("quantity").getValue(Long.class);
                        Double totalPrice = itemSnapshot.child("totalPrice").getValue(Double.class);

                        orderItemsText.append(itemName).append(" - €").append(itemPrice)
                                .append(" x ").append(quantity).append(" = €").append(totalPrice).append("\n");
                    }
                    textViewOrderItems.setText(orderItemsText.toString());

                    // Get and display the current status
                    String currentStatus = snapshot.child("orderStatus").child("currentStatus").getValue(String.class);
                    textViewCurrentStatus.setText("Current Status: " + (currentStatus != null ? currentStatus : "Not Available"));

                    // Extract business ID from the orderDetails node
                    businessId = orderDetailsSnapshot.child("businessId").getValue(String.class);

                    // Extract driver ID from the orderDetails node (Assuming driverId is stored here)
                    driverId = orderDetailsSnapshot.child("driverId").getValue(String.class);

                    // Set up the button to start CustomerReviewActivity
               /*     Button buttonReview = findViewById(R.id.buttonReview);
                    buttonReview.setOnClickListener(v -> {
                        if (businessId != null) {
                            Intent intent = new Intent(OrderStatusActivity.this, CustomerReview.class);
                            intent.putExtra("BUSINESS_ID", businessId);
                            startActivity(intent);
                        } else {
                            Toast.makeText(OrderStatusActivity.this, "Business ID not found.", Toast.LENGTH_SHORT).show();
                        }
                    });*/

                    // Set up the button to start Message Activity
                 /*   Button buttonChatDriver = findViewById(R.id.buttonChatDriver);
                    buttonChatDriver.setOnClickListener(v -> {
                        if (driverId != null && "outfordelivery".equalsIgnoreCase(currentStatus)) {
                            Intent intent = new Intent(OrderStatusActivity.this, SendMessageActivity.class);
                            intent.putExtra("ORDER_ID", orderId);
                            intent.putExtra("DRIVER_ID", driverId);
                            intent.putExtra("CUSTOMER_ID", currentUser.getUid());  // Pass customer ID
                            startActivity(intent);
                        } else {
                            Toast.makeText(OrderStatusActivity.this, "Driver not assigned or order not out for delivery.", Toast.LENGTH_SHORT).show();
                        }
                    });

                    // Set up the button to start ViewMessagesActivity
                    Button viewMessagesButton = findViewById(R.id.viewMessages);
                    viewMessagesButton.setOnClickListener(v -> {
                        if (driverId != null && "outfordelivery".equalsIgnoreCase(currentStatus)) {
                            Intent intent = new Intent(OrderStatusActivity.this, ViewMessagesFromDriver.class);
                            intent.putExtra("ORDER_ID", orderId);
                            intent.putExtra("DRIVER_ID", driverId);
                            intent.putExtra("CUSTOMER_ID", currentUser.getUid());  // Pass customer ID
                            startActivity(intent);
                        } else {
                            Toast.makeText(OrderStatusActivity.this, "Driver not assigned or order not out for delivery.", Toast.LENGTH_SHORT).show();
                        }
                    });*/
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(OrderStatusActivity.this, "Failed to load order status.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
