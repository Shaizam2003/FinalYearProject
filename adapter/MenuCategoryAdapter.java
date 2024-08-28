package com.example.halalcheck3.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.halalcheck3.R;
import com.example.halalcheck3.model.MenuCategory;
import com.example.halalcheck3.model.MenuItem;

import java.util.ArrayList;
import java.util.List;

public class MenuCategoryAdapter extends RecyclerView.Adapter<MenuCategoryAdapter.CategoryViewHolder> {

    private Context context;
    private List<MenuCategory> menuCategories;

    public MenuCategoryAdapter(Context context, List<MenuCategory> menuCategories) {
        this.context = context;
        this.menuCategories = menuCategories;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_menu_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        MenuCategory category = menuCategories.get(position);
        holder.categoryNameTextView.setText(category.getCategoryName());

        // Set up the RecyclerView for menu items
        MyMenuAdapter menuItemAdapter = new MyMenuAdapter(context, category.getMenuItems());
        holder.itemsRecyclerView.setAdapter(menuItemAdapter);
        holder.itemsRecyclerView.setLayoutManager(new LinearLayoutManager(context));

        // Save the adapter reference
        holder.itemsRecyclerView.setTag(menuItemAdapter);
    }

    @Override
    public int getItemCount() {
        return menuCategories.size();
    }

    public List<MenuItem> getSelectedItems() {
        List<MenuItem> selectedItems = new ArrayList<>();
        for (int i = 0; i < getItemCount(); i++) {
            // Get the ViewHolder for the current category
            CategoryViewHolder holder = (CategoryViewHolder) ((RecyclerView) ((Activity) context).findViewById(R.id.recycler_menu))
                    .findViewHolderForAdapterPosition(i);
            if (holder != null) {
                // Retrieve the MyMenuAdapter from the ViewHolder
                MyMenuAdapter adapter = (MyMenuAdapter) holder.itemsRecyclerView.getTag();
                if (adapter != null) {
                    selectedItems.addAll(adapter.getSelectedItems());
                }
            }
        }
        return selectedItems;
    }

    public static class CategoryViewHolder extends RecyclerView.ViewHolder {
        TextView categoryNameTextView;
        RecyclerView itemsRecyclerView;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryNameTextView = itemView.findViewById(R.id.categoryNameTextView);
            itemsRecyclerView = itemView.findViewById(R.id.itemsRecyclerView);
        }
    }
}
