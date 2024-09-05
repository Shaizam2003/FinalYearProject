package com.example.halalcheck3.BusinessSide;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Toast;

import com.example.halalcheck3.R;
import com.example.halalcheck3.adapter.ReviewAdapter;
import com.example.halalcheck3.model.Review;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ViewReviews extends AppCompatActivity {

    private RecyclerView recyclerViewReviews;
    private ReviewAdapter reviewAdapter;
    private List<Review> reviewList = new ArrayList<>();
    private DatabaseReference reviewsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_reviews);

        // Initialize RecyclerView and Adapter
        recyclerViewReviews = findViewById(R.id.recyclerViewReviews);
        recyclerViewReviews.setLayoutManager(new LinearLayoutManager(this));
        reviewAdapter = new ReviewAdapter(reviewList);
        recyclerViewReviews.setAdapter(reviewAdapter);

        // Add a divider line between items
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        recyclerViewReviews.addItemDecoration(dividerItemDecoration);

        // Get current user's email
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "No user is logged in.", Toast.LENGTH_SHORT).show();
            return;
        }

        String userEmail = currentUser.getEmail();
        if (userEmail == null) {
            Toast.makeText(this, "User email not found.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Fetch the business ID by email
        fetchBusinessIdByEmail(userEmail);
    }

    private void fetchBusinessIdByEmail(String email) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("businesses");

        // Query businesses by email
        databaseReference.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String businessId = snapshot.getKey();
                        if (businessId != null) {
                            // Reference to the customerReviews node for the business
                            reviewsRef = FirebaseDatabase.getInstance().getReference()
                                    .child("businesses")
                                    .child(businessId)
                                    .child("customerReviews");

                            // Fetch and display reviews
                            reviewsRef.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    reviewList.clear();
                                    for (DataSnapshot reviewSnapshot : snapshot.getChildren()) {
                                        Review review = reviewSnapshot.getValue(Review.class);
                                        if (review != null) {
                                            reviewList.add(review);
                                        }
                                    }
                                    reviewAdapter.notifyDataSetChanged(); // Notify adapter of data changes
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Toast.makeText(ViewReviews.this, "Failed to load reviews.", Toast.LENGTH_SHORT).show();
                                }
                            });
                            return; // Exit the loop once the matching business is found
                        }
                    }
                } else {
                    Toast.makeText(ViewReviews.this, "No business found for the logged-in user.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ViewReviews.this, "Failed to load business data.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
