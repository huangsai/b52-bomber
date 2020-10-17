package com.mobile.app.bomber.tik.search;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.mobile.app.bomber.common.base.RecyclerAdapterEmpty;
import com.mobile.app.bomber.data.http.entities.ApiAtUser;
import com.mobile.app.bomber.data.http.entities.ApiVideo;
import com.mobile.app.bomber.data.http.entities.Pager;
import com.mobile.app.bomber.runner.RunnerX;
import com.mobile.app.bomber.tik.base.AppRouterUtils;
import com.mobile.app.bomber.tik.mine.UserDetailActivity;
import com.mobile.app.bomber.tik.search.items.SearchVideoItem;
import com.mobile.guava.android.ui.view.recyclerview.EndlessRecyclerViewScrollListener;
import com.mobile.guava.android.ui.view.recyclerview.LinearItemDecoration;
import com.mobile.guava.android.ui.view.recyclerview.RecyclerViewUtilsKt;
import com.mobile.guava.jvm.domain.Source;
import com.pacific.adapter.AdapterUtils;
import com.pacific.adapter.AdapterViewHolder;
import com.pacific.adapter.RecyclerAdapter;

import com.mobile.app.bomber.tik.R;
import com.mobile.app.bomber.common.base.Msg;
import com.mobile.app.bomber.common.base.MyBaseFragment;
import com.mobile.app.bomber.tik.databinding.FragmentSearchUserBinding;
import com.mobile.app.bomber.tik.search.items.SearchUserItem;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import kotlin.Pair;

public class FragmentSearchUser extends MyBaseFragment implements SwipeRefreshLayout.OnRefreshListener {
    private FragmentSearchUserBinding binding;
    private RecyclerAdapterEmpty recyclerAdapter;
    private String result;
    private SearchViewModel model;
    private AdapterViewHolder holder;

    private SearchUserItem item;
    private EndlessRecyclerViewScrollListener endless;

    protected final Pager pager = new Pager();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSearchUserBinding.inflate(inflater);
        initRecyclerView();
        return binding.getRoot();
    }

    private void initRecyclerView() {
        recyclerAdapter = new RecyclerAdapterEmpty();
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        binding.userRecycle.addItemDecoration(LinearItemDecoration.builder(requireContext()).bottomMargin(R.dimen.size_1dp).build());
        binding.userRecycle.setLayoutManager(layoutManager);
        model = AppRouterUtils.viewModels(this, SearchViewModel.class);
//        binding.layoutEmptyView.NoData.setVisibility(View.VISIBLE);
        recyclerAdapter.setEmptyView(binding.layoutEmptyView.NoData, binding.userRecycle);
        recyclerAdapter.setOnClickListener(v -> {
            if (v.getId() == R.id.layout_user_item) {
//                Msg.INSTANCE.toast("点击了用户条目");
                holder = AdapterUtils.INSTANCE.getHolder(v);
                int index = holder.getBindingAdapterPosition();
                item = holder.item();
                UserDetailActivity.start(getActivity(), item.data.getUsers().get(index).getUid());
            }
        });


        endless = new EndlessRecyclerViewScrollListener(
                binding.userRecycle.getLayoutManager(),
                (count, view) -> {
                    if (pager.isAvailable()) {
                        refreshData();
                    }
                    return null;
                }
        );


        binding.userRecycle.addOnScrollListener(endless);
        binding.layoutRefresh.setRefreshing(true);

        binding.layoutRefresh.setOnRefreshListener(this);

        binding.userRecycle.setAdapter(recyclerAdapter);
    }

    private void refreshData() {
        Bundle bundle = getArguments();
        result = bundle.getString("keyword");
        model.searchUserList(result).observe(getViewLifecycleOwner(), source -> {
            if (source instanceof Source.Success) {
                List<ApiAtUser> users = source.requireData();
                List<SearchUserItem> items = new ArrayList<>();
                for (ApiAtUser user : users) {
                    SearchUserItem searchVideoItem = new SearchUserItem(user);
                    items.add(searchVideoItem);
                }
                if (items.size() < 1 || items == null) {
                    Msg.INSTANCE.toast("暂无数据");
                }
                recyclerAdapter.replaceAll(items);

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
////            refreshData();
//        }
    }

    @Override
    public void onRefresh() {
        pager.reset();
        endless.reset();
        refreshData();
        binding.layoutRefresh.setRefreshing(true);
        recyclerAdapter.setEmptyView(binding.layoutEmptyView.NoData, binding.userRecycle);
    }
}
