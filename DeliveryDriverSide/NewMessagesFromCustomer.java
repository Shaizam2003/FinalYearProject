package com.example.halalcheck3.DeliveryDriverSide;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import com.example.halalcheck3.R;
import com.example.halalcheck3.adapter.NewMessagesFromCustomerAdapter;
import com.example.halalcheck3.model.ChatMessage;
import com.example.halalcheck3.model.CustomerInfo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class NewMessagesFromCustomer extends AppCompatActivity {

    private RecyclerView recycler_new_messages;
    private NewMessagesFromCustomerAdapter adapter;
    private List<ChatMessage> newMessages = new ArrayList<>();
    private DatabaseReference ordersRef;
    private DatabaseReference usersRef;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_messages_from_customer);

        recycler_new_messages = findViewById(R.id.recycler_new_messages);
        recycler_new_messages.setLayoutManager(new LinearLayoutManager(this));

        // Initialize Firebase references
        ordersRef = FirebaseDatabase.getInstance().getReference("orders");
        usersRef = FirebaseDatabase.getInstance().getReference("users");
        mAuth = FirebaseAuth.getInstance();

        // Fetch the current driver ID
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            // Handle the case when the user is not authenticated
            return;
        }

        String driverId = currentUser.getUid();
        fetchOrdersForDriver(driverId);
    }

    private void fetchOrdersForDriver(String driverId) {
        // Query the orders node to get orders where the driverId matches
        ordersRef.orderByChild("orderDetails/driverId").equalTo(driverId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot orderSnapshot : dataSnapshot.getChildren()) {
                            String orderId = orderSnapshot.getKey();  // Get the order ID
                            String customerId = orderSnapshot.child("orderDetails/customerId").getValue(String.class);  // Get the customer ID
                            String orderReference = orderSnapshot.child("orderDetails/orderReference").getValue(String.class);

                            // Fetch unread messages from the customer for this order
                            fetchUnreadMessagesFromCustomer(orderId, customerId, orderReference);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Handle database error
                        Log.e("NewMessagesFromCustomer", "Database error: " + databaseError.getMessage());
                    }
                });
    }

    private void fetchUnreadMessagesFromCustomer(String orderId, String customerId, String orderReference) {
        // Query the messages node under the specific order ID to get unread messages
        ordersRef.child(orderId).child("messages").orderByChild("read").equalTo(false)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot messageSnapshot : dataSnapshot.getChildren()) {
                            ChatMessage message = messageSnapshot.getValue(ChatMessage.class);
                            if (message != null && isMessageFromCustomer(message, customerId)) {
                                newMessages.add(message);
                            }
                        }
                        // Fetch the customer email and pass data to adapter
                        fetchCustomerEmail(customerId, orderReference);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Handle database error
                        Log.e("NewMessagesFromCustomer", "Database error: " + databaseError.getMessage());
                    }
                });
    }

    private boolean isMessageFromCustomer(ChatMessage message, String customerId) {
        // Check if the message is from the customer to the current driver
        return message.getSenderId().equals(customerId);
    }

    private void fetchCustomerEmail(String customerId, String orderReference) {
        usersRef.child(customerId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                CustomerInfo user = dataSnapshot.getValue(CustomerInfo.class);
                String customerEmail = user != null ? user.getEmail() : "Email not available";

                // Set up the adapter with the fetched data
                adapter = new NewMessagesFromCustomerAdapter(newMessages, orderReference, customerEmail);
                recycler_new_messages.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database error
                Log.e("NewMessagesFromCustomer", "Database error: " + databaseError.getMessage());
            }
        });
    }
}
