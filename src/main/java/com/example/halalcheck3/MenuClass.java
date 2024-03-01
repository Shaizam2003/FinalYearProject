package com.example.halalcheck3;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.halalcheck3.R;
import com.example.halalcheck3.adapter.MyMenuAdapter;
import com.example.halalcheck3.eventbus.MyUpdateCartEvent;
import com.example.halalcheck3.listener.ICartLoadListener;
import com.example.halalcheck3.listener.IMenuLoadListener;
import com.example.halalcheck3.model.CartModel;
import com.example.halalcheck3.model.MenuItem;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import java.util.ArrayList;
import java.util.List;

public class MenuClass extends AppCompatActivity implements IMenuLoadListener, ICartLoadListener {

    RecyclerView recyclerMenu;
    RelativeLayout menuLayout;
    FrameLayout btnCart;

    IMenuLoadListener menuLoadListener;
    ICartLoadListener cartLoadListener;

    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    List<MenuItem> menuItems = new ArrayList<>(); // Declare menuItems list
    MyMenuAdapter menuAdapter; // Declare menuAdapter

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (EventBus.getDefault().hasSubscriberForEvent(MyUpdateCartEvent.class))
            EventBus.getDefault().removeStickyEvent(MyUpdateCartEvent.class);
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onUpdateCart(MyUpdateCartEvent event) {
        countCartItem();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_class);

        recyclerMenu = findViewById(R.id.recycler_menu);
        menuLayout = findViewById(R.id.menuLayout);
        btnCart = findViewById(R.id.btnCart);

        init();
        loadMenuFromFirebase();
        countCartItem();
    }

    private void loadMenuFromFirebase() {
        String userId = firebaseAuth.getCurrentUser().getUid();
        DatabaseReference menuRef = FirebaseDatabase.getInstance().getReference().child("Menu").child(userId);

        menuRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                menuItems.clear();

                for (DataSnapshot itemSnapshot : dataSnapshot.getChildren()) {
                    MenuItem menuItem = itemSnapshot.getValue(MenuItem.class);

                    if (menuItem != null) {
                        menuItems.add(menuItem);
                    }
                }

                // Notify the adapter that the data has changed
                menuAdapter.notifyDataSetChanged();

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

    private void init() {
        menuLoadListener = this;
        cartLoadListener = this;

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        recyclerMenu.setLayoutManager(gridLayoutManager);

        // Initialize the adapter
        menuAdapter = new MyMenuAdapter(this, menuItems, cartLoadListener);
        recyclerMenu.setAdapter(menuAdapter);
    }

    @Override
    public void onMenuLoadSuccess(List<MenuItem> menuItemList) {
    }

    @Override
    public void onMenuLoadFailed(String message) {
        Snackbar.make(menuLayout, message, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void onCartLoadSuccess(List<CartModel> cartModelList) {
        int cartSum = 0;
        for (CartModel cartModel : cartModelList)
            cartSum += cartModel.getQuantity();
        // Update your UI with the cart sum
    }

    @Override
    public void onCartLoadFailed(String message) {
        Snackbar.make(menuLayout, message, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void OnCartLoadFailed(String message) {
        Snackbar.make(menuLayout, message, Snackbar.LENGTH_LONG).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        countCartItem();
    }

    private void countCartItem() {
        List<CartModel> cartModels = new ArrayList<>();
        FirebaseDatabase.getInstance().getReference("Cart")
                .child("UNIQUE_USER_ID")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot cartSnapshot : snapshot.getChildren()) {
                            CartModel cartModel = cartSnapshot.getValue(CartModel.class);
                            if (cartModel != null) {
                                cartModel.setKey(cartSnapshot.getKey());
                                cartModels.add(cartModel);
                            }
                        }
                        cartLoadListener.onCartLoadSuccess(cartModels);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        cartLoadListener.onCartLoadFailed(error.getMessage());
                    }
                });
    }
}
