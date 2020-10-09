package com.mobile.app.bomber.tik.category.items;

import androidx.annotation.NonNull;

import com.mobile.app.bomber.data.http.entities.ApiAd;
import com.mobile.app.bomber.data.http.entities.ApiFixedad;
import com.mobile.app.bomber.data.http.entities.ApiVideo;
import com.mobile.app.bomber.tik.R;
import com.mobile.app.bomber.tik.databinding.ItemCategoryDiscoveryVideoBinding;
import com.mobile.app.bomber.tik.databinding.ItemCategoryTitleVideoBinding;
import com.mobile.app.bomber.tik.databinding.ItemCategoryTitleVideoitemBinding;
import com.pacific.adapter.AdapterViewHolder;
import com.pacific.adapter.SimpleRecyclerItem;

import org.jetbrains.annotations.NotNull;

public class TitleVideoItem extends SimpleRecyclerItem {

    @NonNull
    public ApiFixedad.FixedadObj data;

    public TitleVideoItem(@NonNull ApiFixedad.FixedadObj data) {
        this.data = data;
    }

    @Override
    public void bind(@NotNull AdapterViewHolder holder) {
        ItemCategoryTitleVideoitemBinding binding = holder.binding(ItemCategoryTitleVideoitemBinding::bind);
//        holder.attachOnClickListener(R.id.item_category_discovery_video);

        holder.attachImageLoader(R.id.img_title);
        holder.attachOnClickListener(R.id.img_title);
    }

    @Override
    public int getLayout() {
        return R.layout.item_category_title_videoitem;
    }
}
