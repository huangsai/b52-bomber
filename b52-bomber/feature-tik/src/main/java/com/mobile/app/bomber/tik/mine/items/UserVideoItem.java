package com.mobile.app.bomber.tik.mine.items;

import androidx.annotation.NonNull;

import com.pacific.adapter.AdapterViewHolder;
import com.pacific.adapter.SimpleRecyclerItem;
import com.mobile.app.bomber.data.http.entities.ApiVideo;

import com.mobile.app.bomber.tik.R;
import com.mobile.app.bomber.tik.databinding.FragmentMeVideoItemBinding;

import org.jetbrains.annotations.NotNull;

public class UserVideoItem extends SimpleRecyclerItem {

    @NonNull
    public final ApiVideo.Video data;

    public UserVideoItem(@NonNull ApiVideo.Video data) {
        this.data = data;
    }

    @Override
    public void bind(@NotNull AdapterViewHolder holder) {
        FragmentMeVideoItemBinding binding = holder.binding(FragmentMeVideoItemBinding::bind);
        holder.attachImageLoader(R.id.iv_item);
        binding.ivLikeStatus.setSelected(data.isLiking());
        binding.tvLikeCount.setText(String.valueOf(data.getLikeCount()));
        holder.attachOnClickListener(R.id.layout_user_video_item);
    }

    @Override
    public int getLayout() {
        return R.layout.fragment_me_video_item;
    }
}
