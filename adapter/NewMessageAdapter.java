package com.example.halalcheck3.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.halalcheck3.R;
import com.example.halalcheck3.model.ChatMessage;

import java.util.List;

public class NewMessageAdapter extends RecyclerView.Adapter<NewMessageAdapter.MessageViewHolder> {

    private List<ChatMessage> messages;
    private String orderReference;
    private String businessAddress;

    public NewMessageAdapter(List<ChatMessage> messages, String orderReference, String businessAddress) {
        this.messages = messages;
        this.orderReference = orderReference;
        this.businessAddress = businessAddress;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_new_message, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        ChatMessage message = messages.get(position);

        // Create a StringBuilder to format the message details
        StringBuilder details = new StringBuilder();
        details.append("Order Reference: ").append(orderReference).append("\n");
        details.append("Business Name: ").append(businessAddress).append("\n");
        details.append("Message From Driver: ").append(message.getContent());

        holder.txtOrderDetails.setText(details.toString());
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView txtOrderDetails;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            txtOrderDetails = itemView.findViewById(R.id.txtOrderDetails);
        }
    }
}
