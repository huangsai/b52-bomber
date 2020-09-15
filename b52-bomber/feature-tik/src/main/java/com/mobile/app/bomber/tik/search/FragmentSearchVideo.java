package com.mobile.app.bomber.tik.search;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;

import com.mobile.app.bomber.common.base.Msg;
import com.mobile.app.bomber.common.base.MyBaseFragment;
import com.mobile.app.bomber.runner.RunnerLib;
import com.mobile.app.bomber.tik.R;
import com.mobile.app.bomber.tik.databinding.FragmentSearchVideoBinding;
import com.mobile.app.bomber.tik.search.items.SearchVideoItem;
import com.mobile.guava.android.ui.view.recyclerview.TestedGridItemDecoration;
import com.pacific.adapter.RecyclerAdapter;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import kotlin.Pair;

public class FragmentSearchVideo extends MyBaseFragment {

    private FragmentSearchVideoBinding binding;
    private RecyclerAdapter recyclerAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSearchVideoBinding.inflate(inflater);
        initRecyclerView();
        return binding.getRoot();
    }

    private void initRecyclerView() {
        GridLayoutManager layoutManager = new GridLayoutManager(requireContext(), 2);
        binding.videoRecycle.addItemDecoration(new TestedGridItemDecoration(requireActivity(), R.dimen.size_1dp));
        binding.videoRecycle.setLayoutManager(layoutManager);
        recyclerAdapter = new RecyclerAdapter();
        recyclerAdapter.setOnClickListener(v -> {
            if (v.getId() == R.id.layout_video_item) {
                Msg.INSTANCE.toast("点击了视频条目");
            }
        });
        binding.videoRecycle.setAdapter(recyclerAdapter);
    }

    private void refreshData() {
        List<SearchVideoItem> items = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            items.add(new SearchVideoItem(null));
        }
        recyclerAdapter.replaceAll(items);
    }

    @Override
    public void onBusEvent(@NotNull Pair<Integer, ?> event) {
        super.onBusEvent(event);
        if (event.getFirst() == RunnerLib.INSTANCE.BUS_SEARCH_RESULT) {
            refreshData();
        }
    }
}
