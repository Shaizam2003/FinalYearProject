package com.example.halalcheck3.DeliveryDriverSide;

import androidx.appcompat.app.AppCompatActivity;


import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.text.TextUtils;

import com.example.halalcheck3.R;
import com.example.halalcheck3.model.ChatMessage;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;



public class SendMessageToCustomer extends AppCompatActivity {

    private EditText messageInput;
    private Button sendButton;

    private DatabaseReference messagesRef;
    private String orderId;
    private String customerId;
    private String driverId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_message_to_customer); // Use the same layout file

        // Initialize UI components
        messageInput = findViewById(R.id.messageInput);
        sendButton = findViewById(R.id.sendButton);

        // Get data from Intent extras
        orderId = getIntent().getStringExtra("orderId");
        customerId = getIntent().getStringExtra("customerId");
        driverId = getIntent().getStringExtra("driverId");

        // Reference to the messages node for the specific order ID
        messagesRef = FirebaseDatabase.getInstance().getReference("orders").child(orderId).child("messages");

        // Set up the send button click listener
        sendButton.setOnClickListener(v -> sendMessage());
    }

    private void sendMessage() {
        String messageContent = messageInput.getText().toString().trim();

        // Validate message content
        if (TextUtils.isEmpty(messageContent)) {
            Toast.makeText(SendMessageToCustomer.this, "Message cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        // Generate a unique message ID
        String messageId = messagesRef.push().getKey();

        // Get the current timestamp
        long timestamp = System.currentTimeMillis();

        // Create a new ChatMessage object with customerId as receiverId
        ChatMessage chatMessage = new ChatMessage(
                messageId,
                driverId,
                customerId,
                messageContent,
                timestamp
        );

        // Save the message to the database
        messagesRef.child(messageId).setValue(chatMessage)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Message sent successfully, clear the input field
                        messageInput.setText("");
                        Toast.makeText(SendMessageToCustomer.this, "Message sent", Toast.LENGTH_SHORT).show();
                    } else {
                        // Handle the error
                        Toast.makeText(SendMessageToCustomer.this, "Failed to send message", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
