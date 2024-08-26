package com.example.halalcheck3.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.halalcheck3.R;
import com.example.halalcheck3.model.Review;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {

    private List<Review> reviewList;

    public ReviewAdapter(List<Review> reviewList) {
        this.reviewList = reviewList;
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_review, parent, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        Review review = reviewList.get(position);
        holder.bind(review);
    }

    @Override
    public int getItemCount() {
        return reviewList.size();
    }

    public void setReviews(List<Review> reviews) {
        this.reviewList = reviews;
        notifyDataSetChanged();
    }

    // Static inner class for ViewHolder
    public static class ReviewViewHolder extends RecyclerView.ViewHolder {

        private TextView textViewRating;
        private TextView textViewComment;
        private TextView textViewTimestamp;
        private TextView textViewUserEmail;

        public ReviewViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewRating = itemView.findViewById(R.id.textViewRating);
            textViewComment = itemView.findViewById(R.id.textViewComment);
            textViewTimestamp = itemView.findViewById(R.id.textViewTimestamp);
            textViewUserEmail = itemView.findViewById(R.id.textViewUserEmail);
        }

        public void bind(Review review) {
            textViewRating.setText("Rating: " + review.getRating());
            textViewComment.setText("Comment: " + review.getComment());
            textViewTimestamp.setText("Timestamp: " + new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(new Date(review.getTimestamp())));
            textViewUserEmail.setText("User: " + review.getUserEmail());
        }
    }
}
