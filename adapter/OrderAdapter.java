package com.example.halalcheck3.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.halalcheck3.R;
import com.example.halalcheck3.listener.OrderStatusListener;
import com.example.halalcheck3.model.Order;
import com.example.halalcheck3.model.OrderItem;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.MediaType;
import okhttp3.Call;
import okhttp3.Callback;

import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {
    private List<Order> orderList;
    private Context context;

    public OrderAdapter(List<Order> orderList, Context context) {
        this.orderList = orderList;
        this.context = context;
        // Initialize OrderStatusListener to listen for changes
        new OrderStatusListener(context);
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

        // Status options
      //  String[] statuses = {"Preparation", "Quality Check", "Out for Delivery"};

        // Status options
        String[] statuses = {"Ready for Delivery"};

        builder.setItems(statuses, (dialog, which) -> {
            String selectedStatus = statuses[which];

            DatabaseReference statusRef = FirebaseDatabase.getInstance().getReference()
                    .child("orders")
                    .child(order.getOrderId())
                    .child("orderStatus");

            statusRef.child("currentStatus").setValue(selectedStatus).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    order.getOrderStatus().setCurrentStatus(selectedStatus);
                    notifyItemChanged(position);
                } else {
                    Log.e("OrderAdapter", "Failed to update status");
                }
            });
        });

        builder.create().show();
    }

    class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView txtOrderDetails;
        TextView txtCurrentStatus;
        Button btnUpdateStatus;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            txtOrderDetails = itemView.findViewById(R.id.txtOrderDetails);
            txtCurrentStatus = itemView.findViewById(R.id.txtCurrentStatus);
            btnUpdateStatus = itemView.findViewById(R.id.btnUpdateStatus);
        }

        public void bind(Order order) {
            DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("users");
            usersRef.child(order.getCustomerId()).child("email").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String customerEmail = dataSnapshot.getValue(String.class);
                    StringBuilder orderDetails = new StringBuilder();

                    orderDetails.append("Order Reference: ").append(order.getOrderReference()).append("\n");

                    if (customerEmail != null) {
                        orderDetails.append("Customer Email: ").append(customerEmail).append("\n");
                    }

                    orderDetails.append("Total Amount: €").append(order.getTotalAmount()).append("\n");
                    orderDetails.append("Items:\n");

                    for (OrderItem item : order.getOrderItems()) {
                        orderDetails.append(item.getItemName()).append(" - €")
                                .append(item.getItemPrice()).append(" x ")
                                .append(item.getQuantity()).append(" = €")
                                .append(item.getTotalPrice()).append("\n");
                    }

                    txtOrderDetails.setText(orderDetails.toString());
                    txtCurrentStatus.setText("Current Status: " + order.getOrderStatus().getCurrentStatus());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e("OrderAdapter", "Error fetching customer email: " + databaseError.getMessage());
                }
            });

            btnUpdateStatus.setOnClickListener(v -> {
                int adapterPosition = getAdapterPosition();
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    showUpdateStatusDialog(order, adapterPosition);
                }
            });
        }
    }
}
