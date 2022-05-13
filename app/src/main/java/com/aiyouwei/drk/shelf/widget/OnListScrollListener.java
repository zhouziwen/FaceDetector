package com.aiyouwei.drk.shelf.widget;


import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by chenjishi on 16/7/28.
 */
public class OnListScrollListener extends RecyclerView.OnScrollListener {

    private boolean isLoading;

    private LinearLayoutManager layoutManager;

    private OnPageEndListener listener;

    public OnListScrollListener(LinearLayoutManager layoutManager, OnPageEndListener listener) {
        this.layoutManager = layoutManager;
        this.listener = listener;
    }

    public void setIsLoading(boolean b) {
        isLoading = b;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        if (isLoading || dy <= 0) return;

        int visibleItemCount = layoutManager.getChildCount();
        int totalItemCount = layoutManager.getItemCount();
        int firstVisibleItem = layoutManager.findFirstVisibleItemPosition();

        if (visibleItemCount + firstVisibleItem >= totalItemCount) {
            listener.onPageEnd();
        }
    }

    public interface OnPageEndListener {

        void onPageEnd();

    }
}
