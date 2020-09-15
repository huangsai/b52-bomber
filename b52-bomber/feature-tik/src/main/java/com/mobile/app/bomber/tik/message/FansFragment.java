package com.mobile.app.bomber.tik.message;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.pacific.adapter.AdapterUtils;
import com.pacific.adapter.AdapterViewHolder;
import com.mobile.app.bomber.data.http.entities.ApiFollow;
import com.mobile.guava.jvm.domain.Source;

import com.mobile.app.bomber.tik.R;
import com.mobile.app.bomber.tik.base.GlideExtKt;
import com.mobile.app.bomber.common.base.Msg;
import com.mobile.app.bomber.common.base.tool.SingleClick;
import com.mobile.app.bomber.tik.message.items.FansItem;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Collectors;

public class FansFragment extends BaseLikingFragment {

    public static FansFragment newInstance() {
        Bundle args = new Bundle();
        FansFragment fragment = new FansFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onViewCreated(@NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.recycler.removeOnScrollListener(endless);
    }

    @Override
    protected void load() {
        model.fanList().observe(getViewLifecycleOwner(), source -> {
            if (source instanceof Source.Success) {
                List<FansItem> list = source.requireData()
                        .stream()
                        .map(o -> new FansItem(o))
                        .collect(Collectors.toList());
                adapter.replaceAll(list);
            } else {
                Msg.INSTANCE.handleSourceException(source.requireError());
            }
            falseRefreshing();
        });
    }

    @Override
    public void load(@NotNull ImageView imageView, @NotNull AdapterViewHolder holder) {
        FansItem item = holder.item();
        GlideExtKt.loadProfile(this, item.data.getProfile(), imageView);
    }

    @SingleClick
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.status_btn) {
            followed(v);
            return;
        }
    }

    private void followed(View view) {
        AdapterViewHolder holder = AdapterUtils.INSTANCE.getHolder(view);
        ApiFollow.Follow data = holder.<FansItem>item().data;
        final int position = holder.getBindingAdapterPosition();
        final boolean oldIsFollowing = data.isFollowing();
        model.follow(data.getFollowUid(), oldIsFollowing).observe(this, source -> {
            if (source instanceof Source.Success) {
                data.setFollowing(!oldIsFollowing);
                adapter.notifyItemChanged(position, 0);
            } else {
                Msg.INSTANCE.handleSourceException(source.requireError());
            }
        });
    }
}
