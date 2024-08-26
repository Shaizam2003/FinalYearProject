package com.example.halalcheck3.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.halalcheck3.R;
import com.example.halalcheck3.model.Order;
import com.example.halalcheck3.model.OrderItem;
import com.example.halalcheck3.model.OrderStatus;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private List<Order> orderList;
    private Context context;

    public OrderAdapter(List<Order> orderList, Context context) {
        this.orderList = orderList;
        this.context = context;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_order_item, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orderList.get(position);
        holder.bind(order);
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    private void showUpdateStatusDialog(Order order, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Update Order Status");

        String[] statuses = {"Preparation", "Quality Check", "Out for Delivery", "Delivered"};
        String[] statusUpdates = {"In progress", "In progress", "In progress", "Delivered"};

        builder.setItems(statuses, (dialog, which) -> {
            String status = statusUpdates[which];

            // Reference to the order status node in Firebase
            DatabaseReference statusRef = FirebaseDatabase.getInstance().getReference()
                    .child("orders")
                    .child(order.getOrderId())
                    .child("orderStatus");

            switch (which) {
                case 0:
                    statusRef.child("preparationStatus").setValue(status);
                    break;
                case 1:
                    statusRef.child("qualityCheckStatus").setValue(status);
                    break;
                case 2:
                    statusRef.child("outForDeliveryStatus").setValue(status);
                    break;
                case 3:
                    statusRef.child("deliveredStatus").setValue(status);
                    break;
            }

            // Update local data and notify the adapter
            order.getOrderStatus().updateStatus(which, status);
            notifyItemChanged(position);

            Toast.makeText(context, "Order status updated", Toast.LENGTH_SHORT).show();
        });

        builder.create().show();
    }

    class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView txtOrderDetails;
        TextView txtPreparationStatus;
        TextView txtQualityCheckStatus;
        TextView txtOutForDeliveryStatus;
        TextView txtDeliveredStatus;
        Button btnUpdateStatus;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            txtOrderDetails = itemView.findViewById(R.id.txtOrderDetails);
            txtPreparationStatus = itemView.findViewById(R.id.txtPreparationStatus);
            txtQualityCheckStatus = itemView.findViewById(R.id.txtQualityCheckStatus);
            txtOutForDeliveryStatus = itemView.findViewById(R.id.txtOutForDeliveryStatus);
            txtDeliveredStatus = itemView.findViewById(R.id.txtDeliveredStatus);
            btnUpdateStatus = itemView.findViewById(R.id.btnUpdateStatus);
        }

        public void bind(Order order) {
            StringBuilder orderDetails = new StringBuilder();
            orderDetails.append("Customer ID: ").append(order.getCustomerId()).append("\n")
                    .append("Total Amount: €").append(order.getTotalAmount()).append("\n")
                    .append("Items:\n");

            for (OrderItem item : order.getOrderItems()) {
                orderDetails.append(item.getItemName()).append(" - €")
                        .append(item.getItemPrice()).append(" x ")
                        .append(item.getQuantity()).append(" = €")
                        .append(item.getTotalPrice()).append("\n");
            }

            txtOrderDetails.setText(orderDetails.toString());

            // Fetch and display the current status
            DatabaseReference statusRef = FirebaseDatabase.getInstance().getReference()
                    .child("orders")
                    .child(order.getOrderId())
                    .child("orderStatus");

            statusRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    OrderStatus orderStatus = snapshot.getValue(OrderStatus.class);
                    if (orderStatus != null) {
                        txtPreparationStatus.setText("Preparation Status: " + orderStatus.getPreparationStatus());
                        txtQualityCheckStatus.setText("Quality Check Status: " + orderStatus.getQualityCheckStatus());
                        txtOutForDeliveryStatus.setText("Out for Delivery Status: " + orderStatus.getOutForDeliveryStatus());
                        txtDeliveredStatus.setText("Delivered Status: " + orderStatus.getDeliveredStatus());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Handle possible errors
                }
            });

            // Set OnClickListener for the update status button
            btnUpdateStatus.setOnClickListener(v -> {
                int adapterPosition = getAdapterPosition();
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    showUpdateStatusDialog(order, adapterPosition);
                }
            });
        }
    }
}
