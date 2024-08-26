package com.example.halalcheck3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

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

public class ViewOrders extends AppCompatActivity {

        private RecyclerView recyclerOrders;
        private OrderAdapter orderAdapter;
        private List<Order> orderList = new ArrayList<>();
        private DatabaseReference ordersRef;
        private FirebaseAuth mAuth;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.activity_view_orders);

                mAuth = FirebaseAuth.getInstance();
                FirebaseUser currentUser = mAuth.getCurrentUser();
                if (currentUser == null) {
                        // Redirect user to login screen or perform necessary authentication
                        Log.e("ViewOrders", "User is not logged in");
                        return;
                }

                String businessId = currentUser.getUid();

                ordersRef = FirebaseDatabase.getInstance().getReference().child("orders");

                recyclerOrders = findViewById(R.id.recycler_orders);
                recyclerOrders.setLayoutManager(new LinearLayoutManager(this));
                orderAdapter = new OrderAdapter(orderList, this);
                recyclerOrders.setAdapter(orderAdapter);

                loadOrdersForBusiness(businessId);
        }

        private void loadOrdersForBusiness(String businessId) {
                ordersRef.orderByChild("orderDetails/businessId").equalTo(businessId)
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
                                                        orderStatus.setPreparationStatus(orderStatusSnapshot.child("preparationStatus").getValue(String.class));
                                                        orderStatus.setQualityCheckStatus(orderStatusSnapshot.child("qualityCheckStatus").getValue(String.class));
                                                        orderStatus.setOutForDeliveryStatus(orderStatusSnapshot.child("outForDeliveryStatus").getValue(String.class));
                                                        orderStatus.setDeliveredStatus(orderStatusSnapshot.child("deliveredStatus").getValue(String.class));
                                                }
                                                order.setOrderStatus(orderStatus); // Set the order status in the order object

                                                // Add the fully constructed order object to the list
                                                orderList.add(order);
                                        }
                                        orderAdapter.notifyDataSetChanged();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                        Log.e("ViewOrders", "Database error: " + databaseError.getMessage());
                                }
                        });
        }
}
