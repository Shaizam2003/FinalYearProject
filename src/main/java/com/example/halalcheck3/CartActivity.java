package com.example.halalcheck3;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.halalcheck3.adapter.MyCartAdapter;
import com.example.halalcheck3.eventbus.MyUpdateCartEvent;
import com.example.halalcheck3.listener.ICartLoadListener;
import com.example.halalcheck3.model.CartModel;
import com.example.halalcheck3.model.MenuItem;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import java.util.ArrayList;
import java.util.List;

public class CartActivity extends AppCompatActivity implements ICartLoadListener {

    private RecyclerView recyclerCart;
    private RelativeLayout mainLayout;
    private ImageView btnBlack;
    private TextView txtTotal;
    private Button btnCheckout;
    private MyCartAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        recyclerCart = findViewById(R.id.recycler_cart);
        mainLayout = findViewById(R.id.cartLayout);
        btnBlack = findViewById(R.id.btnBlack);
        txtTotal = findViewById(R.id.txtTotal);
        btnCheckout = findViewById(R.id.btnCheckout); // Initialize checkout button

        init();

        // Retrieve selected items from intent
        List<MenuItem> selectedItems = (List<MenuItem>) getIntent().getSerializableExtra("selectedItems");

        // Convert MenuItem objects to CartModel objects
        List<CartModel> cartModels = convertToCartModels(selectedItems);

        // Display selected items in the cart
        displaySelectedItems(cartModels);

        // Set OnClickListener for the checkout button
        btnCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to PaymentActivity
                Intent intent = new Intent(CartActivity.this, PaymentActivity.class);
                startActivity(intent);
            }
        });
    }

    private List<CartModel> convertToCartModels(List<MenuItem> selectedItems) {
        List<CartModel> cartModels = new ArrayList<>();
        for (MenuItem item : selectedItems) {
            CartModel cartModel = new CartModel();
            cartModel.setName(item.getItemName());
            cartModel.setPrice(item.getItemPrice());
            cartModel.setQuantity(1);
            cartModel.setTotalPrice(Float.parseFloat(item.getItemPrice()));
            cartModels.add(cartModel);
        }
        return cartModels;
    }

    private void displaySelectedItems(List<CartModel> cartModels) {
        double sum = 0;
        for (CartModel cartModel : cartModels) {
            sum += cartModel.getTotalPrice();
        }
        txtTotal.setText(new StringBuilder("€").append(sum));
        adapter = new MyCartAdapter(this, cartModels);
        recyclerCart.setAdapter(adapter);
    }

    private void init() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerCart.setLayoutManager(layoutManager);
        recyclerCart.addItemDecoration(new DividerItemDecoration(this, layoutManager.getOrientation()));
        btnBlack.setOnClickListener(v -> finish());
    }

    @Override
    public void onCartLoadSuccess(List<CartModel> cartModelList) {
        // Not used in this activity
    }

    @Override
    public void onCartLoadFailed(String message) {
        Snackbar.make(mainLayout, message, Snackbar.LENGTH_LONG).show();
    }
}
