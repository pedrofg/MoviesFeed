package com.moviesfeed.adapters;

import android.app.Activity;
import android.content.Context;

import com.moviesfeed.models.Crew;

import java.util.List;

/**
 * Created by Pedro on 2017-01-28.
 */
public class MovieCrewAdapter extends CastCrewAdapter {

    private List<Crew> listCrew;

    public MovieCrewAdapter(Activity activity, List<Crew> listCrew) {
        super(activity, listCrew.size());
        this.listCrew = listCrew;
    }

    @Override
    public void onBindViewHolder(final CastCrewAdapter.MovieCastCrewViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);

        Crew crew = this.listCrew.get(position);

        holder.txtMovieCastName.setText(formatTitle(crew.getName()));
        holder.txtMovieCastAs.setText(formatSubTitle(crew.getJob()));

        loadImage(crew.getProfilePath(), holder);
    }

}
