package com.mobile.app.bomber.tik.message.items;

import androidx.annotation.NonNull;

import com.pacific.adapter.AdapterViewHolder;
import com.pacific.adapter.SimpleRecyclerItem;

import com.mobile.app.bomber.tik.R;
import com.mobile.app.bomber.tik.databinding.ItemMsgBinding;

import org.jetbrains.annotations.NotNull;

public class MsgItem extends SimpleRecyclerItem {

    @NonNull
    public final String data;

    public MsgItem(@NonNull String data) {
        this.data = data;
    }

    @Override
    public void bind(@NotNull AdapterViewHolder holder) {
        ItemMsgBinding binding = holder.binding(ItemMsgBinding::bind);
        // 绑定图片加载，逻辑在MsgFragment.load(...)方法
        // MsgFragment.onCreate(...)方法设置了图片加载器
        // MsgFragment实现AdapterImageLoader接口
//        holder.attachImageLoader(R.id.img_profile);
    }

    @Override
    public int getLayout() {
        return R.layout.item_msg;
    }
}
