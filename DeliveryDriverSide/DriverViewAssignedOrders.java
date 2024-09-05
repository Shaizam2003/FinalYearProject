package com.example.halalcheck3.DeliveryDriverSide;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import com.example.halalcheck3.R;
import com.example.halalcheck3.adapter.AssignedOrdersAdapter;
import com.example.halalcheck3.adapter.OrderAdapter;
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

public class DriverViewAssignedOrders extends AppCompatActivity {

    private RecyclerView recyclerOrders;
    private AssignedOrdersAdapter assignedOrdersAdapter;
    private List<Order> orderList = new ArrayList<>();
    private DatabaseReference ordersRef;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_view_assigned_orders);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            // Redirect user to login screen or perform necessary authentication
            Log.e("DriverViewAssignedOrders", "User is not logged in");
            return;
        }

        String driverId = currentUser.getUid();

        ordersRef = FirebaseDatabase.getInstance().getReference().child("orders");

        recyclerOrders = findViewById(R.id.recycler_assigned_orders);
        recyclerOrders.setLayoutManager(new LinearLayoutManager(this));
        assignedOrdersAdapter = new AssignedOrdersAdapter(orderList, this, driverId);
        recyclerOrders.setAdapter(assignedOrdersAdapter);

        loadOrdersForDriver(driverId);
    }

    private void loadOrdersForDriver(String driverId) {
        ordersRef.orderByChild("orderDetails/driverId").equalTo(driverId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        orderList.clear();
                        for (DataSnapshot orderSnapshot : dataSnapshot.getChildren()) {
                            // Get order details
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

                            // Check if the status is not "Delivered"
                            if (!"Delivered".equals(orderStatus.getCurrentStatus())) {
                                // Add the fully constructed order object to the list
                                orderList.add(order);
                            }
                        }
                        assignedOrdersAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.e("DriverViewAssignedOrders", "Database error: " + databaseError.getMessage());
                    }
                });
    }

}

