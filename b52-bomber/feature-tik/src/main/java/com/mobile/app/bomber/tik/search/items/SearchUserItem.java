package com.mobile.app.bomber.tik.search.items;

import androidx.annotation.NonNull;

import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.mobile.app.bomber.data.http.entities.ApiAtUser;
import com.mobile.app.bomber.tik.base.GlideExtKt;
import com.mobile.ext.glide.GlideApp;
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
//        GlideExtKt.loadProfile(holder.activity(), data.getProfile(), binding.heartImage);
        int index =  holder.getBindingAdapterPosition();
        GlideApp.with(holder.activity())
                .load(data.getUsers().get(index).getProfile())
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(binding.heartImage);
        holder.attachOnClickListener(R.id.layout_user_item);
        holder.attachOnClickListener(R.id.fensi_atten);
        binding.nofiNick.setText(data.getUsers().get(0).getUsername());
    }


    @Override
    public int getLayout() {
        return R.layout.fragment_search_user_item;
    }
}
