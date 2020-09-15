package com.mobile.app.bomber.tik.message.items;

import androidx.annotation.NonNull;

import com.pacific.adapter.AdapterViewHolder;
import com.pacific.adapter.SimpleRecyclerItem;
import com.mobile.app.bomber.data.http.entities.ApiAtList;

import com.mobile.app.bomber.tik.R;
import com.mobile.app.bomber.tik.databinding.ItemAtBinding;

import org.jetbrains.annotations.NotNull;

public class AtItem extends SimpleRecyclerItem {

    @NonNull
    public final ApiAtList.Item data;

    public AtItem(@NonNull ApiAtList.Item data) {
        this.data = data;
    }

    @Override
    public void bind(@NotNull AdapterViewHolder holder) {
        ItemAtBinding binding = holder.binding(ItemAtBinding::bind);
        binding.txtTitle.setText((data.getUsername()));
        binding.txtContent.setText((data.getContent()));
        binding.txtTime.setText(String.valueOf(data.getTime()));

        holder.attachImageLoader(R.id.img_profile);
        holder.attachImageLoader(R.id.img_cover);
        holder.attachOnClickListener(R.id.img_cover);
        holder.attachOnClickListener(R.id.img_profile);

    }

    @Override
    public int getLayout() {
        return R.layout.item_at;
    }
}
