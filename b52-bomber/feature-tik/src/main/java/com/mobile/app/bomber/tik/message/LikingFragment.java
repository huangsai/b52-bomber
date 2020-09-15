package com.mobile.app.bomber.tik.message;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.pacific.adapter.AdapterUtils;
import com.pacific.adapter.AdapterViewHolder;
import com.mobile.guava.jvm.domain.Source;

import com.mobile.app.bomber.tik.R;
import com.mobile.app.bomber.common.base.Msg;
import com.mobile.app.bomber.tik.home.PlayListActivity;
import com.mobile.app.bomber.tik.message.items.LikeItem;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;

public class LikingFragment extends BaseLikingFragment {

    @Override
    public void onClick(View v) {

        AdapterViewHolder holder;
        if (v.getId() == R.id.img_cover) {
            holder = AdapterUtils.INSTANCE.getHolder(v);
            LikeItem item = holder.item();
            PlayListActivity.start(getActivity(), item.data.getVideoId());
        }
    }
    @Override
    public void load(@NotNull ImageView imageView, @NotNull AdapterViewHolder holder) {
    }

    @Override
    protected void load() {
        if (!pager.isAvailable()) return;

        model.likeList(pager).observe(getViewLifecycleOwner(), source -> {
            if (source instanceof Source.Success) {
                List<LikeItem> list = source.requireData()
                        .stream()
                        .map(o -> new LikeItem(o))
                        .collect(Collectors.toList());

                     if (pager.isFirstPage(2)) {
                        adapter.replaceAll(list);
                    } else {
                        adapter.addAll(list);
                    }
            } else {
                Msg.INSTANCE.handleSourceException(source.requireError());
            }
            falseRefreshing();
        });


    }

    public static LikingFragment newInstance() {
        Bundle args = new Bundle();
        LikingFragment fragment = new LikingFragment();
        fragment.setArguments(args);
        return fragment;
    }
}
