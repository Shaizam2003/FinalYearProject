package com.example.halalcheck3.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.halalcheck3.R;
import com.example.halalcheck3.model.ChatMessage;

import java.util.List;

public class NewMessagesFromCustomerAdapter extends RecyclerView.Adapter<NewMessagesFromCustomerAdapter.ViewHolder> {

    private List<ChatMessage> messages;
    private String orderReference;
    private String customerEmail;

    public NewMessagesFromCustomerAdapter(List<ChatMessage> messages, String orderReference, String customerEmail) {
        this.messages = messages;
        this.orderReference = orderReference;
        this.customerEmail = customerEmail;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_new_message_from_customer, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ChatMessage message = messages.get(position);

        // Use StringBuilder to format the details
        StringBuilder details = new StringBuilder();
        details.append("Order Reference: ").append(orderReference).append("\n")
                .append("Customer: ").append(customerEmail).append("\n")
                .append("Message: ").append(message.getContent());

        holder.txtOrderDetails.setText(details.toString());
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtOrderDetails;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtOrderDetails = itemView.findViewById(R.id.txtOrderDetails);
        }
    }
}
