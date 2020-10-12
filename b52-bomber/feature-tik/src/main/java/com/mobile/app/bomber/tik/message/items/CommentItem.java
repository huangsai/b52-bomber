package com.mobile.app.bomber.tik.message.items;

import androidx.annotation.NonNull;

import com.mobile.app.bomber.data.http.entities.ApiUsermsg;
import com.pacific.adapter.AdapterViewHolder;
import com.pacific.adapter.SimpleRecyclerItem;
import com.mobile.app.bomber.data.http.entities.ApiCommentList;
import com.mobile.guava.jvm.date.Java8TimeKt;

import com.mobile.app.bomber.tik.R;
import com.mobile.app.bomber.tik.databinding.ItemCommentBinding;

import org.jetbrains.annotations.NotNull;

public class CommentItem extends SimpleRecyclerItem {

    @NonNull
    public final ApiUsermsg.Item data;

    public CommentItem(@NonNull ApiUsermsg.Item data) {
        this.data = data;
    }

    @Override
    public void bind(@NotNull AdapterViewHolder holder) {
        ItemCommentBinding binding = holder.binding(ItemCommentBinding::bind);
        binding.txtTitle.setText((data.getFromuserinfo().get(0).getName()));
        binding.txtContent.setText((data.getContent()));

        String ago = Java8TimeKt.ago(data.getCreatetime(), System.currentTimeMillis());
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
