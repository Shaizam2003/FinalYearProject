package com.example.halalcheck3;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

// MainActivity.java
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

// MenuClass.java
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;



public class MenuClass extends AppCompatActivity {

    private ListView menuListView;
    private ArrayList<String> menuItems;
    private ArrayAdapter<String> menuAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_class);

        menuListView = findViewById(R.id.menuListView);
        menuItems = new ArrayList<>();
        menuAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, menuItems);
        menuListView.setAdapter(menuAdapter);

        // Read menu items from Firebase Realtime Database
        readMenuItems();

        // Assuming checkoutButton is declared in your XML layout
        Button checkoutButton = findViewById(R.id.checkoutButton);

        // Set onClickListener for the checkoutButton
        checkoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open another activity when the checkout button is clicked
                startActivity(new Intent(MenuClass.this, PaymentActivity.class));
            }
        });
    }

    private void readMenuItems() {
        DatabaseReference menuRef = FirebaseDatabase.getInstance().getReference().child("Menu");

        menuRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("MenuClass", "onDataChange called");

                menuItems.clear();

                for (DataSnapshot itemSnapshot : dataSnapshot.getChildren()) {
                    MenuItem menuItem = itemSnapshot.getValue(MenuItem.class);

                    if (menuItem != null) {
                        String itemName = menuItem.getItemName();
                        double itemPrice = menuItem.getItemPrice();
                        Log.d("MenuClass", "Item: " + itemName + ", Price: " + itemPrice);

                        String itemInfo = itemName + ": $" + itemPrice;
                        menuItems.add(itemInfo);
                    }
                }

                // Notify the adapter that the data has changed
                menuAdapter.notifyDataSetChanged();

                if (menuItems.isEmpty()) {
                    // Handle case when no menu items are available
                    Log.d("MenuClass", "No menu items available");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("MenuClass", "DatabaseError: " + error.getMessage());
            }
        });
    }
}
