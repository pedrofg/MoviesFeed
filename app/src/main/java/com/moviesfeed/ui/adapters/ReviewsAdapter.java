package com.moviesfeed.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.moviesfeed.R;
import com.moviesfeed.models.Review;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Pedro on 2017-02-16.
 */

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ReviewsViewHolder> {

    private List<Review> listReviews;
    private Context context;
    public static final int MAX_CONTENT_LINES = 6;

    public ReviewsAdapter(Context context, List<Review> listReviews) {
        this.context = context;
        this.listReviews = listReviews;
    }

    @Override
    public ReviewsAdapter.ReviewsViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.movie_detail_review_rv_item, viewGroup, false);

        ReviewsAdapter.ReviewsViewHolder viewHolder = new ReviewsAdapter.ReviewsViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ReviewsAdapter.ReviewsViewHolder holder, int position) {
        Review review = this.listReviews.get(position);

        holder.txtReviewAuthor.setText(review.getAuthor());
        holder.txtReviewContent.setText(review.getContent());
        holder.txtReviewContent.setMaxLines(MAX_CONTENT_LINES);
    }


    public Review getItem(int position) {
        return this.listReviews.get(position);
    }


    @Override
    public int getItemCount() {
        return (this.listReviews != null ? this.listReviews.size() : 0);
    }

    static class ReviewsViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.txtReviewAuthor)
        TextView txtReviewAuthor;
        @BindView(R.id.txtReviewContent)
        TextView txtReviewContent;

        public ReviewsViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
