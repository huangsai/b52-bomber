package com.mobile.app.bomber.tik.category.items;

import android.view.View;

import androidx.annotation.NonNull;

import com.bumptech.glide.util.Preconditions;
import com.pacific.adapter.AdapterViewHolder;
import com.pacific.adapter.SimpleRecyclerItem;
import com.mobile.app.bomber.data.http.entities.ApiRank;

import com.mobile.app.bomber.tik.R;
import com.mobile.app.bomber.tik.databinding.ItemRankBinding;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class RankItem extends SimpleRecyclerItem {

    @NonNull
    public final ApiRank.Rank data;
    private final String countText;
    private final int progress;
    private float flag;


    public RankItem(@NonNull ApiRank.Rank data, ApiRank.Rank rank, int type) {
        Preconditions.checkNotNull(data);
        this.data = data;
        if (type == 1) {
            this.countText = (int) data.getNum() + "个播放";
        } else {
            this.countText = (int) data.getNum() + "个点赞";
        }
        flag = (data.getNum() / rank.getNum());

        if (flag == 1) {
            progress = 100;
        } else {
            progress = (int) (flag * 100);
        }

    }


    @Override
    public void bind(@NotNull AdapterViewHolder holder) {
        ItemRankBinding binding = holder.binding(ItemRankBinding::bind);
        binding.txtUsername.setText(data.getUsername());
        int index = holder.getBindingAdapterPosition();
        binding.txtCount.setText(countText);
        if (data.isFollowing()) {
            binding.btnFollow.setText("取消关注");
            binding.btnFollow.setBackgroundResource(R.drawable.bg_ripple_gray);
        } else {
            binding.btnFollow.setText("+关注");
            binding.btnFollow.setBackgroundResource(R.drawable.bg_ripple_red);
        }
        binding.progress.setProgress(progress);
        binding.vipText.setText(String.valueOf(index + 1));


        if (index == 0) {
            binding.imgVip.setVisibility(View.VISIBLE);
            binding.imgDown.setImageResource(R.drawable.huangsesanjiaoxing);
        } else if (index == 1) {
            binding.imgVip.setVisibility(View.VISIBLE);
            binding.imgDown.setImageResource(R.drawable.baisesanjiaoxing);
        } else if (index == 2) {
            binding.imgVip.setVisibility(View.VISIBLE);
            binding.imgDown.setImageResource(R.drawable.rectangle_white);
        } else {
            binding.imgVip.setVisibility(View.GONE);
            binding.imgDown.setImageResource(R.drawable.rectangle_white);
        }
        holder.attachImageLoader(R.id.img_profile);
        holder.attachOnClickListener(R.id.btn_follow);
    }

    @Override
    public void bindPayloads(@NotNull AdapterViewHolder holder, @Nullable List<?> payloads) {
        ItemRankBinding binding = holder.binding(ItemRankBinding::bind);
        if (data.isFollowing()) {
            binding.btnFollow.setText("取消关注");
            binding.btnFollow.setBackgroundResource(R.drawable.bg_ripple_gray);
        } else {
            binding.btnFollow.setText("+关注");
            binding.btnFollow.setBackgroundResource(R.drawable.bg_ripple_red);
        }
    }

    @Override
    public int getLayout() {
        return R.layout.item_rank;
    }
}
