package com.mobile.app.bomber.tik.category.items;

import androidx.annotation.NonNull;

import com.pacific.adapter.AdapterViewHolder;
import com.pacific.adapter.SimpleRecyclerItem;
import com.mobile.app.bomber.data.http.entities.ApiVideo;

import com.mobile.app.bomber.tik.R;
import com.mobile.app.bomber.tik.databinding.ItemCategoryDiscoveryVideoBinding;

import org.jetbrains.annotations.NotNull;

public class DiscoveryVideoItem extends SimpleRecyclerItem {

    @NonNull
    public final ApiVideo.Video data;

    public DiscoveryVideoItem(@NonNull ApiVideo.Video data) {
        this.data = data;
    }

    @Override
    public void bind(@NotNull AdapterViewHolder holder) {
        ItemCategoryDiscoveryVideoBinding binding = holder.binding(ItemCategoryDiscoveryVideoBinding::bind);
        binding.txtLabel.setText(data.getLabel());
        holder.attachOnClickListener(R.id.item_category_discovery_video);
        holder.attachImageLoader(R.id.img_cover);
    }

    @Override
    public int getLayout() {
        return R.layout.item_category_discovery_video;
    }
}
