package com.mobile.app.bomber.tik.search.items;

import androidx.annotation.NonNull;

import com.pacific.adapter.AdapterViewHolder;
import com.pacific.adapter.SimpleRecyclerItem;
import com.mobile.app.bomber.data.http.entities.ApiVideo;

import com.mobile.app.bomber.tik.R;
import com.mobile.app.bomber.tik.databinding.FragmentSearchVideoItemBinding;

import org.jetbrains.annotations.NotNull;

public class SearchVideoItem extends SimpleRecyclerItem {

    @NonNull
    public final ApiVideo.Video data;

    public SearchVideoItem(@NonNull ApiVideo.Video data) {
        this.data = data;
    }

    @Override
    public void bind(@NotNull AdapterViewHolder holder) {
        FragmentSearchVideoItemBinding binding = holder.binding(FragmentSearchVideoItemBinding::bind);
        holder.attachOnClickListener(R.id.layout_video_item);
    }

    @Override
    public int getLayout() {
        return R.layout.fragment_search_video_item;
    }

}
