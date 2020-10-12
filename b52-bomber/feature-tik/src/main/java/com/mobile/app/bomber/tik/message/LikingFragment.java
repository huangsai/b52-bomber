package com.mobile.app.bomber.tik.message;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.mobile.app.bomber.common.base.Msg;
import com.mobile.app.bomber.tik.R;
import com.mobile.app.bomber.tik.home.PlayListActivity;
import com.mobile.app.bomber.tik.message.items.LikeItem;
import com.mobile.guava.jvm.domain.Source;
import com.pacific.adapter.AdapterUtils;
import com.pacific.adapter.AdapterViewHolder;

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
            PlayListActivity.start(getActivity(), item.data.getVideoid());
        }
    }

    @Override
    public void load(@NotNull ImageView imageView, @NotNull AdapterViewHolder holder) {
    }

    @Override
    protected void load() {

        model.postUserMsg(2, 0).observe(getViewLifecycleOwner(), source -> {
            if (source instanceof Source.Success) {
                List<LikeItem> list = source.requireData()
                        .stream()
                        .map(o -> new LikeItem(o))
                        .collect(Collectors.toList());
                adapter.replaceAll(list);
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
