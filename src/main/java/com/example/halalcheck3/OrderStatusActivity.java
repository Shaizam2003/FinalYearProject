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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_status);

        // Get references to the TextViews
        TextView textViewCustomerEmail = findViewById(R.id.textViewCustomerEmail);
        TextView textViewTotalAmount = findViewById(R.id.textViewTotalAmount);
        TextView textViewOrderItems = findViewById(R.id.textViewOrderItems);
        TextView textViewPreparation = findViewById(R.id.textViewPreparation);
        TextView textViewQualityCheck = findViewById(R.id.textViewQualityCheck);
        TextView textViewOutForDelivery = findViewById(R.id.textViewOutForDelivery);
        TextView textViewDelivered = findViewById(R.id.textViewDelivered);

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

                    textViewTotalAmount.setText("Total Amount: €" + totalAmount);

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

                    // Get and display the order status
                    String preparationStatus = snapshot.child("orderStatus").child("preparationStatus").getValue(String.class);
                    String qualityCheckStatus = snapshot.child("orderStatus").child("qualityCheckStatus").getValue(String.class);
                    String outForDeliveryStatus = snapshot.child("orderStatus").child("outForDeliveryStatus").getValue(String.class);
                    String deliveredStatus = snapshot.child("orderStatus").child("deliveredStatus").getValue(String.class);

                    textViewPreparation.setText("Preparation: " + preparationStatus);
                    textViewQualityCheck.setText("Quality Check: " + qualityCheckStatus);
                    textViewOutForDelivery.setText("Out for Delivery: " + outForDeliveryStatus);
                    textViewDelivered.setText("Delivered: " + deliveredStatus);

                    // Extract business ID from the orderDetails node
                    businessId = orderDetailsSnapshot.child("businessId").getValue(String.class);

                    // Set up the button to start CustomerReviewActivity
                    Button buttonReview = findViewById(R.id.buttonReview);
                    buttonReview.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (businessId != null) {
                                Intent intent = new Intent(OrderStatusActivity.this, CustomerReview.class);
                                intent.putExtra("BUSINESS_ID", businessId);
                                startActivity(intent);
                            } else {
                                Toast.makeText(OrderStatusActivity.this, "Business ID not found.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(OrderStatusActivity.this, "Failed to load order status.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}