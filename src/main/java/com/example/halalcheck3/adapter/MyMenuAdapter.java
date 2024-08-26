package com.example.halalcheck3.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.halalcheck3.MenuClass;
import com.example.halalcheck3.R;
import com.example.halalcheck3.RestaurantMenuActivity;
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
    private List<MenuItem> selectedItems; // ArrayList to store selected menu items

    public MyMenuAdapter(Context context, List<MenuItem> menuItemList) {
        this.context = context;
        this.menuItemList = menuItemList;
        this.selectedItems = new ArrayList<>(); // Initialize selected items ArrayList
    }

    public MyMenuAdapter(RestaurantMenuActivity context, List<MenuItem> menuItems) {
        this.context = context; // Use activity context
        this.menuItemList = menuItems;
        this.selectedItems = new ArrayList<>(); // Initialize selected items ArrayList
    }

    @NonNull
    @Override
    public MyMenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the item layout and create a ViewHolder
        View itemView = LayoutInflater.from(context).inflate(R.layout.layout_menu_item, parent, false);
        return new MyMenuViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyMenuViewHolder holder, int position) {
        // Bind data to ViewHolder
        MenuItem menuItem = menuItemList.get(position);
        holder.txtName.setText(menuItem.getItemName());
        holder.txtPrice.setText("€" + menuItem.getItemPrice());

        // Handle item click event
        holder.itemView.setOnClickListener(v -> {
            addToCart(menuItem); // Add selected item to cart
        });
    }

    private void addToCart(MenuItem menuItem) {
        // Add the selected item to the selectedItems ArrayList
        selectedItems.add(menuItem);

        // Optionally, you can display a message or perform any other action here
        Toast.makeText(context, menuItem.getItemName() + " added to cart", Toast.LENGTH_SHORT).show();
    }

    public List<MenuItem> getSelectedItems() {
        // Getter method to retrieve selected items
        return selectedItems;
    }

    @Override
    public int getItemCount() {
        return menuItemList != null ? menuItemList.size() : 0;
    }

    public class MyMenuViewHolder extends RecyclerView.ViewHolder {

        // ViewHolder class to hold item views
        TextView txtName;
        TextView txtPrice;

        public MyMenuViewHolder(@NonNull View itemView) {
            super(itemView);
            // Initialize views
            txtName = itemView.findViewById(R.id.txtName);
            txtPrice = itemView.findViewById(R.id.txtPrice);
        }
    }
}
