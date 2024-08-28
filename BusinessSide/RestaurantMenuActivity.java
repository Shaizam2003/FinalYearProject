package com.example.halalcheck3.BusinessSide;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.halalcheck3.R;
import com.example.halalcheck3.adapter.MyMenuAdapter;
import com.example.halalcheck3.listener.ICartLoadListener;
import com.example.halalcheck3.listener.IMenuLoadListener;
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
    private RelativeLayout menuLayout;

    private List<MenuItem> menuItems = new ArrayList<>();
    private MyMenuAdapter menuAdapter;

    private DatabaseReference menuRef;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_menu);

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser == null) {
            // If user is not logged in, you can handle this case accordingly
            // For example, you can redirect to login screen or display a message
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
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        recyclerMenu.setLayoutManager(gridLayoutManager);
        menuAdapter = new MyMenuAdapter(this, menuItems);
        recyclerMenu.setAdapter(menuAdapter);
    }

    private void loadMenuFromFirebase() {
        menuRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                menuItems.clear();
                for (DataSnapshot itemSnapshot : dataSnapshot.getChildren()) {
                    String itemName = itemSnapshot.child("itemName").getValue(String.class);
                    double price = itemSnapshot.child("itemPrice").getValue(Double.class);

                 /*   // Convert Long to String
                    String priceString = String.valueOf(price);

                    double price = 0.0; // Default value or handle error cases
                    try {
                        price = Double.parseDouble(priceString);
                    } catch (NumberFormatException e) {
                        Log.e("FirebaseUtil", "Error parsing price: " + e.getMessage());
                    }*/

                    MenuItem menuItem = new MenuItem();
                    menuItem.setItemName(itemName);
                    menuItem.setItemPrice(price);

                    menuItems.add(menuItem);
                    menuAdapter.notifyDataSetChanged();
                }

                if (menuItems.isEmpty()) {
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
