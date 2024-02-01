package com.example.halalcheck3;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class HomePage extends AppCompatActivity {

    private TextView welcomeTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        welcomeTextView = findViewById(R.id.welcomeTextView);

        // Get the user email from the intent
        String userEmail = getIntent().getStringExtra("USER_EMAIL");

        // Display a welcome message
        String welcomeMessage = "Welcome, " + userEmail + "!";
        welcomeTextView.setText(welcomeMessage);
    }
}
