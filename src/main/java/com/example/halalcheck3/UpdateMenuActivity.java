package com.example.halalcheck3;


import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.example.halalcheck3.model.MenuItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class UpdateMenuActivity extends AppCompatActivity {

    private EditText itemNameEditText, itemPriceEditText;
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
            // Redirect user to login screen or perform necessary authentication
            // Here you might want to redirect the user to the login screen or show an appropriate message
            Log.e("UpdateMenuActivity", "User is not logged in");
            // You could start an activity here to redirect to a login screen
            return;
        }
        String userId = currentUser.getUid();

        // Update the reference to point to the specific business user's menu
        menuRef = FirebaseDatabase.getInstance().getReference().child("businesses").child(userId).child("Menu");

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
            // Push new item to FirebaseUtil database
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
