package com.mobile.app.bomber.tik.message;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.pacific.adapter.AdapterImageLoader;
import com.mobile.guava.android.ui.view.recyclerview.EndlessRecyclerViewScrollListener;

import com.mobile.app.bomber.tik.base.AppRouterUtils;
import com.mobile.app.bomber.tik.databinding.FragmentLikingBinding;

public abstract class BaseLikingFragment extends TopMineFragment implements AdapterImageLoader {

    protected FragmentLikingBinding binding;
    protected MsgViewModel model;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        model = AppRouterUtils.viewModels(this, MsgViewModel.class);
        adapter.setOnClickListener(this);
        adapter.setOnDataSetChanged(this);
        adapter.setImageLoader(this);
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        binding = FragmentLikingBinding.inflate(inflater, container, false);
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireActivity());
        endless = new EndlessRecyclerViewScrollListener(layoutManager, (count, recyclerView) -> {
            if (pager.isAvailable()) {
                //load();
            }
            return null;
        });
        binding.recycler.setLayoutManager(layoutManager);
        binding.recycler.setAdapter(adapter);
        binding.layoutRefresh.setOnRefreshListener(this);
        binding.recycler.addOnScrollListener(endless);
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadIfEmpty();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding.recycler.setAdapter(null);
        binding.recycler.removeOnScrollListener(endless);
    }

    @Override
    public void apply(int i) {
        if (i > 0) {
            binding.txtEmpty.setVisibility(View.INVISIBLE);
        } else {
            binding.txtEmpty.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void autoOnRefresh() {
        binding.layoutRefresh.setRefreshing(true);
        onRefresh();
    }

    @Override
    protected void falseRefreshing() {
        binding.layoutRefresh.setRefreshing(false);
    }

    @Override
    protected void load() {
    }
}
