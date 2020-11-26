package com.mobile.app.bomber.tik.category;

import android.view.View;

import androidx.annotation.CallSuper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.pacific.adapter.AdapterImageLoader;
import com.pacific.adapter.AdapterViewHolder;
import com.pacific.adapter.OnDataSetChanged;
import com.pacific.adapter.RecyclerAdapter;
import com.pacific.adapter.SimpleRecyclerItem;
import com.mobile.app.bomber.data.http.entities.ApiVideo;
import com.mobile.app.bomber.data.http.entities.Pager;
import com.mobile.guava.android.ui.view.recyclerview.EndlessRecyclerViewScrollListener;
import com.mobile.guava.android.ui.view.recyclerview.LinearItemDecoration;

import com.mobile.app.bomber.tik.R;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseVideoPresenter extends SimpleRecyclerItem implements OnDataSetChanged,
        View.OnClickListener, SwipeRefreshLayout.OnRefreshListener, AdapterImageLoader {

    protected final Pager pager = new Pager();
    protected final RecyclerAdapter adapter = new RecyclerAdapter();
    protected final List<ApiVideo.Video> videos = new ArrayList<>();

    protected LinearLayoutManager layoutManager;
    protected LinearItemDecoration itemDecoration;
    protected EndlessRecyclerViewScrollListener endless;
    protected AdapterViewHolder viewHolder;

    protected final CategoryFragment fragment;

    public BaseVideoPresenter(CategoryFragment fragment) {
        this.fragment = fragment;
        adapter.setOnDataSetChanged(this);
        adapter.setOnClickListener(this);
        adapter.setImageLoader(this);
    }

    @CallSuper
    @Override
    public void bind(@NotNull AdapterViewHolder holder) {
        this.viewHolder = holder;
        if (layoutManager == null) {
            itemDecoration = LinearItemDecoration.builder(holder.activity())
                    .color(android.R.color.transparent, R.dimen.size_6dp)
                    .horizontal()
                    .build();
            layoutManager = new LinearLayoutManager(
                    holder.activity(),
                    LinearLayoutManager.HORIZONTAL,
                    false
            );
            endless = new EndlessRecyclerViewScrollListener(layoutManager, (count, view) -> {
                if (pager.isAvailable()) {
                    load();
                }
                return null;
            });
            RecyclerView recycler = getRecyclerView();
            recycler.setLayoutManager(layoutManager);
            recycler.addItemDecoration(itemDecoration);
            recycler.setAdapter(adapter);
            recycler.addOnScrollListener(endless);
            getEmptyView().setOnClickListener(v -> onRefresh());
            onRefresh();
        } else {
            throw new IllegalStateException("Not allowed");
        }
    }

    @CallSuper
    @Override
    public void unbind(@NotNull AdapterViewHolder holder) {
        super.unbind(holder);
        RecyclerView recycler = getRecyclerView();
        recycler.removeOnScrollListener(endless);
        recycler.setLayoutManager(null);
        recycler.setAdapter(null);
        this.viewHolder = null;
    }

    @Override
    public void apply(int count) {
        if (viewHolder != null) {
            View emptyView = getEmptyView();
            if (count > 0) {
                emptyView.setVisibility(View.INVISIBLE);
            } else {
                emptyView.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onRefresh() {
        pager.reset();
        if (endless != null) {
            endless.reset();
        }
        load();
    }

    protected abstract void load();

    protected abstract RecyclerView getRecyclerView();

    protected abstract View getEmptyView();
}
