package com.mobile.app.bomber.tik.message;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.mobile.app.bomber.common.base.Msg;
import com.mobile.app.bomber.common.base.tool.SingleClick;
import com.mobile.app.bomber.data.http.entities.ApiUsermsg;
import com.mobile.app.bomber.tik.R;
import com.mobile.app.bomber.tik.base.GlideExtKt;
import com.mobile.app.bomber.tik.message.items.FansItem;
import com.mobile.guava.jvm.domain.Source;
import com.pacific.adapter.AdapterUtils;
import com.pacific.adapter.AdapterViewHolder;

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
        model.postUserMsg(1, 0).observe(getViewLifecycleOwner(), source -> {
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
        GlideExtKt.loadProfile(this, item.data.getFromuserinfo().get(0).getPic(), imageView);
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
        ApiUsermsg.Item data = holder.<FansItem>item().data;
        final int position = holder.getBindingAdapterPosition();
        final boolean oldIsFollowing = data.getIsfollow() == 2;
        model.follow(data.getId(), oldIsFollowing).observe(this, source -> {
            if (source instanceof Source.Success) {
                if (oldIsFollowing) {
                    data.setIsfollow(1);
                } else {
                    data.setIsfollow(2);
                }
                adapter.notifyItemChanged(position, 0);
            } else {
                Msg.INSTANCE.handleSourceException(source.requireError());
            }
        });
    }
}
