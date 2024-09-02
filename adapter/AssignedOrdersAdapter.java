package com.example.halalcheck3.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.halalcheck3.R;
import com.example.halalcheck3.DeliveryDriverSide.SendMessageToCustomer;
import com.example.halalcheck3.DeliveryDriverSide.ViewMessagesFromCustomer;
import com.example.halalcheck3.model.Order;
import com.example.halalcheck3.model.OrderItem;
import com.example.halalcheck3.model.OrderStatus;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class AssignedOrdersAdapter extends RecyclerView.Adapter<AssignedOrdersAdapter.OrderViewHolder> {

    private List<Order> orderList;
    private Context context;
    private String driverId; // Add driverId field

    public AssignedOrdersAdapter(List<Order> orderList, Context context, String driverId) {
        this.orderList = orderList;
        this.context = context;
        this.driverId = driverId; // Initialize driverId
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_assigned_order_item, parent, false);
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

        String[] statuses = {"Out for Delivery", "Delivered"};

        //String[] statuses = {"Delivered"};

        builder.setItems(statuses, (dialog, which) -> {
            String status = statuses[which];

            // Reference to the order status node in Firebase
            DatabaseReference statusRef = FirebaseDatabase.getInstance().getReference()
                    .child("orders")
                    .child(order.getOrderId())
                    .child("orderStatus");

            // Update only the currentStatus in Firebase
            statusRef.child("currentStatus").setValue(status)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // Check if status is "Delivered"
                            if ("Delivered".equals(status)) {
                                // Update driver's availability in Firebase
                                DatabaseReference driverRef = FirebaseDatabase.getInstance().getReference()
                                        .child("drivers")
                                        .child(driverId);

                                driverRef.child("available").setValue(true)
                                        .addOnCompleteListener(driverTask -> {
                                            if (driverTask.isSuccessful()) {
                                                Toast.makeText(context, "Driver availability updated", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(context, "Failed to update driver availability", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }

                            // Update local data and notify the adapter
                            order.getOrderStatus().setCurrentStatus(status);
                            notifyItemChanged(position);

                            Toast.makeText(context, "Order status updated", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "Failed to update order status", Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        builder.create().show();
    }

    class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView txtOrderDetails;
        TextView txtCurrentStatus;
        Button btnUpdateStatus;
        Button btnSendMessages;
        Button btnViewMessages;



        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            txtOrderDetails = itemView.findViewById(R.id.txtOrderDetails);
            txtCurrentStatus = itemView.findViewById(R.id.txtCurrentStatus);
            btnUpdateStatus = itemView.findViewById(R.id.btnUpdateStatus);
            btnSendMessages = itemView.findViewById(R.id.btnSendMessages);
            btnViewMessages = itemView.findViewById(R.id.btnViewMessages);
        }

        public void bind(Order order) {
            // Reference to the business and user nodes in Firebase
            DatabaseReference businessesRef = FirebaseDatabase.getInstance().getReference().child("businesses");
            DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("users");

            // Fetch and display the business email using business ID
            businessesRef.child(order.getBusinessId()).child("email").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String businessEmail = dataSnapshot.getValue(String.class);
                    StringBuilder orderDetails = new StringBuilder();

                    // Display order reference
                    orderDetails.append("Order Reference: ").append(order.getOrderReference()).append("\n");

                    if (businessEmail != null) {
                        orderDetails.append("Business Email: ").append(businessEmail).append("\n");
                    }

                    // Fetch and display the customer email and address using customer ID
                    usersRef.child(order.getCustomerId()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            String customerEmail = dataSnapshot.child("email").getValue(String.class);
                            String customerAddress = dataSnapshot.child("address").getValue(String.class);

                            if (customerEmail != null) {
                                orderDetails.append("Customer Email: ").append(customerEmail).append("\n");
                            } else {
                                Log.d("AssignedOrdersAdapter", "Customer email not found for ID: " + order.getCustomerId());
                            }

                            if (customerAddress != null) {
                                orderDetails.append("Customer Address: ").append(customerAddress).append("\n");
                            } else {
                                Log.d("AssignedOrdersAdapter", "Customer address not found for ID: " + order.getCustomerId());
                            }

                            // Now that both emails and address are fetched, display the order details
                            displayOrderDetails(orderDetails.toString(), order);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Log.e("AssignedOrdersAdapter", "Error fetching customer data: " + databaseError.getMessage());
                        }
                    });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e("AssignedOrdersAdapter", "Error fetching business email: " + databaseError.getMessage());
                }
            });
        }

        private void displayOrderDetails(String orderDetails, Order order) {
            // Append the items in the order
            StringBuilder orderDetailsBuilder = new StringBuilder(orderDetails);
            orderDetailsBuilder.append("Items:\n");

            for (OrderItem item : order.getOrderItems()) {
                orderDetailsBuilder.append(item.getItemName()).append(" - €")
                        .append(item.getItemPrice()).append(" x ")
                        .append(item.getQuantity()).append(" = €")
                        .append(item.getTotalPrice()).append("\n");
            }

            // Set the complete order details text
            txtOrderDetails.setText(orderDetailsBuilder.toString());

            // Fetch and display the current status from Firebase
            DatabaseReference statusRef = FirebaseDatabase.getInstance().getReference()
                    .child("orders")
                    .child(order.getOrderId())
                    .child("orderStatus");

            statusRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    OrderStatus orderStatus = snapshot.getValue(OrderStatus.class);
                    if (orderStatus != null) {
                        txtCurrentStatus.setText("Current Status: " + orderStatus.getCurrentStatus());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Handle possible errors
                }
            });

            // Set OnClickListener for the update status button
            btnUpdateStatus.setOnClickListener(v -> {
                int adapterPosition = getAdapterPosition();
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    showUpdateStatusDialog(order, adapterPosition);
                }
            });
            // Set OnClickListener for the send messages button
            btnSendMessages.setOnClickListener(v -> {
                Intent intent = new Intent(context, SendMessageToCustomer.class);
                intent.putExtra("orderId", order.getOrderId());
                intent.putExtra("customerId", order.getCustomerId());
                intent.putExtra("driverId", driverId);
                context.startActivity(intent);
            });

            // Set OnClickListener for the view messages button
            btnViewMessages.setOnClickListener(v -> {
                Intent intent = new Intent(context, ViewMessagesFromCustomer.class);
                intent.putExtra("orderId", order.getOrderId());
                intent.putExtra("customerId", order.getCustomerId());
                intent.putExtra("driverId", driverId);
                context.startActivity(intent);
            });


        }
    }
}
