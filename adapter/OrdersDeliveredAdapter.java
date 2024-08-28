package com.example.halalcheck3.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.halalcheck3.R;
import com.example.halalcheck3.model.Order;
import com.example.halalcheck3.model.OrderItem;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class OrdersDeliveredAdapter extends RecyclerView.Adapter<OrdersDeliveredAdapter.OrderViewHolder> {

    private List<Order> orderList;
    private Context context;

    public OrdersDeliveredAdapter(List<Order> orderList, Context context) {
        this.orderList = orderList;
        this.context = context;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_order_item_delivered, parent, false);
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

    class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView txtOrderDetails;
        TextView txtCurrentStatus;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            txtOrderDetails = itemView.findViewById(R.id.txtOrderDetails);
            txtCurrentStatus = itemView.findViewById(R.id.txtCurrentStatus);
        }

        public void bind(Order order) {
            // Reference to the user node in Firebase
            DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("users");

            // Fetch and display the customer email using customer ID
            usersRef.child(order.getCustomerId()).child("email").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String customerEmail = dataSnapshot.getValue(String.class);
                    StringBuilder orderDetails = new StringBuilder();

                    orderDetails.append("Order Reference: ").append(order.getOrderReference()).append("\n");

                    if (customerEmail != null) {
                        orderDetails.append("Customer Email: ").append(customerEmail).append("\n");
                    }

                    // Display total amount and items
                    orderDetails.append("Total Amount: €").append(order.getTotalAmount()).append("\n");
                    orderDetails.append("Items:\n");

                    for (OrderItem item : order.getOrderItems()) {
                        orderDetails.append(item.getItemName()).append(" - €")
                                .append(item.getItemPrice()).append(" x ")
                                .append(item.getQuantity()).append(" = €")
                                .append(item.getTotalPrice()).append("\n");
                    }

                    // Set the order details text
                    txtOrderDetails.setText(orderDetails.toString());

                    // Display the current status in a single line
                    txtCurrentStatus.setText("Current Status: " + order.getOrderStatus().getCurrentStatus());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e("OrdersDeliveredAdapter", "Error fetching customer email: " + databaseError.getMessage());
                }
            });
        }
    }
}

