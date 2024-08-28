package com.example.halalcheck3.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.halalcheck3.R;
import com.example.halalcheck3.model.Order;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class PickOrderAdapter extends RecyclerView.Adapter<PickOrderAdapter.OrderViewHolder> {

    private List<Order> orderList;
    private Context context;

    public PickOrderAdapter(List<Order> orderList, Context context) {
        this.orderList = orderList;
        this.context = context;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_pick_order_item, parent, false);
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

    public class OrderViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkBoxSelect;
        TextView txtOrderDetails;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            checkBoxSelect = itemView.findViewById(R.id.checkBoxSelect);
            txtOrderDetails = itemView.findViewById(R.id.txtOrderDetails);
        }

        public void bind(Order order) {
            checkBoxSelect.setChecked(order.isSelected());

            checkBoxSelect.setOnCheckedChangeListener((buttonView, isChecked) -> order.setSelected(isChecked));

            // Reference to the business and user nodes in Firebase
            DatabaseReference businessesRef = FirebaseDatabase.getInstance().getReference().child("businesses");
            DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("users");

            StringBuilder orderDetails = new StringBuilder();

            // Display order reference
            orderDetails.append("Order Reference: ").append(order.getOrderReference()).append("\n");

            // Fetch and display the business email using business ID
            businessesRef.child(order.getBusinessId()).child("email").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String businessEmail = dataSnapshot.getValue(String.class);

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
                                Log.d("PickOrderAdapter", "Customer email not found for ID: " + order.getCustomerId());
                            }

                            if (customerAddress != null) {
                                orderDetails.append("Customer Address: ").append(customerAddress).append("\n");
                            } else {
                                Log.d("PickOrderAdapter", "Customer address not found for ID: " + order.getCustomerId());
                            }

                            // Now that all details are fetched, display the order details
                            txtOrderDetails.setText(orderDetails.toString());
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Log.e("PickOrderAdapter", "Error fetching customer data: " + databaseError.getMessage());
                        }
                    });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e("PickOrderAdapter", "Error fetching business email: " + databaseError.getMessage());
                }
            });
        }
    }
}
