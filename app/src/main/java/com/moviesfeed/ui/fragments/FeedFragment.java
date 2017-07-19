package com.moviesfeed.ui.fragments;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.moviesfeed.R;
import com.moviesfeed.api.Filters;
import com.moviesfeed.models.Movie;
import com.moviesfeed.models.MoviesFeed;
import com.moviesfeed.ui.adapters.FeedAdapter;
import com.moviesfeed.ui.components.EndlessScrollListener;
import com.moviesfeed.ui.presenters.FeedPresenter;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;


public class FeedFragment extends Fragment implements FeedPresenter.FeedPresenterCallback, SwipeRefreshLayout.OnRefreshListener, EndlessScrollListener.ScrollListener, FeedAdapter.OnFeedItemClicked {

    public interface FeedFragmentCallback {
        void onMovieClicked(Movie movie);
    }

    private FeedPresenter presenter;
    private FeedAdapter adapter;
    private Unbinder unbinder;
    private FeedFragmentCallback callback;
    private EndlessScrollListener endlessScrollListener;
    private GridLayoutManager gridLayoutManager;

    public static final int GRID_COLUMNS = 3;
    public static final int GRID_FILL_ALL_COLUMNS = 1;

    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.rvMoviesFeed)
    RecyclerView rvMoviesFeed;
    @BindView(R.id.txtError)
    TextView txtError;
    @BindView(R.id.txtTryAgain)
    TextView txtTryAgain;
    @BindView(R.id.layoutError)
    View layoutError;
    @BindView(R.id.swipeRefresh)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.feedAdView)
    AdView adView;

    public FeedFragment() {
        setRetainInstance(true);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof FeedFragmentCallback) {
            this.callback = (FeedFragmentCallback) context;
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        if (savedInstanceState == null || this.presenter == null) {
            this.presenter = new FeedPresenter();
            this.presenter.init(context(), this);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View fragmentView = inflater.inflate(R.layout.fragment_feed, container, false);
        unbinder = ButterKnife.bind(this, fragmentView);

        this.rvMoviesFeed.setHasFixedSize(true);

        this.swipeRefreshLayout.setOnRefreshListener(this);
        this.swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);

        createGrid();

        return fragmentView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.loadFeed(Filters.POPULARITY);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
    }

    @Override
    public void createGrid() {

        this.gridLayoutManager = new GridLayoutManager(context(), GRID_COLUMNS);
        this.gridLayoutManager.setSpanSizeLookup(onSpanSizeLookup);
        this.rvMoviesFeed.setLayoutManager(this.gridLayoutManager);


        this.rvMoviesFeed.clearOnScrollListeners();
        this.endlessScrollListener = new EndlessScrollListener(this.gridLayoutManager, this);
        this.rvMoviesFeed.addOnScrollListener(endlessScrollListener);

        this.swipeRefreshLayout.setRefreshing(false);
        this.rvMoviesFeed.scrollToPosition(0);

        this.adapter = new FeedAdapter(this.context(), this, 0);
        this.rvMoviesFeed.setAdapter(adapter);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            setProgressBarColor();
        }
    }

    private void setProgressBarColor() {
        this.progressBar.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.colorPrimary), android.graphics.PorterDuff.Mode.SRC_IN);
    }


    GridLayoutManager.SpanSizeLookup onSpanSizeLookup = new GridLayoutManager.SpanSizeLookup() {
        @Override
        public int getSpanSize(int position) {
            return adapter.getItemViewType(position) == FeedAdapter.VIEW_ITEM ? GRID_FILL_ALL_COLUMNS : GRID_COLUMNS;
        }
    };

    @Override
    public void updatingContent() {
        this.progressBar.setVisibility(View.VISIBLE);
        this.swipeRefreshLayout.setVisibility(View.GONE);
        this.layoutError.setVisibility(View.GONE);
    }


    private void loadFeed(Filters filter) {
        this.presenter.requestMoviesFeed(filter);
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
        super.onDestroyView();
        this.rvMoviesFeed.setAdapter(null);
        this.unbinder.unbind();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.callback = null;
    }


    @Override
    public void showError(String message) {
        this.txtError.setText(message);
    }


    @Override
    public Context context() {
        return this.getActivity().getApplicationContext();
    }

    @Override
    public void renderFeed(MoviesFeed moviesFeed, int insertedMovies) {
        this.adapter.setMovies(moviesFeed.getMovies());
        this.adapter.notifyItemRangeInserted(this.adapter.getItemCount() - insertedMovies, insertedMovies);
    }

    @Override
    public void viewMovie(Movie movie) {
        if (this.callback != null) {
            this.callback.onMovieClicked(movie);
        }
    }

    @Override
    public void onFeedItemClicked(Movie movie) {
        this.presenter.onMovieClicked(movie);
    }

    @Override
    public void onBtnUpdateFeedClicked() {
        this.presenter.onBtnUpdateFeedClicked();
    }


    @Override
    public void onRefresh() {
        this.presenter.refreshMoviesFeed();
    }


    @Override
    public void requestNextPage() {
        this.presenter.requestNextPage();
    }

    public void searchMovieFeed(String query) {
        this.presenter.requestSearchMoviesFeed(query);
    }

    public void navigationItemSelected(Filters filter) {
        this.presenter.navigationItemSelected(filter);
    }

    @Override
    public void contentUpdated(boolean error) {
        this.progressBar.setVisibility(View.GONE);
        this.swipeRefreshLayout.setVisibility(View.GONE);
        this.layoutError.setVisibility(View.GONE);

        if (error) {
            this.layoutError.setVisibility(View.VISIBLE);
        } else {
            this.swipeRefreshLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void clearScrollListener() {
        this.rvMoviesFeed.clearOnScrollListeners();
    }

    @Override
    public void stopRefreshing() {
        this.swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void showTryAgain() {
        this.txtTryAgain.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideTryAgain() {
        this.txtTryAgain.setVisibility(View.GONE);
    }


    @OnClick(R.id.txtTryAgain)
    public void onClick(View v) {
        this.presenter.tryAgain();
    }

    @Override
    public void addProgressBottomGrid() {
        this.endlessScrollListener.addProgressItem();
        this.rvMoviesFeed.post(() -> {
            adapter.addProgress();
        });

    }

    @Override
    public void addErrorBottomGrid() {
        this.endlessScrollListener.addErrorItem();
        this.rvMoviesFeed.post(() -> {
            adapter.addError();

            int lastItemPosition = rvMoviesFeed.getAdapter().getItemCount() - 1;
            if (this.gridLayoutManager.findLastVisibleItemPosition() + 1 == lastItemPosition) {
                rvMoviesFeed.scrollToPosition(lastItemPosition);
            }
        });
    }

    @Override
    public void removeProgressBottomGrid() {
        this.endlessScrollListener.removeProgressItem();
        this.adapter.removeProgress();
    }

}
