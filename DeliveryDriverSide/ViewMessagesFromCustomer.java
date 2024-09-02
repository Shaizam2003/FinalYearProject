package com.example.halalcheck3.DeliveryDriverSide;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.example.halalcheck3.R;
import com.example.halalcheck3.adapter.MessagesAdapter;
import com.example.halalcheck3.model.ChatMessage;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ViewMessagesFromCustomer extends AppCompatActivity {

    private RecyclerView recyclerViewMessages;
    private MessagesAdapter messagesAdapter;
    private List<ChatMessage> messageList = new ArrayList<>();
    private DatabaseReference messagesRef;
    private String orderId;
    private String customerId;
    private String driverId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_messages_from_customer);

        // Initialize UI components
        recyclerViewMessages = findViewById(R.id.recyclerViewMessages);
        recyclerViewMessages.setLayoutManager(new LinearLayoutManager(this));

        // Initialize the adapter
        messagesAdapter = new MessagesAdapter(messageList);
        recyclerViewMessages.setAdapter(messagesAdapter);

        // Get orderId, customerId, and driverId from Intent
        Intent intent = getIntent();
        orderId = intent.getStringExtra("orderId");
        customerId = intent.getStringExtra("customerId");
        driverId = intent.getStringExtra("driverId");

        if (orderId != null && customerId != null && driverId != null) {
            // Reference to the messages node in Firebase
            messagesRef = FirebaseDatabase.getInstance().getReference()
                    .child("orders")
                    .child(orderId)
                    .child("messages");

            // Listen for changes to the messages
            messagesRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    messageList.clear();
                    for (DataSnapshot messageSnapshot : snapshot.getChildren()) {
                        ChatMessage message = messageSnapshot.getValue(ChatMessage.class);
                        if (message != null &&
                                (message.getSenderId().equals(customerId) && message.getReceiverId().equals(driverId))) {
                            messageList.add(message);
                        }
                    }
                    messagesAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(ViewMessagesFromCustomer.this, "Failed to load messages.", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "Order ID or Customer ID or Driver ID not found.", Toast.LENGTH_SHORT).show();
        }
    }
}
