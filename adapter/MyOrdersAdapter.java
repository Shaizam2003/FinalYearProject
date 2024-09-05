package com.example.halalcheck3.adapter;



import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.halalcheck3.CustomerReview;
import com.example.halalcheck3.R;
import com.example.halalcheck3.SendMessageToDriver;
import com.example.halalcheck3.ViewMessagesFromDriver;
import com.example.halalcheck3.model.Order;
import com.example.halalcheck3.model.OrderItem;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class MyOrdersAdapter extends RecyclerView.Adapter<MyOrdersAdapter.OrderViewHolder> {

    private List<Order> orderList;
    private Context context;

    public MyOrdersAdapter(List<Order> orderList, Context context) {
        this.orderList = orderList;
        this.context = context;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_order_item, parent, false);
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
        LinearLayout llOptions;
        Button btnSendMessage;
        Button btnViewMessages;
        Button btnReviewRestaurant;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            txtOrderDetails = itemView.findViewById(R.id.txtOrderDetails);
            llOptions = itemView.findViewById(R.id.llOptions);
            btnSendMessage = itemView.findViewById(R.id.btnSendMessage);
            btnViewMessages = itemView.findViewById(R.id.btnViewMessages);
            btnReviewRestaurant = itemView.findViewById(R.id.btnReviewRestaurant);
        }

        public void bind(Order order) {
            String orderId = order.getOrderId();
            String businessId = order.getBusinessId();
            String currentStatus = order.getCurrentStatus();
            String orderReference = order.getOrderReference();

            // Reference to Firebase database for the order
            DatabaseReference ordersRef = FirebaseDatabase.getInstance().getReference().child("orders").child(orderId);

            // Fetch order details
            ordersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        DataSnapshot orderDetailsSnapshot = dataSnapshot.child("orderDetails");
                        String customerId = orderDetailsSnapshot.child("customerId").getValue(String.class);
                        String driverId = orderDetailsSnapshot.child("driverId").getValue(String.class);

                        // Reference to Firebase database for the business
                        DatabaseReference businessRef = FirebaseDatabase.getInstance().getReference().child("businesses").child(businessId);

                        // Fetch business details, specifically the address
                        businessRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot businessSnapshot) {
                                if (businessSnapshot.exists()) {
                                    // Get business address
                                    String businessAddress = businessSnapshot.child("address").getValue(String.class);

                                    // Build order details string
                                    StringBuilder orderDetails = new StringBuilder();
                                    orderDetails.append("Order Reference: ").append(orderReference).append("\n");
                                    orderDetails.append("Business Name: ").append(businessAddress).append("\n"); // Add business address

                                    // Append order items
                                    orderDetails.append("Items:\n");
                                    for (OrderItem item : order.getOrderItems()) {
                                        orderDetails.append(item.getItemName()).append(" - €")
                                                .append(item.getItemPrice()).append(" x ")
                                                .append(item.getQuantity()).append(" = €")
                                                .append(item.getTotalPrice()).append("\n");
                                    }

                                    // Append current status
                                    orderDetails.append("Current Status: ").append(currentStatus).append("\n");

                                    // Set the formatted details to the TextView
                                    txtOrderDetails.setText(orderDetails.toString());

                                    // Handle visibility of buttons based on order status
                                    if ("Out for Delivery".equals(currentStatus)) {
                                        llOptions.setVisibility(View.VISIBLE);
                                        btnSendMessage.setOnClickListener(v -> {
                                            Intent sendMessageIntent = new Intent(context, SendMessageToDriver.class);
                                            sendMessageIntent.putExtra("ORDER_ID", orderId);
                                            sendMessageIntent.putExtra("DRIVER_ID", driverId);
                                            context.startActivity(sendMessageIntent);
                                        });

                                        btnViewMessages.setOnClickListener(v -> {
                                            Intent viewMessagesIntent = new Intent(context, ViewMessagesFromDriver.class);
                                            viewMessagesIntent.putExtra("ORDER_ID", orderId);
                                            viewMessagesIntent.putExtra("DRIVER_ID", driverId);
                                            viewMessagesIntent.putExtra("CUSTOMER_ID", customerId);
                                            context.startActivity(viewMessagesIntent);
                                        });
                                    } else {
                                        llOptions.setVisibility(View.GONE);
                                    }

                                    // Show or hide the review restaurant button based on status
                                    if ("Delivered".equals(currentStatus)) {
                                        btnReviewRestaurant.setVisibility(View.VISIBLE);
                                        btnReviewRestaurant.setOnClickListener(v -> {
                                            Intent reviewRestaurantIntent = new Intent(context, CustomerReview.class);
                                            reviewRestaurantIntent.putExtra("BUSINESS_ID", businessId);
                                            context.startActivity(reviewRestaurantIntent);
                                        });
                                    } else {
                                        btnReviewRestaurant.setVisibility(View.GONE);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Log.e("OrderAdapter", "Error fetching business details: " + databaseError.getMessage());
                            }
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e("OrderAdapter", "Error fetching order details: " + databaseError.getMessage());
                }
            });
        }
    }
}