package com.mobile.app.bomber.tik.search;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.mobile.app.bomber.runner.RunnerX;
import com.mobile.guava.android.ui.view.recyclerview.LinearItemDecoration;
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

public class FragmentSearchUser extends MyBaseFragment {
    private FragmentSearchUserBinding binding;
    private RecyclerAdapter recyclerAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSearchUserBinding.inflate(inflater);
        initRecyclerView();
        return binding.getRoot();
    }

    private void initRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        binding.userRecycle.addItemDecoration(LinearItemDecoration.builder(requireContext()).bottomMargin(R.dimen.size_1dp).build());
        binding.userRecycle.setLayoutManager(layoutManager);
        recyclerAdapter = new RecyclerAdapter();
        recyclerAdapter.setOnClickListener(v -> {
            if (v.getId() == R.id.layout_user_item) {
                Msg.INSTANCE.toast("点击了用户条目");
            }
        });
        binding.userRecycle.setAdapter(recyclerAdapter);
    }

    private void refreshData() {
        List<SearchUserItem> items = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            items.add(new SearchUserItem(null));
        }
        recyclerAdapter.replaceAll(items);
    }

    @Override
    public void onBusEvent(@NotNull Pair<Integer, ?> event) {
        super.onBusEvent(event);
        if (event.getFirst() == RunnerX.INSTANCE.BUS_SEARCH_RESULT) {
            refreshData();
        }
    }
}
