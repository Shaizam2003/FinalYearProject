package com.example.halalcheck3.DeliveryDriverSide;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import com.example.halalcheck3.R;
import com.example.halalcheck3.adapter.AssignedOrdersAdapter;
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

public class DriverSideDeliveredOrders extends AppCompatActivity {

    private RecyclerView recyclerDeliveredOrders;
    private AssignedOrdersAdapter deliveredOrdersAdapter;
    private List<Order> deliveredOrderList = new ArrayList<>();
    private DatabaseReference ordersRef;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_side_delivered_orders);

        recyclerDeliveredOrders = findViewById(R.id.recyclerDeliveredOrders);
        recyclerDeliveredOrders.setLayoutManager(new LinearLayoutManager(this));

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            // Handle the case where the user is not logged in
            Log.e("DriverSideDeliveredOrders", "User is not logged in");
            // Optionally, redirect to login screen or show an error message
            return;
        }

        String driverId = currentUser.getUid();
        deliveredOrdersAdapter = new AssignedOrdersAdapter(deliveredOrderList, this, driverId);
        recyclerDeliveredOrders.setAdapter(deliveredOrdersAdapter);

        ordersRef = FirebaseDatabase.getInstance().getReference().child("orders");
        loadDeliveredOrdersForDriver(driverId);
    }

    private void loadDeliveredOrdersForDriver(String driverId) {
        // Query for orders assigned to the driver and filter for "Delivered" status
        ordersRef.orderByChild("orderDetails/driverId").equalTo(driverId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        deliveredOrderList.clear(); // Clear the list before adding new data
                        for (DataSnapshot orderSnapshot : dataSnapshot.getChildren()) {
                            // Get the current status of the order
                            String currentStatus = orderSnapshot.child("orderStatus/currentStatus").getValue(String.class);

                            if ("Delivered".equals(currentStatus)) {
                                Order order = new Order();
                                DataSnapshot orderDetailsSnapshot = orderSnapshot.child("orderDetails");
                                if (orderDetailsSnapshot.exists()) {
                                    order.setOrderId(orderDetailsSnapshot.child("orderId").getValue(String.class));
                                    order.setBusinessId(orderDetailsSnapshot.child("businessId").getValue(String.class));
                                    order.setCustomerId(orderDetailsSnapshot.child("customerId").getValue(String.class));
                                    order.setTimestamp(orderDetailsSnapshot.child("timestamp").getValue(Long.class));
                                    order.setOrderReference(orderDetailsSnapshot.child("orderReference").getValue(String.class));
                                }

                                // Get order items
                                DataSnapshot orderItemsSnapshot = orderSnapshot.child("orderItems");
                                List<OrderItem> orderItems = new ArrayList<>();
                                for (DataSnapshot itemSnapshot : orderItemsSnapshot.getChildren()) {
                                    OrderItem orderItem = itemSnapshot.getValue(OrderItem.class);
                                    if (orderItem != null) {
                                        orderItems.add(orderItem);
                                    }
                                }
                                order.setOrderItems(orderItems);

                                // Get order status
                                DataSnapshot orderStatusSnapshot = orderSnapshot.child("orderStatus");
                                OrderStatus orderStatus = new OrderStatus();
                                if (orderStatusSnapshot.exists()) {
                                    orderStatus.setCurrentStatus(orderStatusSnapshot.child("currentStatus").getValue(String.class));
                                }
                                order.setOrderStatus(orderStatus); // Set the order status in the order object

                                // Add the fully constructed order object to the list
                                deliveredOrderList.add(order);
                            }
                        }
                        deliveredOrdersAdapter.notifyDataSetChanged(); // Notify the adapter of data changes
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.e("DriverSideDeliveredOrders", "Database error: " + databaseError.getMessage());
                    }
                });
    }
}
