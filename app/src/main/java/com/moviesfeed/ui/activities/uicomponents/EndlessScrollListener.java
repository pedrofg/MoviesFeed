package com.moviesfeed.ui.activities.uicomponents;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.moviesfeed.ui.activities.FeedActivity;

public class EndlessScrollListener extends RecyclerView.OnScrollListener {

    private int previousTotal = 0; // The total number of items in the dataset after the last load
    private boolean loading = true; // True if we are still waiting for the last set of data to load.
    private int visibleThreshold = 3; // The minimum amount of items to have below your current scroll position before loading more.
    private int firstVisibleItem, visibleItemCount, totalItemCount;

    private int isIconBottomVisible;

    private GridLayoutManager gridLayoutManager;
    private ScrollListener callback;

    public EndlessScrollListener(GridLayoutManager gridLayoutManager, ScrollListener refreshList) {
        this.gridLayoutManager = gridLayoutManager;
        this.callback = refreshList;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        totalItemCount = gridLayoutManager.getItemCount();

        if (loading) {
            if (totalItemCount > previousTotal + isIconBottomVisible) {
                Log.i(FeedActivity.class.getName(), "loading = false");
                loading = false;
                previousTotal = totalItemCount;
            }
        }
        if (!loading && totalItemCount != 0 && shouldLoadMoreItems(recyclerView)) {
            Log.i(FeedActivity.class.getName(), "totalItemCount: " + totalItemCount);
            Log.i(FeedActivity.class.getName(), "visibleItemCount: " + visibleItemCount);
            Log.i(FeedActivity.class.getName(), "isIconBottomVisible: " + isIconBottomVisible);
            Log.i(FeedActivity.class.getName(), "firstVisibleItem: " + firstVisibleItem);
            Log.i(FeedActivity.class.getName(), "visibleThreshold: " + visibleThreshold);
            // End has been reached

            callback.requestNextPage();

            loading = true;
        }
    }

    public boolean shouldLoadMoreItems(RecyclerView recyclerView) {
        visibleItemCount = recyclerView.getChildCount();
        totalItemCount = gridLayoutManager.getItemCount();
        firstVisibleItem = gridLayoutManager.findFirstVisibleItemPosition();

        return (totalItemCount - visibleItemCount - isIconBottomVisible) <= (firstVisibleItem + visibleThreshold);
    }

    public void addProgressItem() {
        isIconBottomVisible = 1;
    }

    public void addErrorItem() {
        isIconBottomVisible = 1;
    }

    public void removeProgressItem() {
        isIconBottomVisible = 0;
    }

    public interface ScrollListener {
        void requestNextPage();
    }
}
