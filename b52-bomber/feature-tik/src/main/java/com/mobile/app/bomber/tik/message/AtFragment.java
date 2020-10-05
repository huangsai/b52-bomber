package com.mobile.app.bomber.tik.message;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.mobile.guava.android.mvvm.RouterKt;
import com.mobile.app.bomber.data.http.entities.ApiVideo;
import com.mobile.guava.jvm.domain.Source;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.pacific.adapter.AdapterUtils;
import com.pacific.adapter.AdapterViewHolder;

import com.mobile.app.bomber.tik.R;
import com.mobile.ext.glide.GlideApp;
import com.mobile.app.bomber.tik.base.GlideExtKt;
import com.mobile.app.bomber.common.base.Msg;
import com.mobile.app.bomber.common.base.tool.SingleClick;
import com.mobile.app.bomber.tik.home.PlayListActivity;
import com.mobile.app.bomber.tik.message.items.AtItem;
import com.mobile.app.bomber.tik.mine.UserDetailActivity;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;

public class AtFragment extends BaseLikingFragment {
    private ApiVideo.Video video;

    @SingleClick
    @Override
    public void onClick(View v) {
        AdapterViewHolder holder;
        AtItem item;

        int id = v.getId();
        if (id == R.id.img_cover) {
            holder = AdapterUtils.INSTANCE.getHolder(v);
            item = holder.item();
            PlayListActivity.start(getActivity(), item.data.getVideoId());
        } else if (id == R.id.img_profile) {
            holder = AdapterUtils.INSTANCE.getHolder(v);
            int index = holder.getBindingAdapterPosition();
            item = holder.item();
//            Bundle bundle = new Bundle();
//            video = new ApiVideo.Video(1, "111", item.data.getUId(), 1, 1, 1, null, null, 1, true, true, 1, null, null, "null", null, null, null);
//            bundle.putSerializable("video", video);
            UserDetailActivity.start(getActivity(), item.data.getVideoId());
        }
    }

    @Override
    public void load(@NotNull ImageView imageView, @NotNull AdapterViewHolder holder) {
        AtItem item = holder.item();
        if (imageView.getId() == R.id.img_profile) {
            GlideExtKt.loadProfile(this, item.data.getProfile(), imageView);
            return;
        }

        if (imageView.getId() == R.id.img_cover) {
            GlideApp.with(this)
                    .load(GlideExtKt.decodeImgUrl(item.data.getVideoImageUrl()))
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(imageView);
            return;
        }
    }

    @Override
    protected void load() {
        if (!pager.isAvailable()) return;

        model.atList(pager).observe(getViewLifecycleOwner(), source -> {
            if (source instanceof Source.Success) {
                List<AtItem> list = source.requireData()
                        .stream()
                        .map(o -> new AtItem(o))
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

    public static AtFragment newInstance() {
        Bundle args = new Bundle();
        AtFragment fragment = new AtFragment();
        fragment.setArguments(args);
        return fragment;
    }
}
