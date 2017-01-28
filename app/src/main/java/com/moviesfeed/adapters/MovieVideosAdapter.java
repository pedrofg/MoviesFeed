package com.moviesfeed.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.moviesfeed.R;
import com.moviesfeed.models.Video;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Pedro on 1/22/2017.
 */

public class MovieVideosAdapter extends RecyclerView.Adapter<MovieVideosAdapter.MovieVideosViewHolder> {

    private List<Video> listVideos;
    private Context context;

    public MovieVideosAdapter(Context context, List<Video> listVideos) {
        this.context = context;
        this.listVideos = listVideos;
    }


    @Override
    public MovieVideosViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.movie_video_item, viewGroup, false);

        MovieVideosViewHolder viewHolder = new MovieVideosViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final MovieVideosViewHolder holder, int position) {
        Video video = this.listVideos.get(position);
        holder.progressItem.setVisibility(View.VISIBLE);

        loadImage(video.getYoutubeThumbnailUrl(), holder.imgMovieVideo, new Callback() {
            @Override
            public void onSuccess() {
                holder.imgMovieVideo.setVisibility(View.VISIBLE);
                holder.imgIconPlay.setVisibility(View.VISIBLE);
                holder.progressItem.setVisibility(View.GONE);
            }

            @Override
            public void onError() {
                //TODO handle error
            }
        });

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
                .resizeDimen(R.dimen.movie_video_width, R.dimen.movie_video_height)
                .centerCrop();

        if (useCache) {
            requestCreator.networkPolicy(NetworkPolicy.OFFLINE);
        }
        return requestCreator;
    }

    @Override
    public int getItemCount() {
        return (this.listVideos != null ? this.listVideos.size() : 0);
    }

    public Video getItem(int position) {
        return this.listVideos.get(position);
    }

    static class MovieVideosViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.imgMovieVideo)
        ImageView imgMovieVideo;
        @BindView(R.id.imgIconPlay)
        ImageView imgIconPlay;
        @BindView(R.id.progressMovieVideo)
        ProgressBar progressItem;

        public MovieVideosViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
