package com.example.halalcheck3.BusinessSide;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.halalcheck3.R;
import com.example.halalcheck3.adapter.MenuCategoryAdapter;
import com.example.halalcheck3.adapter.MyMenuAdapter;
import com.example.halalcheck3.listener.ICartLoadListener;
import com.example.halalcheck3.listener.IMenuLoadListener;
import com.example.halalcheck3.model.MenuCategory;
import com.example.halalcheck3.model.MenuItem;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.util.Log;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.halalcheck3.model.MenuItem;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class RestaurantMenuActivity extends AppCompatActivity {

    private RecyclerView recyclerMenu;
    private View menuLayout;
    private List<MenuCategory> menuCategories = new ArrayList<>();
    private MenuCategoryAdapter menuCategoryAdapter;
    private DatabaseReference menuRef;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_menu);

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser == null) {
            Log.e("RestaurantMenuActivity", "User is not logged in");
            return;
        }

        String userId = currentUser.getUid();
        Log.d("RestaurantMenuActivity", "Current User ID: " + userId);
        menuRef = FirebaseDatabase.getInstance().getReference().child("businesses").child(userId).child("Menu");

        recyclerMenu = findViewById(R.id.recycler_menu);
        menuLayout = findViewById(R.id.menuLayout);

        initRecyclerView();
        loadMenuFromFirebase();
    }

    private void initRecyclerView() {
        recyclerMenu.setLayoutManager(new LinearLayoutManager(this));
        menuCategoryAdapter = new MenuCategoryAdapter(this, menuCategories);
        recyclerMenu.setAdapter(menuCategoryAdapter);
    }

    private void loadMenuFromFirebase() {
        menuRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                menuCategories.clear(); // Clear the list before adding new items

                for (DataSnapshot categorySnapshot : dataSnapshot.getChildren()) {
                    String categoryName = categorySnapshot.getKey();
                    List<MenuItem> items = new ArrayList<>();

                    for (DataSnapshot itemSnapshot : categorySnapshot.getChildren()) {
                        String itemName = itemSnapshot.child("itemName").getValue(String.class);
                        Object itemPriceObj = itemSnapshot.child("itemPrice").getValue();
                        double itemPrice = 0.0;

                        if (itemPriceObj instanceof Long) {
                            itemPrice = ((Long) itemPriceObj).doubleValue(); // Convert Long to double
                        } else if (itemPriceObj instanceof Double) {
                            itemPrice = (Double) itemPriceObj; // Directly assign Double value
                        } else {
                            Log.e("RestaurantMenuActivity", "Unexpected itemPrice type: " + (itemPriceObj != null ? itemPriceObj.getClass().getName() : "null"));
                        }

                        MenuItem menuItem = new MenuItem(itemName, itemPrice);
                        items.add(menuItem);
                    }

                    MenuCategory menuCategory = new MenuCategory(categoryName, items);
                    menuCategories.add(menuCategory);
                }

                menuCategoryAdapter.notifyDataSetChanged(); // Notify adapter after the list is updated

                if (menuCategories.isEmpty()) {
                    Snackbar.make(menuLayout, "No menu items available", Snackbar.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Snackbar.make(menuLayout, "Database Error: " + error.getMessage(), Snackbar.LENGTH_LONG).show();
            }
        });
    }
}