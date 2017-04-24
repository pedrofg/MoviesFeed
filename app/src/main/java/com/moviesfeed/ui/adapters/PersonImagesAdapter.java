package com.moviesfeed.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.moviesfeed.R;
import com.moviesfeed.api.MoviesApi;
import com.moviesfeed.models.persondetails.PersonImage;
import com.moviesfeed.ui.components.ImageLoader;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Pedro on 2017-04-16.
 */

public class PersonImagesAdapter extends RecyclerView.Adapter<PersonImagesAdapter.PersonImagesViewHolder> {

    private List<PersonImage> personImageList;
    private Context context;
    private PersonImagesAdapterCallback callback;

    public interface PersonImagesAdapterCallback {
        void onImageViewClicked(ImageView imageView);
    }

    public PersonImagesAdapter(Context context, List<PersonImage> personImageList, PersonImagesAdapterCallback callback) {
        this.context = context;
        this.personImageList = personImageList;
        this.callback = callback;
    }

    @Override
    public PersonImagesViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.movie_detail_images_rv_item, viewGroup, false);

        PersonImagesViewHolder viewHolder = new PersonImagesViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(PersonImagesViewHolder holder, int position) {
        PersonImage personImage = this.personImageList.get(position);

        holder.progressItem.setVisibility(View.VISIBLE);
        final String url = MoviesApi.URL_MOVIE_POSTER + personImage.getFilePath();

        holder.imgPerson.setOnClickListener(v -> callback.onImageViewClicked((ImageView) v));


        ImageLoader.loadImageGlide(context, url, holder.imgPerson, null, 0, false, true, new RequestListener() {
            @Override
            public boolean onException(Exception e, Object model, Target target, boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(Object resource, Object model, Target target, boolean isFromMemoryCache, boolean isFirstResource) {
                holder.progressItem.setVisibility(View.GONE);
                return false;
            }
        });
    }


    @Override
    public int getItemCount() {
        return (this.personImageList != null ? this.personImageList.size() : 0);
    }


    class PersonImagesViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.imgMovieBackdrop)
        ImageView imgPerson;
        @BindView(R.id.progressMovieBackdrop)
        ProgressBar progressItem;


        PersonImagesViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
