package com.example.halalcheck3.DeliveryDriverSide;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.example.halalcheck3.R;
import com.example.halalcheck3.adapter.PickOrderAdapter;
import com.example.halalcheck3.model.Order;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class PickOrders extends AppCompatActivity {

    private RecyclerView recyclerViewOrders;
    private PickOrderAdapter pickOrderAdapter;
    private List<Order> orderList = new ArrayList<>();
    private DatabaseReference ordersRef;
    private FirebaseAuth mAuth;
    private String driverId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_orders);

        recyclerViewOrders = findViewById(R.id.recyclerViewOrders);
        recyclerViewOrders.setLayoutManager(new LinearLayoutManager(this));
        pickOrderAdapter = new PickOrderAdapter(orderList, this);
        recyclerViewOrders.setAdapter(pickOrderAdapter);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        driverId = currentUser.getUid();

        ordersRef = FirebaseDatabase.getInstance().getReference().child("orders");
        loadOrders();

        Button buttonFinish = findViewById(R.id.buttonFinish);
        buttonFinish.setOnClickListener(v -> finishSelection());
    }

    private void loadOrders() {
        ordersRef.orderByChild("orderDetails/driverId").equalTo("")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        orderList.clear();
                        for (DataSnapshot orderSnapshot : dataSnapshot.getChildren()) {
                            Order order = new Order();
                            DataSnapshot orderDetailsSnapshot = orderSnapshot.child("orderDetails");
                            if (orderDetailsSnapshot.exists()) {
                                order.setOrderId(orderSnapshot.getKey());  // Set the order ID
                                order.setBusinessId(orderDetailsSnapshot.child("businessId").getValue(String.class));
                                order.setCustomerId(orderDetailsSnapshot.child("customerId").getValue(String.class));
                                order.setTimestamp(orderDetailsSnapshot.child("timestamp").getValue(Long.class));
                                order.setOrderReference(orderDetailsSnapshot.child("orderReference").getValue(String.class)); // Set the order reference
                                orderList.add(order);  // Add order to the list
                            }
                        }
                        pickOrderAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.e("PickOrders", "Database error: " + databaseError.getMessage());
                    }
                });
    }

    private void finishSelection() {
        boolean ordersAssigned = false;

        for (Order order : orderList) {
            if (order.isSelected()) {
                ordersAssigned = true; // At least one order is selected for assignment
                ordersRef.child(order.getOrderId()).child("orderDetails").child("driverId").setValue(driverId)
                        .addOnCompleteListener(task -> {
                            if (!task.isSuccessful()) {
                                Toast.makeText(this, "Failed to assign order", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        }

        if (ordersAssigned) {
            // Set driver availability to false in the drivers node
            DatabaseReference driverRef = FirebaseDatabase.getInstance().getReference().child("drivers").child(driverId);
            driverRef.child("available").setValue(false)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(this, "Orders assigned and driver set to unavailable", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this, "Failed to update driver availability", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(this, "No orders selected for assignment", Toast.LENGTH_SHORT).show();
        }

        loadOrders(); // Refresh the list
    }
}
