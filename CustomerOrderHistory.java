package com.example.halalcheck3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.halalcheck3.adapter.MyOrdersAdapter;
import com.example.halalcheck3.model.Order;
import com.example.halalcheck3.model.OrderItem;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CustomerOrderHistory extends AppCompatActivity {

    private RecyclerView recyclerViewOrderHistory;
    private MyOrdersAdapter adapter;
    private List<Order> deliveredOrderList;
    private DatabaseReference ordersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_order_history);

        // Initialize RecyclerView
        recyclerViewOrderHistory = findViewById(R.id.recyclerViewOrderHistory);
        recyclerViewOrderHistory.setLayoutManager(new LinearLayoutManager(this));

        // Initialize the order list and adapter
        deliveredOrderList = new ArrayList<>();
        adapter = new MyOrdersAdapter(deliveredOrderList, this);
        recyclerViewOrderHistory.setAdapter(adapter);

        // Initialize Firebase reference
        ordersRef = FirebaseDatabase.getInstance().getReference("orders");

        // Fetch the user ID passed from HomePage
        String userId = getIntent().getStringExtra("USER_ID");

        // Load delivered orders for the customer
        if (userId != null) {
            loadDeliveredOrdersForCustomer(userId);
        } else {
            Toast.makeText(this, "User ID is missing", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadDeliveredOrdersForCustomer(String customerId) {
        ordersRef.orderByChild("orderDetails/customerId").equalTo(customerId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        deliveredOrderList.clear(); // Clear the list to avoid duplicates
                        for (DataSnapshot orderSnapshot : dataSnapshot.getChildren()) {
                            DataSnapshot orderDetailsSnapshot = orderSnapshot.child("orderDetails");

                            // Check if the orderDetails and orderStatus exist
                            if (orderDetailsSnapshot.exists() && orderSnapshot.child("orderStatus/currentStatus").exists()) {
                                String currentStatus = orderSnapshot.child("orderStatus/currentStatus").getValue(String.class);

                                // Filter orders by "Delivered" status
                                if ("Delivered".equals(currentStatus)) {
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

                                    // Add the order to the list of delivered orders
                                    deliveredOrderList.add(order);
                                }
                            }
                        }
                        adapter.notifyDataSetChanged(); // Refresh the adapter
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.e("CustomerOrderHistory", "Database error: " + databaseError.getMessage());
                        Toast.makeText(CustomerOrderHistory.this, "Failed to load orders", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
