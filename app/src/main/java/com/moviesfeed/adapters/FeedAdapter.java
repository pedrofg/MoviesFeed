package com.moviesfeed.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.moviesfeed.R;
import com.moviesfeed.api.MoviesApi;
import com.moviesfeed.models.Movie;
import com.moviesfeed.models.MoviesFeed;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Pedro on 8/17/2016.
 */
public class FeedAdapter extends BaseAdapter {

    private Context context;
    private MoviesFeed moviesFeed;

    public FeedAdapter(Context context) {
        this.context = context;
    }

    public void setMoviesFeed(MoviesFeed moviesFeed) {
        this.moviesFeed = moviesFeed;
    }

    @Override
    public int getCount() {
        return moviesFeed != null ? moviesFeed.getMovies().size() : 0;
    }

    @Override
    public Movie getItem(int i) {
        return moviesFeed != null ? moviesFeed.getMovies().get(i) : null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.grid_movies_feed_item, parent, false);

            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.progressItem.setVisibility(View.VISIBLE);
        String url = MoviesApi.URL_MOVIE_POSTER + moviesFeed.getMovies().get(position).getPosterPath();
        Picasso.with(context)
                .load(url)
                .resizeDimen(R.dimen.movie_thumbnail_width, R.dimen.movie_thumbnail_height)
                .into(holder.imgMoviePoster, new Callback() {
                    @Override
                    public void onSuccess() {
                        holder.progressItem.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError() {

                    }
                });

        return convertView;
    }

    static class ViewHolder {
        @BindView(R.id.movieItemPoster)
        ImageView imgMoviePoster;
        @BindView(R.id.movieItemProgressBar)
        ProgressBar progressItem;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
