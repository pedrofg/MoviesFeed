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
import com.moviesfeed.models.MovieBackdrop;
import com.moviesfeed.ui.components.ImageLoader;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Pedro on 8/28/2016.
 */
public class MovieImagesAdapter extends RecyclerView.Adapter<MovieImagesAdapter.MovieImagesViewHolder> {

    private List<MovieBackdrop> listMovieImages;
    private Context context;
    private ImagesAdapterCallback callback;

    public interface ImagesAdapterCallback {
        void onImageViewClicked(ImageView imageView);
    }

    public MovieImagesAdapter(Context context, List<MovieBackdrop> listMovieBackdrop, ImagesAdapterCallback callback) {
        this.context = context;
        this.listMovieImages = listMovieBackdrop;
        this.callback = callback;
    }

    @Override
    public MovieImagesViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.movie_detail_images_rv_item, viewGroup, false);

        MovieImagesViewHolder viewHolder = new MovieImagesViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final MovieImagesViewHolder holder, int position) {
        MovieBackdrop mb = this.listMovieImages.get(position);

        holder.progressItem.setVisibility(View.VISIBLE);
        final String url = MoviesApi.URL_MOVIE_BACKGROUND + mb.getFilePath();

        holder.imgMovieBackdrop.setOnClickListener(v -> callback.onImageViewClicked((ImageView) v));


        ImageLoader.loadImageGlide(context, url, holder.imgMovieBackdrop, null, 0, false, true, new RequestListener() {
            @Override
            public boolean onException(Exception e, Object model, Target target, boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(Object resource, Object model, Target target, boolean isFromMemoryCache, boolean isFirstResource) {
                holder.progressItem.setVisibility(View.GONE);
                return false;
            }
        });

    }


    @Override
    public int getItemCount() {
        return (this.listMovieImages != null ? this.listMovieImages.size() : 0);
    }


    static class MovieImagesViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.imgMovieBackdrop)
        ImageView imgMovieBackdrop;
        @BindView(R.id.progressMovieBackdrop)
        ProgressBar progressItem;


        public MovieImagesViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}


