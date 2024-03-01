package com.example.halalcheck3;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.halalcheck3.adapter.CartAdapter;
import com.example.halalcheck3.model.MenuItem;

import java.util.ArrayList;

public class CartActivity extends AppCompatActivity {

    private ListView cartListView;
    private ArrayList<MenuItem> cartItems;
    private CartAdapter cartAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        // Get the cart items from the intent
       // cartItems = getIntent().getParcelableArrayListExtra("cartItems");

        // Initialize the ListView and Adapter
        cartListView = findViewById(R.id.cartListView);
        cartAdapter = new CartAdapter(this, cartItems);
       // cartListView.setAdapter(cartAdapter);

        // Assuming you have a button for checkout in your layout
        Button checkoutButton = findViewById(R.id.checkoutButton);

        // Set onClickListener for the checkoutButton
        checkoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open another activity when the checkout button is clicked
                startActivity(new Intent(CartActivity.this, PaymentActivity.class));
            }
        });
    }
}
