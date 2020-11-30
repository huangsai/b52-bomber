package com.mobile.app.bomber.tik.mine.items;

import androidx.annotation.NonNull;

import com.mobile.app.bomber.data.http.entities.ApiFollow;
import com.mobile.app.bomber.tik.R;
import com.mobile.app.bomber.tik.databinding.ItemFansBinding;
import com.mobile.app.bomber.tik.databinding.ItemFollowBinding;
import com.pacific.adapter.AdapterViewHolder;
import com.pacific.adapter.SimpleRecyclerItem;

import org.jetbrains.annotations.NotNull;

public class AttentionFollowItem extends SimpleRecyclerItem {

    @NonNull
    public final ApiFollow.Follow data;

    public boolean isDian;

    public AttentionFollowItem(@NonNull ApiFollow.Follow data, Boolean isFans) {
        this.data = data;
    }

    @Override
    public void bind(@NotNull AdapterViewHolder holder) {
        ItemFollowBinding binding = holder.binding(ItemFollowBinding::bind);
        binding.usernameTv.setText(data.getUsername());
        binding.statusBtn.setText(data.isFollowing() ? "已关注" : "+关注");
        binding.statusBtn.setSelected(data.isFollowing());
        holder.attachImageLoader(R.id.img_profile);
        holder.attachOnClickListener(R.id.status_btn);
        holder.attachOnClickListener(R.id.img_profile);

    }

    @Override
    public int getLayout() {
        return R.layout.item_follow;
    }
}
