package com.mobile.app.bomber.movie.top;

import androidx.annotation.NonNull;

import com.mobile.app.bomber.movie.R;
import com.mobile.app.bomber.movie.databinding.MovieItemTopTitleBinding;
import com.pacific.adapter.AdapterViewHolder;
import com.pacific.adapter.SimpleRecyclerItem;

import org.jetbrains.annotations.NotNull;

public class TopTitlePresenter extends SimpleRecyclerItem {

    @NonNull
    public final String name;

    public TopTitlePresenter(@NonNull String name) {
        this.name = name;
    }

    @Override
    public void bind(@NotNull AdapterViewHolder holder) {
        MovieItemTopTitleBinding binding = holder.binding(MovieItemTopTitleBinding::bind);
        binding.itemTitle.setText(name);
        holder.attachOnClickListener(R.id.item_title);
    }

    @Override
    public int getLayout() {
        return R.layout.movie_item_top_title;
    }
}
