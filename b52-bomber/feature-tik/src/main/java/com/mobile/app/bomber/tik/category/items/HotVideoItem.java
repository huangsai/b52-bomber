package com.mobile.app.bomber.tik.category.items;

import androidx.annotation.NonNull;

import com.pacific.adapter.AdapterViewHolder;
import com.pacific.adapter.SimpleRecyclerItem;
import com.mobile.app.bomber.data.http.entities.ApiVideo;

import com.mobile.app.bomber.tik.R;
import com.mobile.app.bomber.tik.databinding.ItemCategoryHotVideoBinding;

import org.jetbrains.annotations.NotNull;

public class HotVideoItem extends SimpleRecyclerItem {

    @NonNull
    public final ApiVideo.Video data;
    private final String playCountText;

    public HotVideoItem(@NonNull ApiVideo.Video data) {
        this.data = data;
        this.playCountText = "播放" + data.getPlayCount() + "次";
    }

    @Override
    public void bind(@NotNull AdapterViewHolder holder) {
        ItemCategoryHotVideoBinding binding = holder.binding(ItemCategoryHotVideoBinding::bind);
        binding.txtCount.setText("播放" + data.getPlayCount() + "次");
        holder.attachOnClickListener(R.id.item_category_hot_video);
        holder.attachImageLoader(R.id.img_cover);
    }

    @Override
    public int getLayout() {
        return R.layout.item_category_hot_video;
    }
}
