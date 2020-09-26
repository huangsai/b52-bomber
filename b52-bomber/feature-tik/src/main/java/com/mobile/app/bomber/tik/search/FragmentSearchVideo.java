package com.mobile.app.bomber.tik.search;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.mobile.app.bomber.common.base.Msg;
import com.mobile.app.bomber.common.base.MyBaseFragment;
import com.mobile.app.bomber.common.base.RecyclerAdapterEmpty;
import com.mobile.app.bomber.data.http.entities.ApiVideo;
import com.mobile.app.bomber.data.http.entities.Pager;
import com.mobile.app.bomber.runner.RunnerX;
import com.mobile.app.bomber.tik.R;
import com.mobile.app.bomber.tik.base.AppRouterUtils;
//import com.mobile.app.bomber.tik.databinding.FragmentSearchVideoBinding;
import com.mobile.app.bomber.tik.databinding.FragmentSearchVideoBinding;
import com.mobile.app.bomber.tik.mine.UserDetailActivity;
import com.mobile.app.bomber.tik.search.items.SearchVideoItem;
import com.mobile.guava.android.ui.view.recyclerview.EndlessRecyclerViewScrollListener;
import com.mobile.guava.android.ui.view.recyclerview.RecyclerViewUtilsKt;
import com.mobile.guava.android.ui.view.recyclerview.TestedGridItemDecoration;
import com.mobile.guava.jvm.domain.Source;
import com.pacific.adapter.AdapterUtils;
import com.pacific.adapter.AdapterViewHolder;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import kotlin.Pair;

public class FragmentSearchVideo extends MyBaseFragment implements SwipeRefreshLayout.OnRefreshListener{

    private FragmentSearchVideoBinding binding;
    private RecyclerAdapterEmpty recyclerAdapter;
    private String result;
    private SearchViewModel model;
    private AdapterViewHolder holder;
    private SearchVideoItem item;
    private EndlessRecyclerViewScrollListener endless;

    protected final Pager pager = new Pager();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSearchVideoBinding.inflate(inflater);
        initRecyclerView();
        return binding.getRoot();
    }

    private void initRecyclerView() {
        recyclerAdapter = new RecyclerAdapterEmpty();
        GridLayoutManager layoutManager = new GridLayoutManager(requireContext(), 2);
        binding.videoRecycle.addItemDecoration(new TestedGridItemDecoration(requireActivity(), R.dimen.size_1dp));
        binding.videoRecycle.setLayoutManager(layoutManager);
        model = AppRouterUtils.viewModels(this, SearchViewModel.class);
        recyclerAdapter.setEmptyView(binding.layoutEmptyView.NoData, binding.videoRecycle);

        recyclerAdapter.setOnClickListener(v -> {
            if (v.getId() == R.id.layout_video_item) {
                holder = AdapterUtils.INSTANCE.getHolder(v);
                item = holder.item();
                UserDetailActivity.start(getActivity(), item.data.getVideoId());
            }
        });
        endless = new EndlessRecyclerViewScrollListener(
                binding.videoRecycle.getLayoutManager(),
                (count, view) -> {
                    if (pager.isAvailable()) {
                        refreshData();
                    }
                    return null;
                }
        );
        binding.videoRecycle.addOnScrollListener(endless);
        binding.layoutRefresh.setRefreshing(true);

        binding.layoutRefresh.setOnRefreshListener(this);
        binding.videoRecycle.setAdapter(recyclerAdapter);

    }

    private void refreshData() {
//        if (!pager.isAvailable()) return;
        Bundle bundle = getArguments();
        result = bundle.getString("keyword");
        model.search(result, pager).observe(getViewLifecycleOwner(), source -> {
//            if (binding.layoutRefresh.isRefreshing()) binding.layoutRefresh.setRefreshing(false);
            if (source instanceof Source.Success) {
                List<ApiVideo.Video> videos = source.requireData();
                List<SearchVideoItem> items = new ArrayList<>();
                for (ApiVideo.Video video : videos) {
                    SearchVideoItem searchVideoItem = new SearchVideoItem(video);
                    items.add(searchVideoItem);
                }
                if (pager.isReachedTheEnd()) {
                    Msg.INSTANCE.toast("已经加载完数据");
                }
                if (pager.isFirstPage(2)) {
                    recyclerAdapter.replaceAll(items);
                } else {
                    recyclerAdapter.addAll(items);
                }
            } else {
                Msg.INSTANCE.handleSourceException(source.requireError());
            }
            RecyclerViewUtilsKt.cancelRefreshing(binding.layoutRefresh, 500L);
        });
    }

    @Override
    public void onBusEvent(@NotNull Pair<Integer, ?> event) {
        super.onBusEvent(event);
//        if (event.getFirst() == RunnerX.INSTANCE.BUS_SEARCH_RESULT) {
//            refreshData();
//        }
    }

    @Override
    public void onRefresh() {
        pager.reset();
        endless.reset();
        refreshData();
        binding.layoutRefresh.setRefreshing(true);
        recyclerAdapter.setEmptyView(binding.layoutEmptyView.NoData, binding.videoRecycle);
    }
}
