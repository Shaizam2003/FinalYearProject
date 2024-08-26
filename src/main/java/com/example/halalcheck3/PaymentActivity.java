package com.example.halalcheck3;



import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.halalcheck3.model.MenuItem;
import com.example.halalcheck3.model.Order;
import com.example.halalcheck3.model.OrderItem;
import com.example.halalcheck3.model.OrderStatus;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.stripe.android.PaymentConfiguration;
import com.stripe.android.paymentsheet.PaymentSheet;
import com.stripe.android.paymentsheet.PaymentSheetResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class PaymentActivity extends AppCompatActivity {

    Button payment;
    String PublishableKey = "pk_live_51OeshEKHtWE6coC9MUIWrf1IDpdQHeonWM9beKM6PTx2vk2wfOgQ8IFftfhpRgL4PtNbT9zZkEZlYk3LyV61DQeB00EJw0xGdf";
    String SecretKey = "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";
    String CustomerId;
    String EphemeralKey;
    String ClientSecret;
    PaymentSheet paymentSheet;
    double totalAmount;
    private FirebaseAuth mAuth;
    private DatabaseReference mOrdersRef;
    String businessUserId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Initialize Firebase Database reference
        mOrdersRef = FirebaseDatabase.getInstance().getReference("orders");

        payment = findViewById(R.id.btnProcessPayment);

        PaymentConfiguration.init(this, PublishableKey);
        // paymentSheet = new PaymentSheet(this, this::onPaymentResult);

        // Retrieve total amount from intent
        totalAmount = getIntent().getDoubleExtra("totalAmount", 0);
        businessUserId = getIntent().getStringExtra("BusinessId");

        payment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveOrderToDatabase();
                //createCustomer();
            }
        });
    }

    private void createCustomer() {
        String url = "https://api.stripe.com/v1/customers";
        StringRequest request = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject object = new JSONObject(response);
                            CustomerId = object.getString("id");
                            Toast.makeText(PaymentActivity.this, "Customer ID: " + CustomerId, Toast.LENGTH_SHORT).show();
                            getEphemeralKey();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(PaymentActivity.this, "JSON Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(PaymentActivity.this, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + SecretKey);
                return headers;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);
    }

    private void getEphemeralKey() {
        String url = "https://api.stripe.com/v1/ephemeral_keys";
        StringRequest request = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject object = new JSONObject(response);
                            EphemeralKey = object.getString("id");
                            Toast.makeText(PaymentActivity.this, "Ephemeral Key: " + EphemeralKey, Toast.LENGTH_SHORT).show();
                            getClientSecret(CustomerId, EphemeralKey);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(PaymentActivity.this, "JSON Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(PaymentActivity.this, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + SecretKey);
                headers.put("Stripe-Version", "2022-08-01");
                return headers;
            }

            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("customer", CustomerId);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);
    }

    private void getClientSecret(String customerId, String ephemeralKey) {
        String url = "https://api.stripe.com/v1/payment_intents";
        StringRequest request = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject object = new JSONObject(response);
                            ClientSecret = object.getString("client_secret");
                            Toast.makeText(PaymentActivity.this, "Client Secret: " + ClientSecret, Toast.LENGTH_SHORT).show();
                            paymentFlow();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(PaymentActivity.this, "JSON Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(PaymentActivity.this, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + SecretKey);
                return headers;
            }

            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("customer", CustomerId);
                params.put("amount", String.valueOf((int) (totalAmount * 100))); // Amount in cents
                params.put("currency", "eur");
                params.put("automatic_payment_methods[enabled]", "true");
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);
    }

    private void paymentFlow() {
        paymentSheet.presentWithPaymentIntent(ClientSecret, new PaymentSheet.Configuration(
                "Learn with Shaiza",
                new PaymentSheet.CustomerConfiguration(
                        CustomerId,
                        EphemeralKey
                )
        ));
    }

    /*private void onPaymentResult(PaymentSheetResult paymentSheetResult) {
        if (paymentSheetResult instanceof PaymentSheetResult.Completed) {
            // Payment successful, save order to database
            saveOrderToDatabase();
            Toast.makeText(this, "Payment Success", Toast.LENGTH_SHORT).show();

            // Start intent to navigate to OrderStatusActivity
            Intent intent = new Intent(PaymentActivity.this, OrderStatusActivity.class);
            startActivity(intent);
        } else if (paymentSheetResult instanceof PaymentSheetResult.Failed) {
            Toast.makeText(this, "Payment Failed: " + ((PaymentSheetResult.Failed) paymentSheetResult).getError().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        } else if (paymentSheetResult instanceof PaymentSheetResult.Canceled) {
            Toast.makeText(this, "Payment Canceled", Toast.LENGTH_SHORT).show();
        }
    }*/

    private void saveOrderToDatabase() {
        // Get current user
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();

            // Generate a unique order ID
            String orderId = mOrdersRef.push().getKey();
            if (orderId != null) {
                // Create order object with the generated order ID
                Log.d("MyActivity", "Inside Save Order to Database");
                Order order = createOrder(orderId);
                Log.d("MyActivity", "Business user Id is " + order.getBusinessId());

                // Create a new OrderStatus object
                OrderStatus orderStatus = new OrderStatus();

                // Create a map for orderDetails node
                Map<String, Object> orderDetails = new HashMap<>();
                orderDetails.put("orderId", orderId);
                orderDetails.put("businessId", order.getBusinessId());
                orderDetails.put("customerId", order.getCustomerId());
                orderDetails.put("timestamp", order.getTimestamp());

                // Convert orderItems to a Map or List to store in Firebase
                List<Map<String, Object>> orderItemsData = new ArrayList<>();
                for (OrderItem item : order.getOrderItems()) {
                    Map<String, Object> itemData = new HashMap<>();
                    itemData.put("itemName", item.getItemName());
                    itemData.put("itemPrice", item.getItemPrice());
                    itemData.put("quantity", item.getQuantity());
                    itemData.put("totalPrice", item.getTotalPrice());
                    orderItemsData.add(itemData);
                }

                // Save to Firebase database
                DatabaseReference orderRef = mOrdersRef.child(orderId);
                orderRef.child("orderDetails").setValue(orderDetails); // Save order details
                orderRef.child("orderItems").setValue(orderItemsData); // Save order items
                orderRef.child("orderStatus").setValue(orderStatus); // Save order status

                orderRef.child("orderDetails").setValue(orderDetails)
                        .addOnSuccessListener(aVoid -> {
                            // Order and status saved successfully
                            Toast.makeText(PaymentActivity.this, "Order and status saved successfully", Toast.LENGTH_SHORT).show();

                            // Log to ensure this block is executing
                            Log.d("PaymentActivity", "Order saved, starting OrderStatusActivity");

                            // Start OrderStatusActivity
                            Intent intent = new Intent(PaymentActivity.this, OrderStatusActivity.class);
                            intent.putExtra("orderId", orderId); // Passing orderId to OrderStatusActivity
                            startActivity(intent);
                        })
                        .addOnFailureListener(e -> {
                            // Failed to save order
                            Toast.makeText(PaymentActivity.this, "Failed to save order: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            Log.e("PaymentActivity", "Error saving order: " + e.getMessage());
                        });

            } else {
                Toast.makeText(this, "Failed to generate order ID", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
        }
    }

    private Order createOrder(String orderId) {
        // Retrieve selected items from intent
        List<MenuItem> selectedItems = (List<MenuItem>) getIntent().getSerializableExtra("selectedItems");
        Log.d("MyActivity", "Inside Create order " + String.valueOf(selectedItems.size()));

        // Convert selected items to order items
        List<OrderItem> orderItems = new ArrayList<>();
        for (MenuItem item : selectedItems) {
            OrderItem orderItem = new OrderItem(
                    item.getItemName(),
                    item.getItemPrice(),
                    1, // Assuming quantity is always 1 for now
                    item.getItemPrice() // Total price is same as item price for now
            );
            orderItems.add(orderItem);
            Log.d("MyActivity", String.valueOf(orderItems.size()));
        }

        // Get current timestamp
        long timestamp = System.currentTimeMillis();

        // Retrieve businessId and customerId
        String businessId = getIntent().getStringExtra("BusinessId"); // Assuming this is passed via intent
        String customerId = FirebaseAuth.getInstance().getCurrentUser().getUid(); // Get current user ID

        // Create and return order object
        return new Order(orderId, businessId, customerId, timestamp, orderItems);
    }




}

