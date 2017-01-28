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
import com.moviesfeed.models.Cast;
import com.moviesfeed.models.Crew;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Pedro on 2017-01-28.
 */
public class MovieCrewAdapter extends CastCrewAdapter {

    private List<Crew> listCrew;

    public MovieCrewAdapter(Context context, List<Crew> listCrew) {
        super(context, listCrew.size());
        this.listCrew = listCrew;
    }

    @Override
    public void onBindViewHolder(final CastCrewAdapter.MovieCastCrewViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);

        Crew crew = this.listCrew.get(position);

        holder.txtMovieCastName.setText(crew.getName());
        holder.txtMovieCastAs.setText(formatText(crew.getJob()));

        loadImage(crew.getProfilePath(), holder);
    }

}
