package com.example.halalcheck3.DeliveryDriverSide;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.halalcheck3.R;

public class DriverHomePage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drive_home_page);

        Button pickOrders = findViewById(R.id.pickOrders);
        Button viewAssignedOrders = findViewById(R.id.viewAssignedOrders);

        pickOrders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open RestaurantMenuActivity
                Intent intent = new Intent(DriverHomePage.this, PickOrders.class);
                startActivity(intent);
            }
        });

        viewAssignedOrders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open RestaurantMenuActivity
                Intent intent = new Intent(DriverHomePage.this, DriverViewAssignedOrders.class);
                startActivity(intent);
            }
        });



    }
}