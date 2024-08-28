package com.example.halalcheck3.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.halalcheck3.R;
import com.example.halalcheck3.BusinessSide.RestaurantMenuActivity;
import com.example.halalcheck3.model.MenuItem;

import java.util.ArrayList;
import java.util.List;


public class MyMenuAdapter extends RecyclerView.Adapter<MyMenuAdapter.MenuItemViewHolder> {

    private Context context;
    private List<MenuItem> menuItems;
    private List<MenuItem> selectedItems = new ArrayList<>();

    public MyMenuAdapter(Context context, List<MenuItem> menuItems) {
        this.context = context;
        this.menuItems = menuItems;
    }

    @NonNull
    @Override
    public MenuItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_menu_item, parent, false);
        return new MenuItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MenuItemViewHolder holder, int position) {
        MenuItem item = menuItems.get(position);
        holder.itemNameTextView.setText(item.getItemName());
        holder.itemPriceTextView.setText(String.valueOf(item.getItemPrice()));

        // Handle item selection
        holder.itemView.setOnClickListener(v -> {
            if (selectedItems.contains(item)) {
                selectedItems.remove(item);
            } else {
                selectedItems.add(item);
            }
            notifyItemChanged(position); // Refresh the item view
        });

        // Update item view based on selection
        holder.itemView.setBackgroundColor(selectedItems.contains(item) ? Color.LTGRAY : Color.WHITE);
    }

    @Override
    public int getItemCount() {
        return menuItems.size();
    }

    public List<MenuItem> getSelectedItems() {
        return new ArrayList<>(selectedItems); // Return a copy to prevent external modifications
    }

    public static class MenuItemViewHolder extends RecyclerView.ViewHolder {
        TextView itemNameTextView;
        TextView itemPriceTextView;

        public MenuItemViewHolder(@NonNull View itemView) {
            super(itemView);
            itemNameTextView = itemView.findViewById(R.id.txtName);
            itemPriceTextView = itemView.findViewById(R.id.txtPrice);
        }
    }
}
