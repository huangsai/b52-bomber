package com.mobile.app.bomber.tik.mine.items;

import android.view.View;

import androidx.annotation.NonNull;

import com.mobile.app.bomber.data.http.entities.ApiVideo;
import com.mobile.app.bomber.tik.R;
import com.mobile.app.bomber.tik.databinding.FragmentMeVideoItemBinding;
import com.pacific.adapter.AdapterViewHolder;
import com.pacific.adapter.SimpleRecyclerItem;

import org.jetbrains.annotations.NotNull;

public class UserVideoItem extends SimpleRecyclerItem {

    @NonNull
    public final ApiVideo.Video data;
    private final long selfId;

    public UserVideoItem(@NonNull ApiVideo.Video data, long selfID) {
        this.data = data;
        this.selfId = selfID;
    }

    @Override
    public void bind(@NotNull AdapterViewHolder holder) {
        FragmentMeVideoItemBinding binding = holder.binding(FragmentMeVideoItemBinding::bind);
        holder.attachImageLoader(R.id.iv_item);
        binding.ivLikeStatus.setSelected(data.isLiking());
        binding.tvLikeCount.setText(String.valueOf(data.getLikeCount()));
        binding.imgPlay.setVisibility(data.isChecking() ? View.GONE : View.VISIBLE);
        binding.videoStatus.setVisibility(data.isChecking() ? View.VISIBLE : View.GONE);
        binding.tvPlayCount.setText((data.getPlayCount() + ""));
        if (selfId == 2) {
            binding.tvPlayCount.setVisibility(View.VISIBLE);
            binding.tvLikeCount.setVisibility(View.GONE);
            binding.ivLikeStatus.setVisibility(View.GONE);
        } else {
            binding.tvPlayCount.setVisibility(View.GONE);
            binding.tvLikeCount.setVisibility(View.VISIBLE);
            binding.ivLikeStatus.setVisibility(View.VISIBLE);
        }
        holder.attachOnClickListener(R.id.layout_user_video_item);
    }

    @Override
    public int getLayout() {
        return R.layout.fragment_me_video_item;
    }
}
