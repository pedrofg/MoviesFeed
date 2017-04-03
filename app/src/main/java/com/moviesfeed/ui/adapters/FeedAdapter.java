package com.moviesfeed.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.moviesfeed.R;
import com.moviesfeed.api.MoviesApi;
import com.moviesfeed.models.Movie;
import com.moviesfeed.ui.activities.uicomponents.ImageLoader;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class FeedAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    public static final int VIEW_PROGRESS = 0;
    public static final int VIEW_ITEM = 1;
    private boolean isErrorProgress;
    private List<Movie> listMovies;
    private OnFeedItemClicked onFeedItemClicked;

    public interface OnFeedItemClicked {
        void onFeedItemClicked(Movie movie);

        void onUpdateBtnClicked();
    }


    public FeedAdapter(Context context, OnFeedItemClicked onFeedItemClicked) {
        this.context = context;
        this.onFeedItemClicked = onFeedItemClicked;
    }

    @Override
    public int getItemViewType(int position) {
        return getItem(position) != null ? VIEW_ITEM : VIEW_PROGRESS;
    }

    public void setMovies(List<Movie> listMovies) {
        this.listMovies = listMovies;
    }

    public Movie getItem(int position) {
        return this.listMovies.get(position);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        if (viewType == VIEW_ITEM) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.feed_item, parent, false);

            vh = new FeedViewHolder(v);
        } else {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.progress_item, parent, false);

            vh = new ProgressViewHolder(v);

            v.setOnFocusChangeListener((view, hasFocus) -> {
                if (hasFocus) {
                    ((ProgressViewHolder) vh).btnProgressUpdate.setImageDrawable(context.getDrawable(R.drawable.ic_update_clicked));
                } else {
                    ((ProgressViewHolder) vh).btnProgressUpdate.setImageDrawable(context.getDrawable(R.drawable.ic_update));
                }
            });
        }
        return vh;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof FeedViewHolder) {
            FeedViewHolder feedViewHolder = (FeedViewHolder) holder;

            Movie movie = this.listMovies.get(position);

            feedViewHolder.itemView.setOnClickListener(v -> {
                this.onFeedItemClicked.onFeedItemClicked(movie);
            });

            final String url = MoviesApi.URL_MOVIE_POSTER + movie.getPosterPath();

            feedViewHolder.layoutPlaceHolder.setVisibility(View.VISIBLE);
            feedViewHolder.imgMoviePoster.setVisibility(View.GONE);


            ImageLoader.loadImageGlide(context, url, ((FeedViewHolder) holder).imgMoviePoster, new RoundedCornersTransformation(context, 10, 0), 0, false, true, new RequestListener() {
                @Override
                public boolean onException(Exception e, Object model, Target target, boolean isFirstResource) {
                    return false;
                }

                @Override
                public boolean onResourceReady(Object resource, Object model, Target target, boolean isFromMemoryCache, boolean isFirstResource) {
                    feedViewHolder.layoutPlaceHolder.setVisibility(View.GONE);
                    feedViewHolder.imgMoviePoster.setVisibility(View.VISIBLE);
                    return false;
                }
            });

        } else {

            ProgressViewHolder progressViewHolder = (ProgressViewHolder) holder;
            if (this.isErrorProgress) {
                progressViewHolder.progressBar.setVisibility(View.GONE);
                progressViewHolder.btnProgressUpdate.setVisibility(View.VISIBLE);

                progressViewHolder.btnProgressUpdate.setOnClickListener(v -> {
                    this.onFeedItemClicked.onUpdateBtnClicked();
                });

            } else {
                progressViewHolder.progressBar.setVisibility(View.VISIBLE);
                progressViewHolder.btnProgressUpdate.setVisibility(View.GONE);
            }
        }

    }

    @Override
    public int getItemCount() {
        return this.listMovies != null ? this.listMovies.size() : 0;
    }


    public void addProgress(boolean errorProgress) {
        if (this.listMovies != null) {
            this.isErrorProgress = errorProgress;
            this.listMovies.add(null);
            notifyItemInserted(getItemCount() - 1);
        }
    }

    public void removeProgress() {
        if (this.listMovies != null && this.listMovies.size() > 0) {
            if (getItem(getItemCount() - 1) == null) {
                this.listMovies.remove(getItemCount() - 1);
                notifyItemRemoved(getItemCount());
            }
        }
    }


    static class FeedViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.movieItemPoster)
        ImageView imgMoviePoster;
        @BindView(R.id.layoutPlaceHolder)
        View layoutPlaceHolder;

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
