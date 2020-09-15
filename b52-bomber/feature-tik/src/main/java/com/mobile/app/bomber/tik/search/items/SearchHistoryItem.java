package com.mobile.app.bomber.tik.search.items;

import androidx.annotation.NonNull;

import com.pacific.adapter.AdapterViewHolder;
import com.pacific.adapter.SimpleRecyclerItem;
import com.mobile.app.bomber.data.db.entities.DbTikSearchKey;

import com.mobile.app.bomber.tik.R;
import com.mobile.app.bomber.tik.databinding.FragmentHistoryItemBinding;

import org.jetbrains.annotations.NotNull;

public class SearchHistoryItem extends SimpleRecyclerItem {

    @NonNull
    public final DbTikSearchKey data;

    public SearchHistoryItem(@NonNull DbTikSearchKey data) {
        this.data = data;
    }

    @Override
    public void bind(@NotNull AdapterViewHolder holder) {
        FragmentHistoryItemBinding binding = holder.binding(FragmentHistoryItemBinding::bind);
        binding.leftTitle.setText(data.getName());
        holder.attachOnClickListener(R.id.delete);
        holder.attachOnClickListener(R.id.layout_item);
    }

    @Override
    public int getLayout() {
        return R.layout.fragment_history_item;
    }

}
