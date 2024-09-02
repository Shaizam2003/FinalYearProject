package com.example.halalcheck3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import com.example.halalcheck3.adapter.NewMessageAdapter;
import com.example.halalcheck3.model.Business;
import com.example.halalcheck3.model.ChatMessage;
import com.example.halalcheck3.model.Order;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class NewMessagesFromDriver extends AppCompatActivity {

    private RecyclerView recyclerNewMessages;
    private NewMessageAdapter adapter;
    private List<ChatMessage> newMessages = new ArrayList<>();

    private DatabaseReference ordersRef;
    private DatabaseReference businessesRef;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_messages_from_driver);

        recyclerNewMessages = findViewById(R.id.recycler_new_messages);
        recyclerNewMessages.setLayoutManager(new LinearLayoutManager(this));

        // Initialize Firebase references
        ordersRef = FirebaseDatabase.getInstance().getReference("orders");
        businessesRef = FirebaseDatabase.getInstance().getReference("businesses");
        mAuth = FirebaseAuth.getInstance();

        // Fetch the current user ID (assumed to be the customer's ID)
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            // Handle the case when the user is not authenticated
            return;
        }

        String customerId = currentUser.getUid();
        fetchOrdersForCustomer(customerId);
    }

    private void fetchOrdersForCustomer(String customerId) {
        // Query the orders node to get orders where the customerId matches
        ordersRef.orderByChild("orderDetails/customerId").equalTo(customerId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot orderSnapshot : dataSnapshot.getChildren()) {
                            String orderId = orderSnapshot.getKey();  // Get the order ID
                            String driverId = orderSnapshot.child("orderDetails/driverId").getValue(String.class);  // Get the driver ID
                            String orderReference = orderSnapshot.child("orderDetails/orderReference").getValue(String.class);
                            String businessId = orderSnapshot.child("orderDetails/businessId").getValue(String.class);

                            // Fetch unread messages from the driver for this order
                            fetchUnreadMessagesFromDriver(orderId, driverId, orderReference, businessId);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Handle database error
                        Log.e("NewMessagesFromDriver", "Database error: " + databaseError.getMessage());
                    }
                });
    }

    private void fetchUnreadMessagesFromDriver(String orderId, String driverId, String orderReference, String businessId) {
        // Query the messages node under the specific order ID to get unread messages
        ordersRef.child(orderId).child("messages").orderByChild("read").equalTo(false)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot messageSnapshot : dataSnapshot.getChildren()) {
                            ChatMessage message = messageSnapshot.getValue(ChatMessage.class);
                            if (message != null && isMessageFromDriver(message, driverId)) {
                                newMessages.add(message);
                            }
                        }
                        // Fetch the business address and pass data to adapter
                        fetchBusinessAddress(businessId, orderReference);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Handle database error
                        Log.e("NewMessagesFromDriver", "Database error: " + databaseError.getMessage());
                    }
                });
    }

    private boolean isMessageFromDriver(ChatMessage message, String driverId) {
        // Check if the message is from the driver to the current customer
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String customerId = currentUser.getUid();
            return message.getSenderId().equals(driverId) && message.getReceiverId().equals(customerId);
        }
        return false;
    }

    private void fetchBusinessAddress(String businessId, String orderReference) {
        businessesRef.child(businessId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Business business = dataSnapshot.getValue(Business.class);
                String businessAddress = business != null ? business.getAddress() : "Address not available";

                // Set up the adapter with the fetched data
                adapter = new NewMessageAdapter(newMessages, orderReference, businessAddress);
                recyclerNewMessages.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database error
                Log.e("NewMessagesFromDriver", "Database error: " + databaseError.getMessage());
            }
        });
    }
}
