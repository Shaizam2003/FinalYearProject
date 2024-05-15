package com.example.halalcheck3;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class BusinessHomePage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_home_page);

        Button viewMenuButton = findViewById(R.id.viewMenuButton);
        Button updateMenuButton = findViewById(R.id.updateMenuButton);
        Button viewOrdersButton = findViewById(R.id.viewOrdersButton);

        viewMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open RestaurantMenuActivity
                Intent intent = new Intent(BusinessHomePage.this, RestaurantMenuActivity.class);
                startActivity(intent);
            }
        });

        updateMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open Update Menu Activity
                Intent intent = new Intent(BusinessHomePage.this, UpdateMenuActivity.class);
                startActivity(intent);
            }
        });

        viewOrdersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open Orders Activity
                Intent intent = new Intent(BusinessHomePage.this, ViewOrders.class);
                startActivity(intent);
            }
        });
    }
}
