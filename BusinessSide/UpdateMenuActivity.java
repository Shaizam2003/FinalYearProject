package com.example.halalcheck3.BusinessSide;


import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.halalcheck3.R;
import com.example.halalcheck3.model.MenuItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class UpdateMenuActivity extends AppCompatActivity {

    private EditText itemNameEditText, itemPriceEditText;
    private Spinner categorySpinner;
    private Button addItemButton;

    private DatabaseReference menuRef;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_menu);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Log.e("UpdateMenuActivity", "User is not logged in");
            return;
        }
        String userId = currentUser.getUid();

        menuRef = FirebaseDatabase.getInstance().getReference().child("businesses").child(userId).child("Menu");

        itemNameEditText = findViewById(R.id.itemNameEditText);
        itemPriceEditText = findViewById(R.id.itemPriceEditText);
        categorySpinner = findViewById(R.id.categorySpinner);
        addItemButton = findViewById(R.id.addItemButton);

        // Set up the Spinner with predefined categories
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.menu_categories, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);

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
        String selectedCategory = categorySpinner.getSelectedItem().toString();

        if (!itemName.isEmpty() && !itemPrice.isEmpty() && !selectedCategory.isEmpty()) {
            // Save the item under the selected category in Firebase
            DatabaseReference categoryRef = menuRef.child(selectedCategory);
            categoryRef.push().setValue(new MenuItem(itemName, Double.parseDouble(itemPrice)));

            // Clear input fields
            itemNameEditText.setText("");
            itemPriceEditText.setText("");

            Toast.makeText(this, "Item added to menu", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Please enter item name, price, and select a category", Toast.LENGTH_SHORT).show();
        }
    }
}
