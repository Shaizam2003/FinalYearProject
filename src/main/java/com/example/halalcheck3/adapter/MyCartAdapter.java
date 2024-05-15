package com.example.halalcheck3.adapter;

// MyCartAdapter.java
import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

//import com.bumptech.glide.Glide;
import com.example.halalcheck3.R;
import com.example.halalcheck3.eventbus.MyUpdateCartEvent;
import com.example.halalcheck3.model.CartModel;
import com.google.firebase.database.FirebaseDatabase;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

public class MyCartAdapter extends RecyclerView.Adapter<MyCartAdapter.MyCartViewHolder> {

    private Context context;
    private List<CartModel> cartModelList;

    public MyCartAdapter(Context context, List<CartModel> cartModelList) {
        this.context = context;
        this.cartModelList = cartModelList;
    }

    public class MyCartViewHolder extends RecyclerView.ViewHolder {

        ImageView btnMinus;
        ImageView btnPlus;
        ImageView btnDelete;
        TextView txtName;
        TextView txtPrice;
        TextView txtQuantity;

        public MyCartViewHolder(@NonNull View itemView) {
            super(itemView);
            btnMinus = itemView.findViewById(R.id.btnMinus);
            btnPlus = itemView.findViewById(R.id.btnPlus);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            txtName = itemView.findViewById(R.id.txtName);
            txtPrice = itemView.findViewById(R.id.txtPrice);
            txtQuantity = itemView.findViewById(R.id.txtQuantity);
        }
    }

    @NonNull
    @Override
    public MyCartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_cart_item, parent, false);
        return new MyCartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyCartViewHolder holder, int position) {

        holder.txtPrice.setText(new StringBuilder("€").append(cartModelList.get(position).getPrice()));
        holder.txtName.setText(new StringBuilder().append(cartModelList.get(position).getName()));
        holder.txtQuantity.setText(new StringBuilder().append(cartModelList.get(position).getQuantity()));

        //Event
        holder.btnMinus.setOnClickListener(v -> {
            minusCartItem(holder, cartModelList.get(position));
        });

        holder.btnPlus.setOnClickListener(v -> {
            plusCartItem(holder, cartModelList.get(position));
        });

        holder.btnDelete.setOnClickListener(v -> {
            AlertDialog dialog = new AlertDialog.Builder(context)
                    .setTitle("Delete Item")
                    .setMessage("Are you sure you want to Delete")
                    .setNegativeButton("CANCEL", (dialog1, which) -> dialog1.dismiss())
                    .setPositiveButton("YES", (dialog12, which) -> {
                        // Temp Removed
                        notifyItemRemoved(position);
                        deleteFromFirebase(cartModelList.get(position));
                        dialog12.dismiss();
                    }).create();
            dialog.show();
        });
    }

    private void deleteFromFirebase(CartModel cartModel) {
        FirebaseDatabase.getInstance()
                .getReference("Cart")
                .child("UNIQUE_USER_ID")
                .child(cartModel.getKey())
                .removeValue()
                .addOnSuccessListener(aVoid -> EventBus.getDefault().postSticky(new MyUpdateCartEvent()));
    }

    private void plusCartItem(MyCartViewHolder holder, CartModel cartModel) {
        cartModel.setQuantity(cartModel.getQuantity() + 1);
        cartModel.setTotalPrice(cartModel.getQuantity() * cartModel.getPrice());
        holder.txtQuantity.setText(String.valueOf(cartModel.getQuantity()));
        updateFirebase(cartModel);
    }

    private void minusCartItem(MyCartViewHolder holder, CartModel cartModel) {
        if (cartModel.getQuantity() > 1) {
            cartModel.setQuantity(cartModel.getQuantity() - 1);
            cartModel.setTotalPrice(cartModel.getQuantity() * cartModel.getPrice());
            holder.txtQuantity.setText(String.valueOf(cartModel.getQuantity()));
            updateFirebase(cartModel);
        }
    }

    @Override
    public int getItemCount() {
        return cartModelList.size();
    }

    private void updateFirebase(CartModel cartModel) {
        FirebaseDatabase.getInstance()
                .getReference("Cart")
                .child("UNIQUE_USER_ID")
                .child(cartModel.getKey())
                .setValue(cartModel)
                .addOnSuccessListener(aVoid -> EventBus.getDefault().postSticky(new MyUpdateCartEvent()));
    }
}
