package com.moviesfeed.adapters;

import android.content.Context;

import com.moviesfeed.models.Crew;

import java.util.List;

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

        holder.txtMovieCastName.setText(formatTitle(crew.getName()));
        holder.txtMovieCastAs.setText(formatSubTitle(crew.getJob()));

        loadImage(crew.getProfilePath(), holder);
    }

}
