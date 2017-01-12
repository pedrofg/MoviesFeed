package com.moviesfeed.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.moviesfeed.R;
import com.moviesfeed.activities.uicomponents.CircleTransform;
import com.moviesfeed.api.MoviesApi;
import com.moviesfeed.models.Movie;
import com.moviesfeed.models.MoviesFeed;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.FeedViewHolder> {

    private Context context;
    private MoviesFeed moviesFeed;

    public FeedAdapter(Context context) {
        this.context = context;
    }

    public void setMoviesFeed(MoviesFeed moviesFeed) {
        this.moviesFeed = moviesFeed;
    }

    public Movie getItem(int position) {
        return this.moviesFeed.getMovies().get(position);
    }

    @Override
    public FeedViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_movies_feed_item, parent, false);

        FeedViewHolder viewHolder = new FeedViewHolder(view);
        return viewHolder;
    }

    @Override
    public int getItemCount() {
        return moviesFeed != null ? moviesFeed.getMovies().size() : 0;
    }

    @Override
    public void onBindViewHolder(final FeedViewHolder holder, int position) {
        holder.progressItem.setVisibility(View.VISIBLE);
        String url = MoviesApi.URL_MOVIE_POSTER + moviesFeed.getMovies().get(position).getPosterPath();
        Picasso.with(context)
                .load(url)
                .resizeDimen(R.dimen.movie_thumbnail_width, R.dimen.movie_thumbnail_height)
                .transform(new CircleTransform(20, 0))
                .into(holder.imgMoviePoster, new Callback() {
                    @Override
                    public void onSuccess() {
                        holder.progressItem.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError() {

                    }
                });
    }

    static class FeedViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.movieItemPoster)
        ImageView imgMoviePoster;
        @BindView(R.id.movieItemProgressBar)
        ProgressBar progressItem;

        public FeedViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
