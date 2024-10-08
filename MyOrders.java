package com.example.halalcheck3;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;


import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.halalcheck3.adapter.MyOrdersAdapter;
import com.example.halalcheck3.model.Order;
import com.example.halalcheck3.model.OrderItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class MyOrders extends AppCompatActivity {

    private RecyclerView recyclerView;
    private MyOrdersAdapter adapter;
    private List<Order> orderList;
    private DatabaseReference ordersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_orders);

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.recyclerViewOrders);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize the order list and adapter
        orderList = new ArrayList<>();
        adapter = new MyOrdersAdapter(orderList, this);
        recyclerView.setAdapter(adapter);

        // Initialize Firebase reference
        ordersRef = FirebaseDatabase.getInstance().getReference("orders");

        // Fetch the user ID passed from HomePage
        String userId = getIntent().getStringExtra("USER_ID");

        // Load orders for the customer with status not equal to "Delivered"
        if (userId != null) {
            loadOrdersForCustomer(userId);
        } else {
            Toast.makeText(this, "User ID is missing", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadOrdersForCustomer(String customerId) {
        ordersRef.orderByChild("orderDetails/customerId").equalTo(customerId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        orderList.clear(); // Clear the list to avoid duplicates
                        for (DataSnapshot orderSnapshot : dataSnapshot.getChildren()) {
                            DataSnapshot orderDetailsSnapshot = orderSnapshot.child("orderDetails");

                            // Check if the orderDetails and orderStatus exist
                            if (orderDetailsSnapshot.exists() && orderSnapshot.child("orderStatus/currentStatus").exists()) {
                                String currentStatus = orderSnapshot.child("orderStatus/currentStatus").getValue(String.class);

                                // Filter orders where status is not "Delivered"
                                if (!"Delivered".equals(currentStatus)) {
                                    Order order = new Order();
                                    order.setOrderId(orderDetailsSnapshot.child("orderId").getValue(String.class));
                                    order.setBusinessId(orderDetailsSnapshot.child("businessId").getValue(String.class));
                                    order.setTimestamp(orderDetailsSnapshot.child("timestamp").getValue(Long.class));
                                    order.setOrderReference(orderDetailsSnapshot.child("orderReference").getValue(String.class));
                                    order.setCurrentStatus(currentStatus);

                                    // Retrieve order items
                                    DataSnapshot orderItemsSnapshot = orderSnapshot.child("orderItems");
                                    List<OrderItem> orderItems = new ArrayList<>();
                                    for (DataSnapshot itemSnapshot : orderItemsSnapshot.getChildren()) {
                                        String itemName = itemSnapshot.child("itemName").getValue(String.class);
                                        Double itemPrice = itemSnapshot.child("itemPrice").getValue(Double.class);
                                        Integer quantity = itemSnapshot.child("quantity").getValue(Integer.class);
                                        Double totalPrice = itemSnapshot.child("totalPrice").getValue(Double.class);

                                        OrderItem item = new OrderItem(itemName, itemPrice, quantity, totalPrice);
                                        orderItems.add(item);
                                    }
                                    order.setOrderItems(orderItems);

                                    // Add the order to the list of orders
                                    orderList.add(order);
                                }
                            }
                        }
                        adapter.notifyDataSetChanged(); // Refresh the adapter
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.e("MyOrders", "Database error: " + databaseError.getMessage());
                        Toast.makeText(MyOrders.this, "Failed to load orders", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
