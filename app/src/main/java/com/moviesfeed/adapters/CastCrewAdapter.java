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
import com.moviesfeed.activities.uicomponents.CircularTransform;
import com.moviesfeed.api.MoviesApi;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Pedro on 2017-01-28.
 */

public class CastCrewAdapter extends RecyclerView.Adapter<CastCrewAdapter.MovieCastCrewViewHolder> {


    public static final int MAX_ROLE_LENGTH = 18;
    private Context context;
    private int listSize;

    public CastCrewAdapter(Context context, int listSize) {
        this.context = context;
        this.listSize = listSize;
    }

    @Override
    public MovieCastCrewViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.movie_cast_crew_item, viewGroup, false);

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
        this.loadImage(url, holder.imgMovieCastCrew, new Callback() {
            @Override
            public void onSuccess() {
                holder.imgMovieCastCrew.setVisibility(View.VISIBLE);
                holder.progressItem.setVisibility(View.GONE);
            }

            @Override
            public void onError() {
                holder.progressItem.setVisibility(View.GONE);
            }
        });
    }

    public String formatText(String text) {
        String formattedText = text;
        if (text.length() > MAX_ROLE_LENGTH) {
            formattedText = text.substring(0, MAX_ROLE_LENGTH) + "...";
        }
        return formattedText;
    }

    private void loadImage(final String url, final ImageView imageView, final Callback callback) {
        RequestCreator rcCache = getRequestCreator(url, 0, true);

        rcCache.into(imageView, new Callback() {
            @Override
            public void onSuccess() {
                callback.onSuccess();
            }

            @Override
            public void onError() {
                RequestCreator rcDownload = getRequestCreator(url, 0, false);
                rcDownload.into(imageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        callback.onSuccess();
                    }

                    @Override
                    public void onError() {
                        RequestCreator rcErrorImg = getRequestCreator(null, R.drawable.no_profile, false);
                        rcErrorImg.into(imageView, callback);
                    }
                });
            }
        });

    }

    private RequestCreator getRequestCreator(String url, int resourceError, boolean useCache) {
        RequestCreator requestCreator;

        if (url != null)
            requestCreator = Picasso.with(context).load(url);
        else
            requestCreator = Picasso.with(context).load(resourceError);

        requestCreator.resizeDimen(R.dimen.movie_cast_width, R.dimen.movie_cast_height);
        requestCreator.transform(new CircularTransform());

        if (useCache) {
            requestCreator.networkPolicy(NetworkPolicy.OFFLINE);
        }
        return requestCreator;
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
