package com.example.halalcheck3.model;



public class ChatMessage {
    private String messageId;
    private String senderId;
    private String receiverId;
    private String content;
    private long timestamp;
    private boolean read = false; // Default value set to false

    // Default constructor required for calls to DataSnapshot.getValue(ChatMessage.class)
    public ChatMessage() {
    }

    // Constructor with parameters
    public ChatMessage(String messageId, String senderId, String receiverId, String content, long timestamp) {
        this.messageId = messageId;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.content = content;
        this.timestamp = timestamp;
        this.read = false; // Ensure 'read' is set to false by default
    }

    // Getters
    public String getMessageId() {
        return messageId;
    }

    public String getSenderId() {
        return senderId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public String getContent() {
        return content;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public boolean isRead() {
        return read;
    }

    // Setter for read
    public void setRead(boolean read) {
        this.read = read;
    }
}
