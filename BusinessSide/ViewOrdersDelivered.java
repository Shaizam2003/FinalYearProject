package com.example.halalcheck3.BusinessSide;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import com.example.halalcheck3.R;
import com.example.halalcheck3.adapter.OrderAdapter;
import com.example.halalcheck3.adapter.OrdersDeliveredAdapter;
import com.example.halalcheck3.model.Order;
import com.example.halalcheck3.model.OrderItem;
import com.example.halalcheck3.model.OrderStatus;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ViewOrdersDelivered extends AppCompatActivity {

    private RecyclerView recyclerOrdersDelivered;
    private OrdersDeliveredAdapter ordersDeliveredAdapter;
    private List<Order> deliveredOrderList = new ArrayList<>();
    private DatabaseReference ordersRef;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_orders_delivered); // Create this layout

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            // Redirect user to login screen or perform necessary authentication
            Log.e("ViewOrdersDelivered", "User is not logged in");
            return;
        }

        String businessId = currentUser.getUid();

        ordersRef = FirebaseDatabase.getInstance().getReference().child("orders");

        recyclerOrdersDelivered = findViewById(R.id.recycler_orders_delivered);
        recyclerOrdersDelivered.setLayoutManager(new LinearLayoutManager(this));
        ordersDeliveredAdapter = new OrdersDeliveredAdapter(deliveredOrderList, this); // Use OrdersDeliveredAdapter
        recyclerOrdersDelivered.setAdapter(ordersDeliveredAdapter);

        loadDeliveredOrdersForBusiness(businessId);
    }

    private void loadDeliveredOrdersForBusiness(String businessId) {
        ordersRef.orderByChild("orderDetails/businessId").equalTo(businessId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        deliveredOrderList.clear();
                        for (DataSnapshot orderSnapshot : dataSnapshot.getChildren()) {
                            // Get order status
                            DataSnapshot orderStatusSnapshot = orderSnapshot.child("orderStatus");
                            String currentStatus = orderStatusSnapshot.child("currentStatus").getValue(String.class);

                            // Only add orders that have been delivered
                            if ("Delivered".equals(currentStatus)) {
                                Order order = new Order();
                                DataSnapshot orderDetailsSnapshot = orderSnapshot.child("orderDetails");

                                if (orderDetailsSnapshot.exists()) {
                                    order.setOrderId(orderDetailsSnapshot.child("orderId").getValue(String.class));
                                    order.setBusinessId(orderDetailsSnapshot.child("businessId").getValue(String.class));
                                    order.setCustomerId(orderDetailsSnapshot.child("customerId").getValue(String.class));
                                    order.setTimestamp(orderDetailsSnapshot.child("timestamp").getValue(Long.class));
                                    order.setOrderReference(orderDetailsSnapshot.child("orderReference").getValue(String.class)); // Set the order reference
                                }

                                DataSnapshot orderItemsSnapshot = orderSnapshot.child("orderItems");
                                List<OrderItem> orderItems = new ArrayList<>();
                                for (DataSnapshot itemSnapshot : orderItemsSnapshot.getChildren()) {
                                    OrderItem orderItem = itemSnapshot.getValue(OrderItem.class);
                                    if (orderItem != null) {
                                        orderItems.add(orderItem);
                                    }
                                }
                                order.setOrderItems(orderItems);

                                if (orderStatusSnapshot.exists()) {
                                    OrderStatus orderStatus = new OrderStatus();
                                    orderStatus.setCurrentStatus(currentStatus);
                                    order.setOrderStatus(orderStatus);
                                }

                                deliveredOrderList.add(order);
                            }
                        }
                        ordersDeliveredAdapter.notifyDataSetChanged(); // Notify the adapter of data changes
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.e("ViewOrdersDelivered", "Database error: " + databaseError.getMessage());
                    }
                });
    }
}
