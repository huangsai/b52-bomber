package com.mobile.app.bomber.tik.mine.items;

import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.pacific.adapter.AdapterViewHolder;
import com.pacific.adapter.SimpleRecyclerItem;
import com.mobile.app.bomber.data.http.entities.ApiFollow;

import com.mobile.app.bomber.tik.R;
import com.mobile.app.bomber.tik.databinding.ItemFansBinding;

import org.jetbrains.annotations.NotNull;

public class AttentionFansItem extends SimpleRecyclerItem {

    @NonNull
    public final ApiFollow.Follow data;

    public boolean isDian;

    public AttentionFansItem(@NonNull ApiFollow.Follow data, Boolean isFans) {
        this.data = data;
    }

    @Override
    public void bind(@NotNull AdapterViewHolder holder) {
        ItemFansBinding binding = holder.binding(ItemFansBinding::bind);
        String username = data.getUsername();
        if (username.isEmpty()) {
            username = "微瑟00";
        }
        binding.usernameTv.setText(username);
        binding.statusBtn.setText(data.isFollowing() ? "互相关注" : "回关");
        binding.statusBtn.setSelected(data.isFollowing());
        holder.attachImageLoader(R.id.img_profile);
        holder.attachOnClickListener(R.id.status_btn);
        holder.attachOnClickListener(R.id.img_profile);

    }

    @Override
    public int getLayout() {
        return R.layout.item_fans;
    }
}
