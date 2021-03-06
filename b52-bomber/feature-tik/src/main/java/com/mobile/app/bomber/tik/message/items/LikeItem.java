package com.mobile.app.bomber.tik.message.items;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.mobile.app.bomber.common.base.tool.SingleClick;
import com.mobile.app.bomber.data.http.entities.ApiUsermsg;
import com.mobile.app.bomber.data.http.entities.ApiVideo;
import com.mobile.app.bomber.tik.R;
import com.mobile.app.bomber.tik.base.GlideExtKt;
import com.mobile.app.bomber.tik.databinding.ItemLikeAtBinding;
import com.mobile.app.bomber.tik.databinding.ItemLikeBinding;
import com.mobile.app.bomber.tik.mine.UserDetailActivity;
import com.mobile.ext.glide.GlideApp;
import com.mobile.guava.android.ui.view.recyclerview.LinearItemDecoration;
import com.mobile.guava.jvm.date.Java8TimeKt;
import com.pacific.adapter.AdapterUtils;
import com.pacific.adapter.AdapterViewHolder;
import com.pacific.adapter.RecyclerAdapter;
import com.pacific.adapter.SimpleRecyclerItem;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;

public class LikeItem extends SimpleRecyclerItem implements View.OnClickListener {
    private ApiVideo.Video video;

    private final RecyclerAdapter adapter = new RecyclerAdapter();
    private final String content;
    private LinearLayoutManager linearLayoutManager;
    private LinearItemDecoration linearItemDecoration;
    private ItemLikeBinding binding;

    @NonNull
    public final ApiUsermsg.Item data;

    public LikeItem(@NonNull ApiUsermsg.Item data) {
        this.data = data;
        List<ApiUsermsg.Item.Fromuserinfo> at = data.getFromuserinfo();
        if (at == null || at.isEmpty()) {
            this.content = "点赞过你";
        } else {
            this.content = "等" + data.getFromuserinfo().size() + "人点赞过你";
            if (at.size() > 3) {
                at = at.subList(0, 3);

            }
            this.adapter.addAll(at.stream().map(o -> new AtItem(o)).collect(Collectors.toList()));

        }
        this.adapter.setOnClickListener(this);
    }

    @SingleClick
    @Override
    public void onClick(View v) {

        AdapterViewHolder holder;
        int id = v.getId();
        if (id == R.id.item_like_at) {
            holder = AdapterUtils.INSTANCE.getHolder(v);
            LikeItem.AtItem atItem = holder.item();
            long uid = atItem.data.getUid();
            UserDetailActivity.start(holder.activity(), uid);
            return;
        }
    }

    @Override
    public void bind(@NotNull AdapterViewHolder holder) {
        binding = holder.binding(ItemLikeBinding::bind);
        binding.txtContent.setText(content);
        String ago = Java8TimeKt.ago(data.getCreatetime() * 1000L, System.currentTimeMillis());
        binding.txtTime.setText(ago);
        if (adapter.isEmpty()) {
            binding.recycler.setVisibility(View.GONE);
        } else {
            binding.recycler.setVisibility(View.VISIBLE);
            if (linearLayoutManager == null) {
                linearLayoutManager = new LinearLayoutManager(
                        holder.activity(),
                        LinearLayoutManager.HORIZONTAL,
                        false
                );
                linearItemDecoration = LinearItemDecoration.builder(holder.activity())
                        .color(android.R.color.transparent, R.dimen.size_6dp)
                        .horizontal()
                        .build();
            }
            if (binding.recycler.getItemDecorationCount() == 0) {
                binding.recycler.addItemDecoration(linearItemDecoration);
            }
            binding.recycler.setLayoutManager(linearLayoutManager);
            binding.recycler.setAdapter(adapter);
        }
        ApiUsermsg.Item.Fromuserinfo img = data.getFromuserinfo().get(0);
        binding.txtTitle.setText(img.getName());
        GlideExtKt.loadProfile(holder.activity(), img.getPic(), binding.imgProfile);
        GlideApp.with(holder.activity())
                .load(data.getCover())
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(binding.imgCover);
        holder.attachOnClickListener(R.id.img_cover);

    }

    @Override
    public void unbind(@NotNull AdapterViewHolder holder) {
        binding = holder.binding(ItemLikeBinding::bind);
        binding.recycler.removeItemDecoration(linearItemDecoration);
        binding.recycler.setAdapter(null);
        binding.recycler.setLayoutManager(null);
        binding = null;
    }

    @Override
    public int getLayout() {
        return R.layout.item_like;
    }


    public static class AtItem extends SimpleRecyclerItem {

        @NonNull
        public final ApiUsermsg.Item.Fromuserinfo data;

        public AtItem(@NonNull ApiUsermsg.Item.Fromuserinfo data) {
            this.data = data;
        }

        @Override
        public void bind(@NotNull AdapterViewHolder holder) {
            ItemLikeAtBinding binding = holder.binding(ItemLikeAtBinding::bind);
            GlideExtKt.loadProfile(holder.activity(), data.getPic(), binding.itemLikeAt);
            holder.attachOnClickListener(R.id.item_like_at);

        }

        @Override
        public int getLayout() {
            return R.layout.item_like_at;
        }
    }
}
