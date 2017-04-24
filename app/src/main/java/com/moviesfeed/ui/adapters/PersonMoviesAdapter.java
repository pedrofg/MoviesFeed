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
import com.moviesfeed.models.persondetails.PersonCreditsScreen;
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
    private List<PersonCreditsScreen> personCreditsScreenList;
    private OnPersonMovieItemClicked onPersonMovieItemClicked;

    public PersonMoviesAdapter(Context context, List<PersonCreditsScreen> personCreditsScreenList, OnPersonMovieItemClicked onPersonMovieItemClicked) {
        this.context = context;
        this.personCreditsScreenList = personCreditsScreenList;
        this.onPersonMovieItemClicked = onPersonMovieItemClicked;
    }

    public interface OnPersonMovieItemClicked {
        void onPersonMovieItemClicked(int movieID);
    }


    @Override
    public PersonMoviesViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.person_movie_item, viewGroup, false);

        PersonMoviesViewHolder viewHolder = new PersonMoviesViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(PersonMoviesViewHolder holder, int position) {
        PersonCreditsScreen personCreditsScreen = this.personCreditsScreenList.get(position);

        String url = MoviesApi.URL_MOVIE_POSTER + personCreditsScreen.getPosterPath();
        int id = personCreditsScreen.getIdTmdb();
        String date = "";
        if (!TextUtils.isEmpty(personCreditsScreen.getReleaseDate()))
            date = personCreditsScreen.getReleaseDate().substring(0, 4);

        String job = "";
        if (personCreditsScreen.getKnownForCharacter())
            job = context.getString(R.string.as);

        job += personCreditsScreen.getKnownFor();

        holder.txtJob.setMaxLines(MAX_TEXT_LINE);
        holder.txtJob.setText(job);
        holder.txtDate.setText(date);


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
        return this.personCreditsScreenList.size();
    }


    public class PersonMoviesViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.movieItemPoster)
        ImageView imgMoviePoster;
        @BindView(R.id.layoutPlaceHolder)
        View layoutPlaceHolder;
        @BindView(R.id.txtPersonMovieAs)
        TextView txtJob;
        @BindView(R.id.txtPersonMovieDate)
        TextView txtDate;


        public PersonMoviesViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
