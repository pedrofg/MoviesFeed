package com.moviesfeed.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.moviesfeed.R;
import com.moviesfeed.api.MoviesApi;
import com.moviesfeed.models.persondetails.PersonCast;
import com.moviesfeed.models.persondetails.PersonCrew;
import com.moviesfeed.ui.components.ImageLoader;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

/**
 * Created by Pedro on 2017-01-28.
 */

public class PersonMoviesAdapter extends RecyclerView.Adapter<PersonMoviesAdapter.PersonMoviesViewHolder> {


    public static final int MAX_TEXT_LINE = 2;
    private Context context;
    private List<PersonCast> personCastList;
    private List<PersonCrew> personCrewList;
    private OnPersonMovieItemClicked onPersonMovieItemClicked;

    public interface OnPersonMovieItemClicked {
        void onPersonMovieItemClicked(int movieID);
    }

    public PersonMoviesAdapter(Context context, List<PersonCast> personCastList, List<PersonCrew> personCrewList, OnPersonMovieItemClicked onPersonMovieItemClicked) {
        this.context = context;
        this.personCastList = personCastList;
        this.personCrewList = personCrewList;
        this.onPersonMovieItemClicked = onPersonMovieItemClicked;
    }


    @Override
    public PersonMoviesViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.person_movie_item, viewGroup, false);

        PersonMoviesViewHolder viewHolder = new PersonMoviesViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(PersonMoviesViewHolder holder, int position) {
        String job = "";
        String url = "";
        int id;
        //position + 1 because list.size()
        if (this.personCastList.size() >= position + 1) {
            PersonCast cast = this.personCastList.get(position);

            if (!TextUtils.isEmpty(cast.getCharacter()))
                job = context.getString(R.string.as) + cast.getCharacter();

            url = cast.getPosterPath();
            id = cast.getIdTmdb();
        } else {
            //- listCast.size() to position starts from the beginning of listCrew.
            PersonCrew crew = this.personCrewList.get(position - this.personCastList.size());

            job = crew.getJob();
            url = crew.getPosterPath();
            id = crew.getIdTmdb();
        }

        url = MoviesApi.URL_MOVIE_POSTER + url;

        holder.txtJob.setMaxLines(MAX_TEXT_LINE);
        holder.txtJob.setText(job);


        holder.layoutPlaceHolder.setVisibility(View.VISIBLE);
        holder.imgMoviePoster.setVisibility(View.GONE);

        loadThumbnail(holder, url);

        holder.itemView.setOnClickListener(v -> {
            this.onPersonMovieItemClicked.onPersonMovieItemClicked(id);
        });
    }

    private void loadThumbnail(PersonMoviesViewHolder holder, String url) {
        ImageLoader.loadImageGlide(context, url, holder.imgMoviePoster, new RoundedCornersTransformation(context, 10, 0), 0, false, true, new RequestListener() {
            @Override
            public boolean onException(Exception e, Object model, Target target, boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(Object resource, Object model, Target target, boolean isFromMemoryCache, boolean isFirstResource) {
                holder.layoutPlaceHolder.setVisibility(View.GONE);
                holder.imgMoviePoster.setVisibility(View.VISIBLE);
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.personCastList.size() + this.personCrewList.size();
    }


    public class PersonMoviesViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.movieItemPoster)
        ImageView imgMoviePoster;
        @BindView(R.id.layoutPlaceHolder)
        View layoutPlaceHolder;
        @BindView(R.id.txtPersonMovieAs)
        TextView txtJob;


        public PersonMoviesViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
