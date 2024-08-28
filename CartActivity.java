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
import java.util.Locale;

public class CartActivity extends AppCompatActivity {

    private RecyclerView recyclerCart;
    private RelativeLayout mainLayout;
    private ImageView btnBlack;
    private TextView txtTotal;
    private Button btnCheckout;
    private MyCartAdapter adapter;
    private double totalAmount; // Store total amount
    String businessUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        recyclerCart = findViewById(R.id.recycler_cart);
        mainLayout = findViewById(R.id.cartLayout);
        btnBlack = findViewById(R.id.btnBlack);
        txtTotal = findViewById(R.id.txtTotal);
        btnCheckout = findViewById(R.id.btnCheckout);

        init();

        List<MenuItem> selectedItems = (List<MenuItem>) getIntent().getSerializableExtra("selectedItems");

        businessUserId = getIntent().getStringExtra("BusinessId");
        List<CartModel> cartModels = convertToCartModels(selectedItems);
        displaySelectedItems(cartModels);

        btnCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CartActivity.this, PaymentActivity.class);
                intent.putExtra("totalAmount", totalAmount); // Pass total amount
                intent.putExtra("BusinessId", businessUserId);
                intent.putExtra("selectedItems", new ArrayList<>(selectedItems));
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
            cartModel.setTotalPrice(item.getItemPrice());
            cartModels.add(cartModel);
        }
        return cartModels;
    }

    private void displaySelectedItems(List<CartModel> cartModels) {
        totalAmount = 0;
        for (CartModel cartModel : cartModels) {
            totalAmount += cartModel.getTotalPrice();
        }
        txtTotal.setText(String.format(Locale.getDefault(), "€%.2f", totalAmount)); // Display total amount
        adapter = new MyCartAdapter(this, cartModels);
        recyclerCart.setAdapter(adapter);
    }

    private void init() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerCart.setLayoutManager(layoutManager);
        recyclerCart.addItemDecoration(new DividerItemDecoration(this, layoutManager.getOrientation()));
        btnBlack.setOnClickListener(v -> finish());
    }
}
