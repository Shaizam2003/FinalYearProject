package com.example.halalcheck3;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import com.example.halalcheck3.model.Review;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class CustomerReview extends AppCompatActivity {

    private EditText editTextComment;
    private RatingBar ratingBar;
    private Button buttonSubmitReview;
    private String businessId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_review);

        // Initialize views
        editTextComment = findViewById(R.id.editTextComment);
        ratingBar = findViewById(R.id.ratingBar);
        buttonSubmitReview = findViewById(R.id.buttonSubmitReview);

        // Retrieve business ID from the intent
        businessId = getIntent().getStringExtra("BUSINESS_ID");

        buttonSubmitReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitReview();
            }
        });
    }

    private void submitReview() {
        String comment = editTextComment.getText().toString().trim();
        float rating = ratingBar.getRating();

        // Validate input
        if (TextUtils.isEmpty(comment)) {
            Toast.makeText(this, "Please enter a comment.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (rating <= 0) {
            Toast.makeText(this, "Please provide a rating.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (businessId == null) {
            Toast.makeText(this, "Business ID is missing.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get the currently logged-in user
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "No user is logged in.", Toast.LENGTH_SHORT).show();
            return;
        }
        String userEmail = currentUser.getEmail(); // Retrieve user email

        // Create a new Review object with user email
        Review review = new Review(rating, comment, System.currentTimeMillis(), userEmail);

        // Get reference to Firebase Database
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                .child("businesses")
                .child(businessId)
                .child("customerReviews");

        // Generate a new review ID
        String reviewId = databaseReference.push().getKey();
        if (reviewId != null) {
            // Save the review under the generated review ID
            databaseReference.child(reviewId).setValue(review)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(CustomerReview.this, "Review submitted successfully.", Toast.LENGTH_SHORT).show();
                            finish(); // Close activity
                        } else {
                            Toast.makeText(CustomerReview.this, "Failed to submit review. Please try again.", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(this, "Failed to generate review ID. Please try again.", Toast.LENGTH_SHORT).show();
        }
    }
}
