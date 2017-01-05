package com.moviesfeed.activities.uicomponents;

import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.GridView;

public class EndlessScrollListener implements OnScrollListener {

    private GridView gridView;
    private boolean isLoading;
    private boolean hasMorePages;
    private int pageNumber = 0;
    private RefreshList refreshList;
    private boolean isRefreshing;

    public EndlessScrollListener(GridView gridView, RefreshList refreshList) {
        this.gridView = gridView;
        this.isLoading = false;
        this.isRefreshing = false;
        this.hasMorePages = true;
        this.refreshList = refreshList;
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (totalItemCount > 0 && gridView.getLastVisiblePosition() + 1 == totalItemCount && !isLoading) {
            isLoading = true;
            if (hasMorePages && !isRefreshing) {
                isRefreshing = true;
                refreshList.onRefresh(pageNumber);
            }
        } else {
            isLoading = false;
        }

    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
    }

    public void noMorePages() {
        this.hasMorePages = false;
    }

    public void notifyMorePages() {
        isRefreshing = false;
        pageNumber = pageNumber + 1;
    }

    public interface RefreshList {
        void onRefresh(int pageNumber);
    }
}