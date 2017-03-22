package com.moviesfeed.ui.activities.uicomponents;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.moviesfeed.ui.activities.FeedActivity;

public class EndlessScrollListener extends RecyclerView.OnScrollListener {

    private int previousTotal = 0; // The total number of items in the dataset after the last load
    private boolean loading = true; // True if we are still waiting for the last set of data to load.
    private int visibleThreshold = 3; // The minimum amount of items to have below your current scroll position before loading more.
    int firstVisibleItem, visibleItemCount, totalItemCount;

    int isProgressVisible;

    private GridLayoutManager gridLayoutManager;
    private EndlessScrollListener.RefreshList refreshList;

    public EndlessScrollListener(GridLayoutManager gridLayoutManager, EndlessScrollListener.RefreshList refreshList) {
        this.gridLayoutManager = gridLayoutManager;
        this.refreshList = refreshList;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        totalItemCount = gridLayoutManager.getItemCount();

        if (loading) {
            if (totalItemCount > previousTotal + isProgressVisible) {
                Log.i(FeedActivity.class.getName(), "loading = false");
                loading = false;
                previousTotal = totalItemCount;
            }
        }
        if (!loading && totalItemCount != 0 && shouldLoadMoreItems(recyclerView)) {
            Log.i(FeedActivity.class.getName(), "totalItemCount: " + totalItemCount);
            Log.i(FeedActivity.class.getName(), "visibleItemCount: " + visibleItemCount);
            Log.i(FeedActivity.class.getName(), "isProgressVisible: " + isProgressVisible);
            Log.i(FeedActivity.class.getName(), "firstVisibleItem: " + firstVisibleItem);
            Log.i(FeedActivity.class.getName(), "visibleThreshold: " + visibleThreshold);
            // End has been reached

            refreshList.requestNextPage();

            loading = true;
        }
    }

    public boolean shouldLoadMoreItems(RecyclerView recyclerView) {
        visibleItemCount = recyclerView.getChildCount();
        totalItemCount = gridLayoutManager.getItemCount();
        firstVisibleItem = gridLayoutManager.findFirstVisibleItemPosition();

        return (totalItemCount - visibleItemCount - isProgressVisible) <= (firstVisibleItem + visibleThreshold);
    }

    public void setLoading(boolean loading) {
        this.loading = loading;
    }

    public void addProgressItem() {
        isProgressVisible = 1;
    }

    public void removeProgressItem() {
        isProgressVisible = 0;
    }

    public interface RefreshList {
        void requestNextPage();
    }
}
