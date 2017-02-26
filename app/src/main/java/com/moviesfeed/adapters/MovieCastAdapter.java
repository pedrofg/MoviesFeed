package com.moviesfeed.adapters;

import android.app.Activity;
import android.content.Context;

import com.moviesfeed.R;
import com.moviesfeed.models.Cast;

import java.util.List;

/**
 * Created by Pedro on 2017-01-28.
 */
public class MovieCastAdapter extends CastCrewAdapter {

    private List<Cast> listCast;
    private Activity activity;

    public MovieCastAdapter(Activity activity, List<Cast> listCast) {
        super(activity, listCast.size());
        this.activity = activity;
        this.listCast = listCast;
    }

    @Override
    public void onBindViewHolder(final CastCrewAdapter.MovieCastCrewViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);

        Cast cast = this.listCast.get(position);

        holder.txtMovieCastName.setText(formatTitle(cast.getName()));

        holder.txtMovieCastAs.setText(activity.getString(R.string.as) + formatSubTitle(cast.getCharacter()));

        loadImage(cast.getProfilePath(), holder);
    }

}
