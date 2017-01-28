package com.moviesfeed.adapters;

import android.content.Context;

import com.moviesfeed.R;
import com.moviesfeed.models.Cast;

import java.util.List;

/**
 * Created by Pedro on 2017-01-28.
 */
public class MovieCastAdapter extends CastCrewAdapter {

    private List<Cast> listCast;
    private Context context;

    public MovieCastAdapter(Context context, List<Cast> listCast) {
        super(context, listCast.size());
        this.context = context;
        this.listCast = listCast;
    }

    @Override
    public void onBindViewHolder(final CastCrewAdapter.MovieCastCrewViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);

        Cast cast = this.listCast.get(position);

        holder.txtMovieCastName.setText(cast.getName());

        holder.txtMovieCastAs.setText(context.getString(R.string.as) + formatText(cast.getCharacter()));

        loadImage(cast.getProfilePath(), holder);
    }

}
