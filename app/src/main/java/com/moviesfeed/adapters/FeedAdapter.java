package com.moviesfeed.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.moviesfeed.R;
import com.moviesfeed.activities.uicomponents.BorderTransform;
import com.moviesfeed.api.MoviesApi;
import com.moviesfeed.models.Movie;
import com.moviesfeed.models.MoviesFeed;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FeedAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private MoviesFeed moviesFeed;
    public static final int VIEW_PROGRESS = 0;
    public static final int VIEW_ITEM = 1;
    private boolean isErrorProgress;


    public FeedAdapter(Context context) {
        this.context = context;
    }

    public MoviesFeed getMoviesFeed() {
        return this.moviesFeed;
    }

    @Override
    public int getItemViewType(int position) {
        return getItem(position) != null ? VIEW_ITEM : VIEW_PROGRESS;
    }

    public void setMoviesFeed(MoviesFeed moviesFeed) {
        this.moviesFeed = moviesFeed;
    }

    public Movie getItem(int position) {
        return this.moviesFeed.getMovies().get(position);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        if (viewType == VIEW_ITEM) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.grid_movies_feed_item, parent, false);

            vh = new FeedViewHolder(v);
        } else {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.progress_item, parent, false);

            vh = new ProgressViewHolder(v);
        }
        return vh;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof FeedViewHolder) {
            ((FeedViewHolder) holder).progressItem.setVisibility(View.VISIBLE);
            final String url = MoviesApi.URL_MOVIE_POSTER + moviesFeed.getMovies().get(position).getPosterPath();

            loadImage(url, ((FeedViewHolder) holder).imgMoviePoster, new Callback() {
                @Override
                public void onSuccess() {
                    ((FeedViewHolder) holder).progressItem.setVisibility(View.GONE);
                }

                @Override
                public void onError() {
                    //TODO handle error
                }
            });
        } else {
            if (this.isErrorProgress) {
                ((ProgressViewHolder) holder).progressBar.setVisibility(View.GONE);
                ((ProgressViewHolder) holder).btnProgressUpdate.setVisibility(View.VISIBLE);
            } else {
                ((ProgressViewHolder) holder).progressBar.setVisibility(View.VISIBLE);
                ((ProgressViewHolder) holder).btnProgressUpdate.setVisibility(View.GONE);
            }
        }

    }

    @Override
    public int getItemCount() {
        return moviesFeed != null ? moviesFeed.getMovies().size() : 0;
    }

    private void loadImage(final String url, final ImageView imageView, final Callback callback) {

        getRequestCreator(url, true).into(imageView, new Callback() {
            @Override
            public void onSuccess() {
                callback.onSuccess();
            }

            @Override
            public void onError() {
                getRequestCreator(url, false).into(imageView, callback);
            }
        });

    }

    private RequestCreator getRequestCreator(String url, boolean useCache) {
        RequestCreator requestCreator = Picasso.with(context)
                .load(url)
                .resizeDimen(R.dimen.movie_thumbnail_width, R.dimen.movie_thumbnail_height)
                .transform(new BorderTransform(20, 0));

        if (useCache) {
            requestCreator.networkPolicy(NetworkPolicy.OFFLINE);
        }
        return requestCreator;
    }

    public void addProgress(boolean errorProgress) {
        this.isErrorProgress = errorProgress;
        this.moviesFeed.getMovies().add(null);
        notifyItemInserted(getItemCount() - 1);
    }

    public void removeProgress() {
        if (getItem(getItemCount() - 1) == null) {
            this.moviesFeed.getMovies().remove(getItemCount() - 1);
            notifyItemRemoved(getItemCount());
        }
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

    static class ProgressViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.itemProgressLoading)
        ProgressBar progressBar;
        @BindView(R.id.itemProgressUpdateBtn)
        ImageView btnProgressUpdate;

        public ProgressViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }


}
