package com.mobile.app.bomber.tik.home.items;

import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;

import com.pacific.adapter.AdapterViewHolder;
import com.pacific.adapter.SimpleRecyclerItem;
import com.mobile.app.bomber.data.http.entities.ApiVideo;

import com.mobile.app.bomber.tik.R;
import com.mobile.app.bomber.tik.databinding.ItemNearbyVideoBinding;

import org.jetbrains.annotations.NotNull;

public class NearbyVideoItem extends SimpleRecyclerItem {

    @NonNull
    public final ApiVideo.Video data;

    public NearbyVideoItem(@NonNull ApiVideo.Video data) {
        this.data = data;
    }

    @Override
    public void bind(@NotNull AdapterViewHolder holder) {
        ItemNearbyVideoBinding binding = holder.binding(ItemNearbyVideoBinding::bind);
        binding.txtName.setText(data.getUsername());
        binding.txtDistance.setText(data.distanceText());
        if (TextUtils.isEmpty(data.getDesc())) {
//            binding.txtDesc.setVisibility(View.GONE);
            binding.txtDesc.setText("视频暂无简介");

        } else {
//            binding.txtDesc.setVisibility(View.VISIBLE);
            binding.txtDesc.setText(data.getDesc());
        }
        holder.attachImageLoader(R.id.img_profile);
        holder.attachImageLoader(R.id.img_cover);
        holder.attachOnClickListener(R.id.item_nearby_video);
    }

    @Override
    public int getLayout() {
        return R.layout.item_nearby_video;
    }
}