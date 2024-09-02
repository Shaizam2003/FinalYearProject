package com.example.halalcheck3.DeliveryDriverSide;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.halalcheck3.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DriverHomePage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drive_home_page);

        Button pickOrders = findViewById(R.id.pickOrders);
        Button viewAssignedOrders = findViewById(R.id.viewAssignedOrders);
        Button viewNewMessages = findViewById(R.id.viewNewMessages);

        pickOrders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open RestaurantMenuActivity
                Intent intent = new Intent(DriverHomePage.this, PickOrders.class);
                startActivity(intent);
            }
        });

        viewAssignedOrders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open RestaurantMenuActivity
                Intent intent = new Intent(DriverHomePage.this, DriverViewAssignedOrders.class);
                startActivity(intent);
            }
        });

        viewNewMessages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the current user's driver ID
                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                if (currentUser == null) {
                    // Handle the case when the user is not authenticated
                    Log.e("DriverHomePage", "User is not logged in");
                    return;
                }
                String driverId = currentUser.getUid();

                // Reference to the orders node
                DatabaseReference ordersRef = FirebaseDatabase.getInstance().getReference().child("orders");

                // Query the orders node to get orders where the driverId matches
                ordersRef.orderByChild("orderDetails/driverId").equalTo(driverId)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot orderSnapshot : dataSnapshot.getChildren()) {
                                    String orderId = orderSnapshot.getKey();  // Get the order ID

                                    // Reference to the messages node under the specific order ID
                                    DatabaseReference messagesRef = ordersRef.child(orderId).child("messages");

                                    // Update the read status of all messages to true
                                    messagesRef.orderByChild("read").equalTo(false)
                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot messagesSnapshot) {
                                                    for (DataSnapshot messageSnapshot : messagesSnapshot.getChildren()) {
                                                        String messageId = messageSnapshot.getKey();  // Get the message ID
                                                        messagesRef.child(messageId).child("read").setValue(true)
                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        if (task.isSuccessful()) {
                                                                            Log.d("DriverHomePage", "Message marked as read: " + messageId);
                                                                        } else {
                                                                            Log.e("DriverHomePage", "Failed to mark message as read: " + task.getException().getMessage());
                                                                        }
                                                                    }
                                                                });
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                                    Log.e("DriverHomePage", "Database error: " + databaseError.getMessage());
                                                }
                                            });
                                }

                                // Open NewMessagesFromCustomer Activity after updating the read status
                                Intent intent = new Intent(DriverHomePage.this, NewMessagesFromCustomer.class);
                                startActivity(intent);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Log.e("DriverHomePage", "Database error: " + databaseError.getMessage());
                            }
                        });
            }
        });



    }
}