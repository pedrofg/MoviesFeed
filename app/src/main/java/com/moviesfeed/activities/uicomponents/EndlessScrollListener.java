package com.moviesfeed.activities.uicomponents;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.moviesfeed.activities.FeedActivity;

public class EndlessScrollListener extends RecyclerView.OnScrollListener {

    private int previousTotal = 0; // The total number of items in the dataset after the last load
    private boolean loading = true; // True if we are still waiting for the last set of data to load.
    private int visibleThreshold = 3; // The minimum amount of items to have below your current scroll position before loading more.
    int firstVisibleItem, visibleItemCount, totalItemCount;

    private int currentPage = 1;
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

        visibleItemCount = recyclerView.getChildCount();
        totalItemCount = gridLayoutManager.getItemCount();
        firstVisibleItem = gridLayoutManager.findFirstVisibleItemPosition();

        if (loading) {
            if (totalItemCount > previousTotal + isProgressVisible) {
                Log.i(FeedActivity.class.getName(), "loading = false");
                loading = false;
                previousTotal = totalItemCount;
            }
        }
        if (!loading && totalItemCount != 0 && (totalItemCount - visibleItemCount - isProgressVisible)
                <= (firstVisibleItem + visibleThreshold)) {
            Log.i(FeedActivity.class.getName(), "totalItemCount: " + totalItemCount);
            Log.i(FeedActivity.class.getName(), "visibleItemCount: " + visibleItemCount);
            Log.i(FeedActivity.class.getName(), "isProgressVisible: " + isProgressVisible);
            Log.i(FeedActivity.class.getName(), "firstVisibleItem: " + firstVisibleItem);
            Log.i(FeedActivity.class.getName(), "visibleThreshold: " + visibleThreshold);
            // End has been reached

            // Do something
            currentPage++;

            refreshList.requestNextPage(currentPage);

            loading = true;
        }
    }

    public void addProgressItem() {
        isProgressVisible = 1;
    }

    public void removeProgressItem() {
        isProgressVisible = 0;
    }

    public void reset() {
        this.loading = true;
        this.previousTotal = 0;
        this.currentPage = 1;
        this.isProgressVisible = 0;
    }

    public interface RefreshList {
        void requestNextPage(int currentPage);
    }
}
