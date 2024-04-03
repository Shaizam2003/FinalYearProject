package com.example.halalcheck3;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.halalcheck3.model.MenuItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RestaurantMenuActivity extends AppCompatActivity {

    private EditText itemNameEditText, itemPriceEditText;
    private Button addItemButton;

    private DatabaseReference menuRef;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_menu);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            // Redirect user to login screen or perform necessary authentication
            // return;
        }
        String userId = currentUser.getUid();

        menuRef = FirebaseDatabase.getInstance().getReference().child("Menu").child(userId);

        itemNameEditText = findViewById(R.id.itemNameEditText);
        itemPriceEditText = findViewById(R.id.itemPriceEditText);
        addItemButton = findViewById(R.id.addItemButton);

        addItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addItemToMenu();
            }
        });
    }

    private void addItemToMenu() {
        String itemName = itemNameEditText.getText().toString().trim();
        String itemPrice = itemPriceEditText.getText().toString().trim();

        if (!itemName.isEmpty() && !itemPrice.isEmpty()) {
            // Push new item to Firebase database
            menuRef.push().setValue(new MenuItem(itemName, Double.parseDouble(itemPrice)));

            // Clear input fields
            itemNameEditText.setText("");
            itemPriceEditText.setText("");

            Toast.makeText(this, "Item added to menu", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Please enter item name and price", Toast.LENGTH_SHORT).show();
        }
    }
}
