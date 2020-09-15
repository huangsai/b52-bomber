package com.mobile.app.bomber.tik.message.items;

import androidx.annotation.NonNull;

import com.pacific.adapter.AdapterViewHolder;
import com.pacific.adapter.SimpleRecyclerItem;
import com.mobile.app.bomber.data.http.entities.ApiCommentList;
import com.mobile.guava.jvm.date.Java8TimeKt;

import com.mobile.app.bomber.tik.R;
import com.mobile.app.bomber.tik.databinding.ItemCommentBinding;

import org.jetbrains.annotations.NotNull;

public class CommentItem extends SimpleRecyclerItem {

    @NonNull
    public final ApiCommentList.Item data;

    public CommentItem(@NonNull ApiCommentList.Item data) {
        this.data = data;
    }

    @Override
    public void bind(@NotNull AdapterViewHolder holder) {
        ItemCommentBinding binding = holder.binding(ItemCommentBinding::bind);
        binding.txtTitle.setText((data.getUsername()));
        binding.txtContent.setText((data.getContent()));

        String ago = Java8TimeKt.ago(data.getTime(), System.currentTimeMillis());
        binding.txtTime.setText(ago);

        holder.attachImageLoader(R.id.img_profile);
        holder.attachImageLoader(R.id.img_cover);

        holder.attachOnClickListener(R.id.img_cover);
        holder.attachOnClickListener(R.id.img_profile);


    }

    @Override
    public int getLayout() {
        return R.layout.item_comment;
    }
}
