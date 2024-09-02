package com.example.halalcheck3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import android.text.TextUtils;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


import com.example.halalcheck3.model.ChatMessage;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import android.widget.Toast;

public class SendMessageToDriver extends AppCompatActivity {

    private EditText messageInput;
    private Button sendButton;

    private DatabaseReference ordersRef;
    private DatabaseReference messagesRef;
    private String orderId;
    private String customerId;
    private String driverId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_message);

        // Initialize UI components
        messageInput = findViewById(R.id.messageInput);
        sendButton = findViewById(R.id.sendButton);

        // Get orderId from Intent extras
        orderId = getIntent().getStringExtra("ORDER_ID");
        customerId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Reference to the orders node to get the driver's ID
        ordersRef = FirebaseDatabase.getInstance().getReference("orders").child(orderId);

        // Fetch driver's ID from the orderDetails node
        ordersRef.child("orderDetails").child("driverId").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                driverId = dataSnapshot.getValue(String.class);

                if (driverId != null) {
                    // Driver ID successfully retrieved, set up the messages reference
                    messagesRef = ordersRef.child("messages");

                    // Set up the send button click listener
                    sendButton.setOnClickListener(v -> sendMessage());
                } else {
                    Toast.makeText(SendMessageToDriver.this, "Driver ID not found for this order", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("SendMessageToDriver", "Error fetching driver ID: " + databaseError.getMessage());
            }
        });
    }

    private void sendMessage() {
        String messageContent = messageInput.getText().toString().trim();

        // Validate message content
        if (TextUtils.isEmpty(messageContent)) {
            Toast.makeText(SendMessageToDriver.this, "Message cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        // Generate a unique message ID
        String messageId = messagesRef.push().getKey();

        // Get the current timestamp
        long timestamp = System.currentTimeMillis();

        // Create a new ChatMessage object with driverId as receiverId
        ChatMessage chatMessage = new ChatMessage(
                messageId,
                customerId,
                driverId,
                messageContent,
                timestamp
        );

        // Save the message to the database
        messagesRef.child(messageId).setValue(chatMessage)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Message sent successfully, clear the input field
                        messageInput.setText("");
                        Toast.makeText(SendMessageToDriver.this, "Message sent", Toast.LENGTH_SHORT).show();
                    } else {
                        // Handle the error
                        Toast.makeText(SendMessageToDriver.this, "Failed to send message", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
