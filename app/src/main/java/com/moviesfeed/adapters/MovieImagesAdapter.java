package com.moviesfeed.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.moviesfeed.activities.uicomponents.ImageLoader;
import com.moviesfeed.R;
import com.moviesfeed.api.MoviesApi;
import com.moviesfeed.models.MovieBackdrop;
import com.squareup.picasso.Callback;

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
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.movie_detail_images_rv_item, viewGroup, false);

        MovieImagesViewHolder viewHolder = new MovieImagesViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final MovieImagesViewHolder holder, int position) {
        MovieBackdrop mb = this.listMovieImages.get(position);

        holder.progressItem.setVisibility(View.VISIBLE);
        final String url = MoviesApi.URL_MOVIE_BACKGROUND + mb.getFilePath();

        ImageLoader.loadImage(context, url, holder.imgMovieBackdrop, null, 0, 0, 0, false,
                new Callback() {
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


