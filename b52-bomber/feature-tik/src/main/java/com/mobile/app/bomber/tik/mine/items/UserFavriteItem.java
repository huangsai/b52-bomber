package com.mobile.app.bomber.tik.mine.items;

import android.view.View;

import androidx.annotation.NonNull;

import com.mobile.app.bomber.data.http.entities.ApiMovieCollectionList;
import com.mobile.app.bomber.data.http.entities.ApiVideo;
import com.mobile.app.bomber.tik.R;
import com.mobile.app.bomber.tik.databinding.FragmentMeFavriteVideoItemBinding;
import com.mobile.app.bomber.tik.databinding.FragmentMeVideoItemBinding;
import com.pacific.adapter.AdapterViewHolder;
import com.pacific.adapter.SimpleRecyclerItem;

import org.jetbrains.annotations.NotNull;

public class UserFavriteItem extends SimpleRecyclerItem {

    @NonNull
    public final ApiMovieCollectionList.Movie data;
    private final long selfId;

    public UserFavriteItem(@NonNull ApiMovieCollectionList.Movie data, long selfID) {
        this.data = data;
        this.selfId = selfID;
    }

    @Override
    public void bind(@NotNull AdapterViewHolder holder) {
        FragmentMeFavriteVideoItemBinding binding = holder.binding(FragmentMeFavriteVideoItemBinding::bind);
        holder.attachImageLoader(R.id.iv_item);
//        binding.ivLikeStatus.setSelected(data.isLiking());
//        binding.tvLikeCount.setText(String.valueOf(data.getLike()));

//        binding.videoStatus.setVisibility(data.isChecking() ? View.VISIBLE : View.GONE);
        binding.tvPlayCount.setText(String.valueOf(data.getPlaynum()));
        holder.attachOnClickListener(R.id.layout_user_video_item);
    }

    @Override
    public int getLayout() {
        return R.layout.fragment_me_favrite_video_item;
    }
}
