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
import com.moviesfeed.ui.components.ImageLoader;
import com.moviesfeed.models.Video;

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
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.movie_detail_video_rv_item, viewGroup, false);

        MovieVideosViewHolder viewHolder = new MovieVideosViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final MovieVideosViewHolder holder, int position) {
        Video video = this.listVideos.get(position);

        ImageLoader.loadImageGlide(context, video.getYoutubeThumbnailUrl(), holder.imgMovieVideo, null, 0, true, true, new RequestListener() {
            @Override
            public boolean onException(Exception e, Object model, Target target, boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(Object resource, Object model, Target target, boolean isFromMemoryCache, boolean isFirstResource) {
                holder.imgMovieVideo.setVisibility(View.VISIBLE);
                holder.imgIconPlay.setVisibility(View.VISIBLE);
                holder.progressItem.setVisibility(View.GONE);
                return false;
            }
        });
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
