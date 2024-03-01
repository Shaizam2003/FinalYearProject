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
    private ICartLoadListener iCartLoadListener;

    public MyMenuAdapter(Context context, List<MenuItem> menuItemList, ICartLoadListener iCartLoadListener) {
        this.context = context;
        this.menuItemList = menuItemList;
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
        holder.txtPrice.setText("$" + menuItem.getItemPrice());

        holder.setListener((view, adapterPosition) -> {
            addToCart(menuItemList.get(adapterPosition)); // Use adapterPosition to get correct item
        });
    }

    private void addToCart(MenuItem menuItem) {
        DatabaseReference userCart = FirebaseDatabase
                .getInstance()
                .getReference("Cart")
                .child("UNIQUE_USER_ID");

        userCart.child(String.valueOf(menuItemList.indexOf(menuItem))) // Use indexOf to get position as key
                .addListenerForSingleValueEvent(new ValueEventListener(){

                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()) //if user already have item in cart
                        {
                            //just update the quantity and totalPrice
                            CartModel cartModel = snapshot.getValue(CartModel.class);
                            cartModel.setQuantity(cartModel.getQuantity() + 1);
                            Map<String,Object> updateData = new HashMap<>();
                            updateData.put("quantity", cartModel.getQuantity());
                            updateData.put("totalPrice", cartModel.getQuantity() * Float.parseFloat(cartModel.getPrice()));

                            userCart.child(String.valueOf(menuItemList.indexOf(menuItem))) // Use indexOf to get position as key
                                    .updateChildren(updateData)
                                    .addOnSuccessListener(aVoid ->  {
                                        iCartLoadListener.onCartLoadSuccess(new ArrayList<>()); // Passing an empty list
                                    })
                                    .addOnFailureListener(e -> iCartLoadListener.onCartLoadFailed(e.getMessage()));

                        }
                        else //if Items are not in Cart, add!
                        {
                            CartModel cartModel = new CartModel();
                            cartModel.setName(menuItem.getItemName());
                            cartModel.setPrice(menuItem.getItemPrice());
                            cartModel.setQuantity(1);
                            cartModel.setTotalPrice(Float.parseFloat(menuItem.getItemPrice()));

                            userCart.child(String.valueOf(menuItemList.indexOf(menuItem))) // Use indexOf to get position as key
                                    .setValue(cartModel.toMap())
                                    .addOnSuccessListener(aVoid ->  {
                                        iCartLoadListener.onCartLoadSuccess(new ArrayList<>()); // Passing an empty list
                                    })
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

    public class MyMenuViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView imageView;
        TextView txtName;
        TextView txtPrice;

        IRecyclerViewClickListener listener;

        public void setListener(IRecyclerViewClickListener listener) {
            this.listener = listener;
        }


        public MyMenuViewHolder(@NonNull View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txtName);
            txtPrice = itemView.findViewById(R.id.txtPrice);
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            if (listener != null) {
                listener.onRecyclerClick(v, getAdapterPosition());
            }
            // Display a Snackbar message indicating that the item has been added to the cart
            Snackbar.make(itemView, "Item added to cart", Snackbar.LENGTH_SHORT).show();
        }
    }
}
