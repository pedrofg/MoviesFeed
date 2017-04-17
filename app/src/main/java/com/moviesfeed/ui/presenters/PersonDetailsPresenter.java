package com.moviesfeed.ui.presenters;

import android.content.Context;
import android.net.Uri;
import android.support.design.widget.AppBarLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.moviesfeed.R;
import com.moviesfeed.interactors.PersonDetailsInteractor;
import com.moviesfeed.interactors.PersonDetailsInteractorCallback;
import com.moviesfeed.models.persondetails.Person;
import com.moviesfeed.models.persondetails.PersonCast;
import com.moviesfeed.models.persondetails.PersonCrew;
import com.moviesfeed.models.persondetails.PersonImage;
import com.moviesfeed.ui.activities.uicomponents.AppBarStateChangeListener;
import com.moviesfeed.ui.activities.uicomponents.AppBarStateChangeListener.State;

import java.util.List;

/**
 * Created by Pedro on 2017-04-16.
 */
public class PersonDetailsPresenter implements Presenter, PersonDetailsInteractorCallback {


    public interface PersonPresenterCallback {

        void showError(String message);

        Context context();

        void contentUpdated(boolean error);

        void updatingContent();

        void showPersonBiography(String biography);

        void showPersonBirthday(String birthday);

        void showPersonBirthplace(String birthplace);

        void showHomepage(Uri homepage);

        void showPersonMovies(List<PersonCast> personCastList, List<PersonCrew> personCrewList);

        void showPersonImages(List<PersonImage> personImageList);

        void updateToolbarTitle(String title);

        String getToolbarTitle();

        int getToolbarHeight();

        void updateToolbarBackground(int alpha);

        void openMovieDetails(int movieId);

        void animImagePoster(ImageView imageView, int height, ImageView.ScaleType scaleType);

        void setScrollsEnable(boolean blocked);

        void hideLayoutInfo();
    }

    private PersonDetailsInteractor personDetailsInteractor;
    private PersonPresenterCallback callback;
    private int MAX_ALPHA = 255;

    public void appBarLayoutStateChanged(AppBarLayout appBarLayout, AppBarStateChangeListener.State state, int verticalOffset) {
        if (state == State.COLLAPSED && this.personDetailsInteractor.hasCache())
            callback.updateToolbarTitle(this.personDetailsInteractor.getPersonName());
        else if (!TextUtils.isEmpty(callback.getToolbarTitle())) {
            callback.updateToolbarTitle("");
        }
        //measuring for alpha
        int toolBarHeight = callback.getToolbarHeight();
        int appBarHeight = appBarLayout.getMeasuredHeight();
        Float f = ((((float) appBarHeight - toolBarHeight) + verticalOffset) / ((float) appBarHeight - toolBarHeight)) * MAX_ALPHA;
        callback.updateToolbarBackground(MAX_ALPHA - Math.round(f));
    }

    public void init(Context context, PersonPresenterCallback callback) {
        this.callback = callback;
        this.personDetailsInteractor = new PersonDetailsInteractor(context, this);
        this.personDetailsInteractor.init();
    }

    public void requestPersonDetails(int personID) {
        Log.d(MovieDetailPresenter.class.getName(), "requestPersonDetails personID: " + personID);
        callback.updatingContent();
        this.personDetailsInteractor.requestPersonDetails(personID);
    }

    public void tryAgain() {
        Log.d(MovieDetailPresenter.class.getName(), "tryAgain()");
        callback.updatingContent();
        this.personDetailsInteractor.tryAgain();
    }


    public void onPersonMovieItemClicked(int movieID) {
        this.callback.openMovieDetails(movieID);
    }

    public void onImagePosterClicked(ImageView imageView) {

        boolean isZoomed;
        int imgDefaultHeight = (int) callback.context().getResources().getDimension(R.dimen.movie_detail_img_backdrop_height);

        isZoomed = imageView.getHeight() != imgDefaultHeight;

        int height = isZoomed ? imgDefaultHeight
                : ViewGroup.LayoutParams.MATCH_PARENT;

        ImageView.ScaleType scaleType = isZoomed ? ImageView.ScaleType.FIT_XY
                : ImageView.ScaleType.CENTER_CROP;


        callback.animImagePoster(imageView, height, scaleType);

        callback.setScrollsEnable(isZoomed);
    }

    @Override
    public void onLoadSuccess(Person person) {
        boolean personHasInfo = false;
        if (!TextUtils.isEmpty(person.getBiography())) {
            this.callback.showPersonBiography(person.getBiography());
            personHasInfo = true;
        }
        if (!TextUtils.isEmpty(person.getBirthday())) {
            this.callback.showPersonBirthday(person.getBirthday());
            personHasInfo = true;
        }
        if (!TextUtils.isEmpty(person.getPlaceOfBirth())) {
            this.callback.showPersonBirthplace(person.getPlaceOfBirth());
            personHasInfo = true;
        }
        if (!TextUtils.isEmpty(person.getHomepage())) {
            Uri url = Uri.parse(person.getHomepage());
            this.callback.showHomepage(url);
            personHasInfo = true;
        }

        if (!personHasInfo)
            callback.hideLayoutInfo();

        if (person.getPersonMovieCredits() != null &&
                (person.getPersonMovieCredits().getPersonCasts() != null || person.getPersonMovieCredits().getPersonCrews() != null) &&
                (person.getPersonMovieCredits().getPersonCasts().size() > 0 || person.getPersonMovieCredits().getPersonCrews().size() > 0)) {
            this.callback.showPersonMovies(person.getPersonMovieCredits().getPersonCasts(), person.getPersonMovieCredits().getPersonCrews());
        }


        if (person.getPersonImages() != null &&
                person.getPersonImages().getPersonImageList() != null &&
                person.getPersonImages().getPersonImageList().size() > 0) {
            this.callback.showPersonImages(person.getPersonImages().getPersonImageList());
        }

        this.callback.contentUpdated(false);
    }

    @Override
    public void onLoadError(Throwable throwable, boolean isNetworkError) {
        StringBuilder message = new StringBuilder();
        message.append(isNetworkError ? callback.context().getString(R.string.error_request_feed_network) : callback.context().getString(R.string.error_request_feed));
        callback.showError(message.toString());
        callback.contentUpdated(true);
    }

    @Override
    public void resume() {

    }

    @Override
    public void pause() {

    }

    @Override
    public void destroy() {
        this.personDetailsInteractor.dispose();
        this.callback = null;
    }

}
