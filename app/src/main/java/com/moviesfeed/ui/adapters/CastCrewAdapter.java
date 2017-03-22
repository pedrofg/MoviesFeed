package com.moviesfeed.ui.adapters;

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
import com.moviesfeed.ui.activities.uicomponents.ImageLoader;
import com.moviesfeed.R;
import com.moviesfeed.api.MoviesApi;
import com.moviesfeed.models.Cast;
import com.moviesfeed.models.Crew;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Created by Pedro on 2017-01-28.
 */

public class CastCrewAdapter extends RecyclerView.Adapter<CastCrewAdapter.MovieCastCrewViewHolder> {


    public static final int MAX_TEXT_LINE = 2;
    private Activity activity;
    private List<Cast> listCast;
    private List<Crew> listCrew;

    public CastCrewAdapter(Activity activity, List<Cast> listCast, List<Crew> listcrew) {
        this.activity = activity;
        this.listCast = listCast;
        this.listCrew = listcrew;
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

        String title = "";
        String subTitle = "";
        String url = "";

        //position + 1 because list.size()
        if (this.listCast.size() >= position + 1) {
            Cast cast = this.listCast.get(position);
            title = cast.getName();
            subTitle = activity.getString(R.string.as) + cast.getCharacter();
            url = cast.getProfilePath();
        } else {
            //- listCast.size() to position starts from the beginning of listCrew.
            Crew crew = this.listCrew.get(position - this.listCast.size());

            title = crew.getName();
            subTitle = crew.getJob();
            url = crew.getProfilePath();
        }

        holder.txtMovieCastName.setMaxLines(MAX_TEXT_LINE);
        holder.txtMovieCastAs.setMaxLines(MAX_TEXT_LINE);

        holder.txtMovieCastName.setText(title);
        holder.txtMovieCastAs.setText(subTitle);

        loadImage(url, holder);
    }

    @Override
    public int getItemCount() {
        return this.listCast.size() + this.listCrew.size();
    }


    public void loadImage(String path, final MovieCastCrewViewHolder holder) {
        final String url = MoviesApi.URL_MOVIE_POSTER + path;

        ImageLoader.loadImageGlide(activity, url, holder.imgMovieCastCrew, new CropCircleTransformation(activity), R.drawable.no_profile, false, false, new RequestListener() {
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