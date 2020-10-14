package com.mobile.app.bomber.tik.message.items;

import android.view.View;

import androidx.annotation.NonNull;

import com.mobile.app.bomber.data.http.entities.ApiUsermsg;
import com.pacific.adapter.AdapterViewHolder;
import com.pacific.adapter.SimpleRecyclerItem;
import com.mobile.app.bomber.data.http.entities.ApiFollow;

import com.mobile.app.bomber.tik.R;
import com.mobile.app.bomber.tik.databinding.ItemFansBinding;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class FansItem extends SimpleRecyclerItem {

    @NonNull
    public final ApiUsermsg.Item data;
    private final String time;

    public FansItem(@NonNull ApiUsermsg.Item data) {
        this.data = data;
        this.time = new SimpleDateFormat("MM-dd").format(new Date(data.getCreatetime()));
    }

    @Override
    public void bind(@NotNull AdapterViewHolder holder) {
        ItemFansBinding binding = holder.binding(ItemFansBinding::bind);
        binding.usernameTv.setText(data.getFromuserinfo().get(0).getName());
        binding.descTv.setVisibility(View.VISIBLE);
        if(data.getFollowtype()==1){
            binding.descTv.setText("关注了你");
        }else {
            binding.descTv.setText("通过作品关注了你");
        }

        binding.timeTv.setVisibility(View.VISIBLE);
        binding.timeTv.setText(time);
        binding.statusBtn.setText(data.getIsfollow()==2 ? "互相关注" : "回关");
        binding.statusBtn.setSelected(data.getIsfollow()==2);
        holder.attachImageLoader(R.id.img_profile);
        holder.attachOnClickListener(R.id.status_btn);
    }

    @Override
    public void bindPayloads(@NotNull AdapterViewHolder holder, @Nullable List<?> payloads) {
        ItemFansBinding binding = holder.binding(ItemFansBinding::bind);
        binding.statusBtn.setText(data.getIsfollow()==2 ? "互相关注" : "回关");
        binding.statusBtn.setSelected(data.getIsfollow()==2);
    }

    @Override
    public int getLayout() {
        return R.layout.item_fans;
    }
}
