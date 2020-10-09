package com.mobile.app.bomber.tik.search.items;

import androidx.annotation.NonNull;

import com.mobile.app.bomber.data.http.entities.ApiAtUser;
import com.pacific.adapter.AdapterViewHolder;
import com.pacific.adapter.SimpleRecyclerItem;
import com.mobile.app.bomber.data.http.entities.ApiFollow;

import com.mobile.app.bomber.tik.R;
import com.mobile.app.bomber.tik.databinding.FragmentSearchUserItemBinding;

import org.jetbrains.annotations.NotNull;

public class SearchUserItem extends SimpleRecyclerItem {
    @NonNull
    public final ApiAtUser data;

    public SearchUserItem(@NonNull ApiAtUser user) {
        this.data = user;
    }

    @Override
    public void bind(@NotNull AdapterViewHolder holder) {
        FragmentSearchUserItemBinding binding = holder.binding(FragmentSearchUserItemBinding::bind);
        holder.attachOnClickListener(R.id.layout_user_item);
    }

    @Override
    public int getLayout() {
        return R.layout.fragment_search_user_item;
    }
}
