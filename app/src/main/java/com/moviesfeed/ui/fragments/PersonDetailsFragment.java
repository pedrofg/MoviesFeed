package com.moviesfeed.ui.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.moviesfeed.R;
import com.moviesfeed.models.persondetails.PersonCast;
import com.moviesfeed.models.persondetails.PersonCrew;
import com.moviesfeed.models.persondetails.PersonImage;
import com.moviesfeed.ui.components.AppBarStateChangeListener;
import com.moviesfeed.ui.components.CustomLinearLayoutManager;
import com.moviesfeed.ui.components.DividerItemDecoration;
import com.moviesfeed.ui.adapters.PersonImagesAdapter;
import com.moviesfeed.ui.adapters.PersonMoviesAdapter;
import com.moviesfeed.ui.presenters.PersonDetailsPresenter;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static com.moviesfeed.ui.activities.PersonDetailsActivity.INTENT_PERSON_DETAILS_ID;

/**
 * Created by Pedro on 2017-04-16.
 */

public class PersonDetailsFragment extends DetailsFragment implements PersonDetailsPresenter.PersonPresenterCallback, PersonImagesAdapter.PersonImagesAdapterCallback, PersonMoviesAdapter.OnPersonMovieItemClicked {


    public interface PersonDetailsFragmentCallback {
        void openMovieDetail(int movieId);

        void setToolbar(Toolbar toolbar);

        void openPersonHomepage(Uri url);
    }

    private Unbinder unbinder;
    private PersonDetailsFragmentCallback callback;
    private DividerItemDecoration dividerItemDecoration;
    private CustomLinearLayoutManager rvMovieImagesLayoutManager;
    private PersonDetailsPresenter presenter;

    @BindView(R.id.toolbarPersonDetails)
    Toolbar toolbar;
    @BindView(R.id.imgHomepage)
    ImageView imgHomePage;
    @BindView(R.id.appBarLayout)
    AppBarLayout appBarLayout;
    @BindView(R.id.collapsingToolBar)
    CollapsingToolbarLayout collapsingToolBar;
    @BindView(R.id.progressLoadingMovies)
    ProgressBar progressLoadingMovies;
    @BindView(R.id.nestedScrollView)
    NestedScrollView nestedScrollView;
    @BindView(R.id.txtError)
    TextView txtPersonDetailsError;
    @BindView(R.id.recyclerViewPersonImages)
    RecyclerView rvPersonImages;
    @BindView(R.id.recyclerViewPersonMovies)
    RecyclerView rvPersonMovies;
    @BindView(R.id.cardViewInfo)
    View layoutInfo;
    @BindView(R.id.cardViewPersonMovies)
    View layoutPersonMovies;
    @BindView(R.id.viewRoot)
    CoordinatorLayout viewRoot;
    @BindView(R.id.txtPersonBiography)
    TextView txtPersonBiography;
    @BindView(R.id.txtPersonBirthday)
    TextView txtPersonBirthday;
    @BindView(R.id.txtPersonBirthplace)
    TextView txtPersonBirthplace;
    @BindView(R.id.txtBiography)
    TextView txtBiography;
    @BindView(R.id.layoutBirthday)
    View layoutBirthday;
    @BindView(R.id.layoutBirthplace)
    View layoutBirthplace;
    @BindView(R.id.layoutTitle)
    View layoutTitle;
    @BindView(R.id.layoutError)
    View layoutError;
    @BindView(R.id.personDetailsAdView)
    AdView adView;


    public PersonDetailsFragment() {
        setRetainInstance(true);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof PersonDetailsFragmentCallback) {
            this.callback = (PersonDetailsFragmentCallback) context;
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            this.presenter = new PersonDetailsPresenter();
            this.presenter.init(context(), this);
            this.dividerItemDecoration = new DividerItemDecoration(ContextCompat.getDrawable(context(), R.drawable.list_separator), false, false);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View fragmentView = inflater.inflate(R.layout.fragment_person_details, container, false);
        this.unbinder = ButterKnife.bind(this, fragmentView);

        this.callback.setToolbar(toolbar);

        this.appBarLayout.addOnOffsetChangedListener(new AppBarStateChangeListener() {
            @Override
            public void onStateChanged(AppBarLayout appBarLayout, AppBarStateChangeListener.State state, int verticalOffset) {
                presenter.appBarLayoutStateChanged(appBarLayout, state, verticalOffset);
            }
        });

        this.rvPersonImages.setHasFixedSize(true);
        this.rvPersonMovies.setHasFixedSize(true);

        return fragmentView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (savedInstanceState == null) {
            this.presenter.requestPersonDetails(getArguments().getInt(INTENT_PERSON_DETAILS_ID));
            AdRequest adRequest = new AdRequest.Builder().build();
            adView.loadAd(adRequest);
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        this.presenter.resume();
    }

    @Override
    public void onPause() {
        super.onPause();
        this.presenter.pause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.presenter.destroy();
    }

    @Override
    public void onDestroyView() {
        this.unbinder.unbind();
        super.onDestroyView();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.callback = null;
    }

    @OnClick(R.id.txtTryAgain)
    public void onClickTxtError(View v) {
        this.presenter.tryAgain();
    }

    public RecyclerView.LayoutManager getHorizontalLayoutManager() {
        return new LinearLayoutManager(context(), LinearLayoutManager.HORIZONTAL, false);
    }

    private void hideAllViews() {
        this.progressLoadingMovies.setVisibility(View.GONE);
        this.appBarLayout.setVisibility(View.GONE);
        this.nestedScrollView.setVisibility(View.GONE);
        this.layoutError.setVisibility(View.GONE);
    }


    @Override
    public void showError(String message) {
        this.txtPersonDetailsError.setText(message);
    }

    @Override
    public Context context() {
        return this.getActivity().getApplicationContext();
    }

    @Override
    public void contentUpdated(boolean error) {
        hideAllViews();
        if (error) {
            this.layoutError.setVisibility(View.VISIBLE);
        } else {
            this.appBarLayout.setVisibility(View.VISIBLE);
            this.nestedScrollView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void updatingContent() {
        hideAllViews();
        this.progressLoadingMovies.setVisibility(View.VISIBLE);
    }

    @Override
    public void updateToolbarTitle(String title) {
        super.updateToolbarTitle(this.toolbar, title);
    }

    @Override
    public String getToolbarTitle() {
        return super.getToolbarTitle(this.toolbar);
    }

    @Override
    public int getToolbarHeight() {
        return super.getToolbarHeight(this.toolbar);
    }

    @Override
    public void updateToolbarBackground(int alpha) {
        super.updateToolbarBackground(this.toolbar, alpha);
    }

    @Override
    public void animImagePoster(ImageView imageView, int height, ImageView.ScaleType scaleType) {
        super.animImagePoster(this.viewRoot, imageView, this.appBarLayout, height, scaleType);
    }

    @Override
    public void setScrollsEnable(boolean enable) {
        super.setScrollsEnable(enable, this.appBarLayout, this.rvMovieImagesLayoutManager);
    }

    @Override
    public void showHomepage(Uri homepage) {
        this.layoutTitle.setVisibility(View.VISIBLE);
        this.imgHomePage.setVisibility(View.VISIBLE);
        this.imgHomePage.setOnClickListener(v -> {
            callback.openPersonHomepage(homepage);
        });
    }

    @Override
    public void showPersonBiography(String biography) {
        this.layoutTitle.setVisibility(View.VISIBLE);
        this.txtBiography.setVisibility(View.VISIBLE);
        this.txtPersonBiography.setVisibility(View.VISIBLE);
        this.txtPersonBiography.setText(biography);
    }

    @Override
    public void showPersonBirthday(String birthday) {
        this.layoutBirthday.setVisibility(View.VISIBLE);
        this.txtPersonBirthday.setText(birthday);
    }

    @Override
    public void showPersonBirthplace(String birthplace) {
        this.layoutBirthplace.setVisibility(View.VISIBLE);
        this.txtPersonBirthplace.setText(birthplace);
    }

    @Override
    public void hideLayoutInfo() {
        this.layoutInfo.setVisibility(View.GONE);
    }

    @Override
    public void showPersonMovies(List<PersonCast> personCastList, List<PersonCrew> personCrewList) {
        this.rvPersonMovies.setLayoutManager(getHorizontalLayoutManager());
        this.rvPersonMovies.addItemDecoration(this.dividerItemDecoration);
        PersonMoviesAdapter adapter = new PersonMoviesAdapter(context(), personCastList, personCrewList, this);
        this.rvPersonMovies.setAdapter(adapter);
        this.layoutPersonMovies.setVisibility(View.VISIBLE);
    }

    @Override
    public void showPersonImages(List<PersonImage> personImageList) {
        this.rvMovieImagesLayoutManager = new CustomLinearLayoutManager(context(), LinearLayoutManager.HORIZONTAL);
        this.rvPersonImages.setLayoutManager(this.rvMovieImagesLayoutManager);
        this.rvPersonImages.setAdapter(new PersonImagesAdapter(context(), personImageList, this));
    }

    @Override
    public void onImageViewClicked(ImageView imageView) {
        this.presenter.onImagePosterClicked(imageView);
    }


    @Override
    public void onPersonMovieItemClicked(int movieID) {
        this.presenter.onPersonMovieItemClicked(movieID);
    }

    @Override
    public void openMovieDetails(int movieId) {
        this.callback.openMovieDetail(movieId);
    }


}
