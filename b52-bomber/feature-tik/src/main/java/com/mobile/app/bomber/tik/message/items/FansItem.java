package com.mobile.app.bomber.tik.message.items;

import android.view.View;

import androidx.annotation.NonNull;

import com.pacific.adapter.AdapterViewHolder;
import com.pacific.adapter.SimpleRecyclerItem;
import com.mobile.app.bomber.data.http.entities.ApiFollow;

import com.mobile.app.bomber.tik.R;
import com.mobile.app.bomber.tik.databinding.ItemFansBinding;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class FansItem extends SimpleRecyclerItem {

    @NonNull
    public final ApiFollow.Follow data;
    private final String time;

    public FansItem(@NonNull ApiFollow.Follow data) {
        this.data = data;
        this.time = new SimpleDateFormat("MM-dd").format(new Date(data.getFollowTime()));
    }

    @Override
    public void bind(@NotNull AdapterViewHolder holder) {
        ItemFansBinding binding = holder.binding(ItemFansBinding::bind);
        binding.usernameTv.setText(data.getUsername());
        binding.descTv.setVisibility(View.VISIBLE);
        binding.descTv.setText("他关注了你");
        binding.timeTv.setVisibility(View.VISIBLE);
        binding.timeTv.setText(time);
        binding.statusBtn.setText(data.isFollowing() ? "互相关注" : "回关");
        binding.statusBtn.setSelected(data.isFollowing());
        holder.attachImageLoader(R.id.img_profile);
        holder.attachOnClickListener(R.id.status_btn);
    }

    @Override
    public void bindPayloads(@NotNull AdapterViewHolder holder, @Nullable List<?> payloads) {
        ItemFansBinding binding = holder.binding(ItemFansBinding::bind);
        binding.statusBtn.setText(data.isFollowing() ? "互相关注" : "回关");
        binding.statusBtn.setSelected(data.isFollowing());
    }

    @Override
    public int getLayout() {
        return R.layout.item_fans;
    }
}
