package com.example.halalcheck3.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.halalcheck3.MenuClass;
import com.example.halalcheck3.R;
import com.example.halalcheck3.eventbus.MyUpdateCartEvent;
import com.example.halalcheck3.listener.ICartLoadListener;
import com.example.halalcheck3.listener.IRecyclerViewClickListener;
import com.example.halalcheck3.model.CartModel;
import com.example.halalcheck3.model.MenuItem;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.greenrobot.eventbus.EventBus;


public class MyMenuAdapter extends RecyclerView.Adapter<MyMenuAdapter.MyMenuViewHolder> {

    private Context context;
    private List<MenuItem> menuItemList;
    private DatabaseReference userCartRef;
    private ICartLoadListener iCartLoadListener;

    public MyMenuAdapter(Context context, List<MenuItem> menuItemList, DatabaseReference userCartRef, ICartLoadListener iCartLoadListener) {
        this.context = context.getApplicationContext(); // Use application context to prevent memory leaks
        this.menuItemList = menuItemList;
        this.userCartRef = userCartRef;
        this.iCartLoadListener = iCartLoadListener;
    }

    @NonNull
    @Override
    public MyMenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.layout_menu_item, parent, false);
        return new MyMenuViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyMenuViewHolder holder, int position) {
        MenuItem menuItem = menuItemList.get(position);
        holder.txtName.setText(menuItem.getItemName());
        holder.txtPrice.setText("€" + menuItem.getItemPrice());

        holder.itemView.setOnClickListener(v -> addToCart(menuItem));
    }

    private void addToCart(MenuItem menuItem) {
        userCartRef.child(String.valueOf(menuItemList.indexOf(menuItem)))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            CartModel cartModel = snapshot.getValue(CartModel.class);
                            if (cartModel != null) {
                                cartModel.setQuantity(cartModel.getQuantity() + 1);
                                Map<String, Object> updateData = new HashMap<>();
                                updateData.put("quantity", cartModel.getQuantity());
                                updateData.put("totalPrice", cartModel.getQuantity() * Float.parseFloat(cartModel.getPrice()));

                                userCartRef.child(String.valueOf(menuItemList.indexOf(menuItem)))
                                        .updateChildren(updateData)
                                        .addOnSuccessListener(aVoid -> iCartLoadListener.onCartLoadSuccess(new ArrayList<>()))
                                        .addOnFailureListener(e -> iCartLoadListener.onCartLoadFailed(e.getMessage()));
                            }
                        } else {
                            CartModel cartModel = new CartModel();
                            cartModel.setName(menuItem.getItemName());
                            cartModel.setPrice(menuItem.getItemPrice());
                            cartModel.setQuantity(1);
                            cartModel.setTotalPrice(Float.parseFloat(menuItem.getItemPrice()));

                            userCartRef.child(String.valueOf(menuItemList.indexOf(menuItem)))
                                    .setValue(cartModel.toMap())
                                    .addOnSuccessListener(aVoid -> iCartLoadListener.onCartLoadSuccess(new ArrayList<>()))
                                    .addOnFailureListener(e -> iCartLoadListener.onCartLoadFailed(e.getMessage()));
                        }
                        EventBus.getDefault().postSticky(new MyUpdateCartEvent());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        iCartLoadListener.onCartLoadFailed(error.getMessage());
                    }
                });
    }

    @Override
    public int getItemCount() {
        return menuItemList.size();
    }

    public class MyMenuViewHolder extends RecyclerView.ViewHolder {

        TextView txtName;
        TextView txtPrice;

        public MyMenuViewHolder(@NonNull View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txtName);
            txtPrice = itemView.findViewById(R.id.txtPrice);
        }
    }
}
