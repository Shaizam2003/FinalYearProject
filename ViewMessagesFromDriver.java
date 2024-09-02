package com.example.halalcheck3;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.halalcheck3.adapter.MessagesAdapter;
import com.example.halalcheck3.model.ChatMessage;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class ViewMessagesFromDriver extends AppCompatActivity {

    private RecyclerView recyclerView;
    private MessagesAdapter messagesAdapter;
    private List<ChatMessage> messageList = new ArrayList<>();
    private DatabaseReference messagesRef;
    private String driverId;
    private String customerId;
    private String orderId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_messages);

        recyclerView = findViewById(R.id.recyclerViewMessages);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        messagesAdapter = new MessagesAdapter(messageList);
        recyclerView.setAdapter(messagesAdapter);

        Intent intent = getIntent();
        orderId = intent.getStringExtra("ORDER_ID");
        customerId = intent.getStringExtra("CUSTOMER_ID");
        driverId = intent.getStringExtra("DRIVER_ID");

        // Log the values for debugging
        Log.d("ViewMessagesFromDriver", "Order ID: " + orderId);
        Log.d("ViewMessagesFromDriver", "Customer ID: " + customerId);
        Log.d("ViewMessagesFromDriver", "Driver ID: " + driverId);

        if (orderId != null && customerId != null && driverId != null) {
            messagesRef = FirebaseDatabase.getInstance().getReference()
                    .child("orders")
                    .child(orderId)
                    .child("messages");

            messagesRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Log.d("ViewMessagesFromDriver", "DataSnapshot received.");
                    messageList.clear();
                    for (DataSnapshot messageSnapshot : snapshot.getChildren()) {
                        ChatMessage message = messageSnapshot.getValue(ChatMessage.class);
                        if (message != null) {
                            Log.d("ViewMessagesFromDriver", "Message: " + message.getContent());
                            if (message.getSenderId().equals(driverId) && message.getReceiverId().equals(customerId)) {
                                messageList.add(message);
                            }
                        }
                    }
                    messagesAdapter.notifyDataSetChanged();
                    Log.d("ViewMessagesFromDriver", "Messages updated. Count: " + messageList.size());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(ViewMessagesFromDriver.this, "Failed to load messages.", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "Order ID, Customer ID, or Driver ID not found.", Toast.LENGTH_SHORT).show();
        }
    }
}
