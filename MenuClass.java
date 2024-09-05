package com.example.halalcheck3;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import androidx.annotation.NonNull;
import androidx.appcompat.view.menu.MenuAdapter;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.halalcheck3.R;
import com.example.halalcheck3.adapter.MenuCategoryAdapter;
import com.example.halalcheck3.adapter.MyMenuAdapter;
import com.example.halalcheck3.eventbus.MyUpdateCartEvent;
import com.example.halalcheck3.listener.ICartLoadListener;
import com.example.halalcheck3.listener.IMenuLoadListener;
import com.example.halalcheck3.model.Business;
import com.example.halalcheck3.model.CartModel;
import com.example.halalcheck3.model.MenuCategory;
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
import java.util.Map;

public class MenuClass extends AppCompatActivity implements IMenuLoadListener, ICartLoadListener {

    RecyclerView recyclerMenu;
    RelativeLayout menuLayout;
    FrameLayout btnCart;

    IMenuLoadListener menuLoadListener;
    ICartLoadListener cartLoadListener;

    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    String userId;
    String businessUserId;
    private DatabaseReference cartRef;

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
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

        // Retrieve the business userId and phoneNumber from the intent
        Intent intent = getIntent();
        userId = intent.getStringExtra("UserId");
        businessUserId = userId;
        String phoneNumber = intent.getStringExtra("phone_number");

        recyclerMenu = findViewById(R.id.recycler_menu);
        menuLayout = findViewById(R.id.menuLayout);
        btnCart = findViewById(R.id.btnCart);

        menuLoadListener = this;
        cartLoadListener = this;

        recyclerMenu.setLayoutManager(new LinearLayoutManager(this));

        loadMenuFromFirebase();

        btnCart.setOnClickListener(v -> {
            // Retrieve selected menu items from the adapter
            MenuCategoryAdapter adapter = (MenuCategoryAdapter) recyclerMenu.getAdapter();
            if (adapter != null) {
                List<MenuItem> selectedItems = adapter.getSelectedItems();
                if (selectedItems != null && !selectedItems.isEmpty()) {
                    // Pass the selected items to CartActivity
                    Intent intent1 = new Intent(MenuClass.this, CartActivity.class);
                    intent1.putExtra("BusinessId", businessUserId);
                    intent1.putExtra("selectedItems", new ArrayList<>(selectedItems));
                    startActivity(intent1);
                } else {
                    Snackbar.make(btnCart, "No items selected", Snackbar.LENGTH_LONG).show();
                }
            } else {
                Snackbar.make(btnCart, "No items available", Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void loadMenuFromFirebase() {
        DatabaseReference businessRef = FirebaseDatabase.getInstance().getReference()
                .child("businesses").child(businessUserId).child("Menu");

        businessRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<MenuCategory> menuCategories = new ArrayList<>();
                for (DataSnapshot categorySnapshot : dataSnapshot.getChildren()) {
                    String categoryName = categorySnapshot.getKey();
                    List<MenuItem> items = new ArrayList<>();

                    for (DataSnapshot itemSnapshot : categorySnapshot.getChildren()) {
                        try {
                            Map<String, Object> itemData = (Map<String, Object>) itemSnapshot.getValue();
                            if (itemData != null) {
                                String itemName = (String) itemData.get("itemName");
                                Object itemPriceObj = itemData.get("itemPrice");
                                double itemPrice;

                                if (itemPriceObj instanceof Long) {
                                    itemPrice = ((Long) itemPriceObj).doubleValue(); // Convert Long to double
                                } else if (itemPriceObj instanceof Integer) {
                                    itemPrice = ((Integer) itemPriceObj).doubleValue(); // Convert Integer to double
                                } else if (itemPriceObj instanceof Double) {
                                    itemPrice = (Double) itemPriceObj; // Directly use the Double value
                                } else {
                                    itemPrice = 0.0; // Default value
                                    Log.e("MenuClass", "Unexpected itemPrice type: " + itemPriceObj.getClass().getName());
                                }

                                MenuItem menuItem = new MenuItem(itemName, itemPrice);
                                items.add(menuItem);
                            } else {
                                Log.e("MenuClass", "Error: Item data is null");
                            }
                        } catch (Exception e) {
                            Log.e("MenuClass", "Error parsing MenuItem: " + e.getMessage());
                        }
                    }

                    if (!items.isEmpty()) {
                        menuCategories.add(new MenuCategory(categoryName, items));
                    }
                }

                if (menuCategories.isEmpty()) {
                    Snackbar.make(menuLayout, "No menu items available", Snackbar.LENGTH_LONG).show();
                } else {
                    setupRecyclerView(menuCategories);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Snackbar.make(menuLayout, "Database Error: " + error.getMessage(), Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void setupRecyclerView(List<MenuCategory> menuCategories) {
        MenuCategoryAdapter menuCategoryAdapter = new MenuCategoryAdapter(this, menuCategories);
        recyclerMenu.setAdapter(menuCategoryAdapter);
        recyclerMenu.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public void onMenuLoadSuccess(List<MenuItem> menuItemList) {
        // Handle successful menu load
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
    protected void onResume() {
        super.onResume();
        countCartItem();
    }

    private void countCartItem() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference cartRef = FirebaseDatabase.getInstance().getReference("Cart").child(userId);

        cartRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<CartModel> cartModels = new ArrayList<>();
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
