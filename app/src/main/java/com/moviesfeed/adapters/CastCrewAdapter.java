package com.moviesfeed.adapters;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.moviesfeed.activities.uicomponents.ImageLoader;
import com.moviesfeed.R;
import com.moviesfeed.api.MoviesApi;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Created by Pedro on 2017-01-28.
 */

public abstract class CastCrewAdapter extends RecyclerView.Adapter<CastCrewAdapter.MovieCastCrewViewHolder> {


    public static final int MAX_TITLE_LENGTH = 16;
    public static final int MAX_SUB_TITLE_LENGTH = 16;
    private Activity activity;
    private int listSize;

    public CastCrewAdapter(Activity activity, int listSize) {
        this.activity = activity;
        this.listSize = listSize;
    }

    @Override
    public MovieCastCrewViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.movie_detail_cast_crew_rv_item, viewGroup, false);

        MovieCastCrewViewHolder viewHolder = new MovieCastCrewViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MovieCastCrewViewHolder holder, int position) {
        holder.progressItem.setVisibility(View.VISIBLE);

    }

    @Override
    public int getItemCount() {
        return this.listSize;
    }

    public void loadImage(String path, final MovieCastCrewViewHolder holder) {
        final String url = MoviesApi.URL_MOVIE_POSTER + path;

        ImageLoader.loadImageGlide(activity, url, holder.imgMovieCastCrew, new CropCircleTransformation(activity), R.drawable.no_profile, false, new RequestListener() {
            @Override
            public boolean onException(Exception e, Object model, Target target, boolean isFirstResource) {
                showImageLayout(holder);
                return false;
            }

            @Override
            public boolean onResourceReady(Object resource, Object model, Target target, boolean isFromMemoryCache, boolean isFirstResource) {
                showImageLayout(holder);
                return false;
            }
        });
    }

    private void showImageLayout(MovieCastCrewViewHolder holder) {
        holder.imgMovieCastCrew.setVisibility(View.VISIBLE);
        holder.progressItem.setVisibility(View.GONE);
    }

    private String ellipsizeText(String text, int maxLenght) {
        if (text.length() > maxLenght) {
            return text.substring(0, maxLenght) + "...";
        } else {
            return text;
        }
    }

    public String formatTitle(String text) {
        return ellipsizeText(text, MAX_TITLE_LENGTH);
    }

    public String formatSubTitle(String text) {
        return ellipsizeText(text, MAX_SUB_TITLE_LENGTH);
    }


    static class MovieCastCrewViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.imgMovieCastCrew)
        ImageView imgMovieCastCrew;
        @BindView(R.id.progressMovieCastCrew)
        ProgressBar progressItem;
        @BindView(R.id.txtMovieCastCrewName)
        TextView txtMovieCastName;
        @BindView(R.id.txtMovieCastCrewAs)
        TextView txtMovieCastAs;

        public MovieCastCrewViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
