package com.mobile.app.bomber.common.base;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.pacific.adapter.OnDataSetChanged;
import com.pacific.adapter.RecyclerAdapter;
import com.mobile.app.bomber.data.http.entities.Pager;
import com.mobile.guava.android.ui.view.recyclerview.EndlessRecyclerViewScrollListener;

public abstract class RecyclerViewFragment extends MyBaseFragment
        implements SwipeRefreshLayout.OnRefreshListener, OnDataSetChanged, View.OnClickListener {

    public RecyclerViewFragment() {
    }

    public RecyclerViewFragment(int contentLayoutId) {
        super(contentLayoutId);
    }

    @NonNull
    protected final Pager pager = new Pager();

    @NonNull
    protected final RecyclerAdapter adapter = new RecyclerAdapter();

    @NonNull
    protected EndlessRecyclerViewScrollListener endless;

    @Override
    public void onRefresh() {
        pager.reset();
        endless.reset();
        load();
    }

    protected void loadIfEmpty() {
        if (adapter.isEmpty()) {
            onRefresh();
        }
    }

    protected abstract void autoOnRefresh();

    protected abstract void falseRefreshing();

    protected abstract void load();
}
