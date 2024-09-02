package com.example.halalcheck3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class HomePage extends AppCompatActivity {

    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        // Retrieve the user ID passed from MainActivity
        userId = getIntent().getStringExtra("USER_ID");

        // Initialize the buttons
        Button buttonMakeOrder = findViewById(R.id.buttonMakeOrder);
        Button buttonMyOrders = findViewById(R.id.buttonMyOrders);
        Button buttonNewMessages = findViewById(R.id.buttonNewMessages);

        // Set up the click listener for the Make an Order button
        buttonMakeOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open CurrentLocationActivity and pass the user ID
                Intent intent = new Intent(HomePage.this, CurrentLocationActivity.class);
                intent.putExtra("USER_ID", userId); // Pass user ID
                startActivity(intent);
            }
        });

        // Set up the click listener for the My Orders button
        buttonMyOrders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open MyOrdersActivity and pass the user ID
                Intent intent = new Intent(HomePage.this, MyOrders.class);
                intent.putExtra("USER_ID", userId); // Pass user ID
                startActivity(intent);
            }
        });

        buttonNewMessages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the current user's ID
                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                if (currentUser == null) {
                    // Handle the case when the user is not authenticated
                    Log.e("HomePage", "User is not logged in");
                    return;
                }
                String customerId = currentUser.getUid();  // Get the current customer ID

                // Reference to the orders node
                DatabaseReference ordersRef = FirebaseDatabase.getInstance().getReference().child("orders");

                // Query the orders node to get orders where the customerId matches
                ordersRef.orderByChild("orderDetails/customerId").equalTo(customerId)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot orderSnapshot : dataSnapshot.getChildren()) {
                                    String orderId = orderSnapshot.getKey();  // Get the order ID

                                    // Reference to the messages node under the specific order ID
                                    DatabaseReference messagesRef = ordersRef.child(orderId).child("messages");

                                    // Update the read status of all messages from the driver to true
                                    messagesRef.orderByChild("read").equalTo(false)
                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot messagesSnapshot) {
                                                    for (DataSnapshot messageSnapshot : messagesSnapshot.getChildren()) {
                                                        // Update read status of the message
                                                        String messageId = messageSnapshot.getKey();  // Get the message ID
                                                        String receiverId = messageSnapshot.child("receiverId").getValue(String.class);

                                                        // Check if the message is from the driver
                                                        if (receiverId != null && receiverId.equals(customerId)) {
                                                            messagesRef.child(messageId).child("read").setValue(true)
                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                            if (task.isSuccessful()) {
                                                                                Log.d("HomePage", "Message marked as read: " + messageId);
                                                                            } else {
                                                                                Log.e("HomePage", "Failed to mark message as read: " + task.getException().getMessage());
                                                                            }
                                                                        }
                                                                    });
                                                        }
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                                    Log.e("HomePage", "Database error: " + databaseError.getMessage());
                                                }
                                            });
                                }

                                // Open NewMessagesFromDriver Activity after updating the read status
                                Intent intent = new Intent(HomePage.this, NewMessagesFromDriver.class);
                                intent.putExtra("USER_ID", customerId); // Pass the customer ID
                                startActivity(intent);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Log.e("HomePage", "Database error: " + databaseError.getMessage());
                            }
                        });
            }
        });

    }
}
