package com.moviesfeed.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.moviesfeed.R;
import com.moviesfeed.api.MoviesApi;
import com.moviesfeed.models.MovieBackdrop;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Pedro on 8/28/2016.
 */
public class MovieImagesAdapter extends RecyclerView.Adapter<MovieImagesAdapter.MovieImagesViewHolder> {

    private List<MovieBackdrop> listMovieImages;
    private Context context;

    public MovieImagesAdapter(Context context, List<MovieBackdrop> listMovieBackdrop) {
        this.context = context;
        this.listMovieImages = listMovieBackdrop;
    }

    @Override
    public MovieImagesViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.movie_details_images_rv_item, viewGroup, false);

        MovieImagesViewHolder viewHolder = new MovieImagesViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final MovieImagesViewHolder holder, int position) {
        MovieBackdrop mb = this.listMovieImages.get(position);

        holder.progressItem.setVisibility(View.VISIBLE);
        final String url = MoviesApi.URL_MOVIE_BACKGROUND + mb.getFilePath();
        loadImage(url, holder.imgMovieBackdrop, new Callback() {
            @Override
            public void onSuccess() {
                holder.progressItem.setVisibility(View.GONE);
            }

            @Override
            public void onError() {
                //TODO handle error
            }
        });

        holder.txtPosition.setText(++position + " - " + this.listMovieImages.size());

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
                .load(url);

        if (useCache) {
            requestCreator.networkPolicy(NetworkPolicy.OFFLINE);
        }
        return requestCreator;
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
        @BindView(R.id.txtPosition)
        TextView txtPosition;


        public MovieImagesViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}


