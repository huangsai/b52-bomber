package com.mobile.app.bomber.movie.top.items;

import androidx.annotation.NonNull;

import com.mobile.app.bomber.data.http.entities.ApiMovie;
import com.mobile.app.bomber.movie.R;
import com.mobile.app.bomber.movie.databinding.MovieItemTopVerBinding;
import com.pacific.adapter.AdapterViewHolder;
import com.pacific.adapter.SimpleRecyclerItem;

import org.jetbrains.annotations.NotNull;

public class TopMovieVerItem extends SimpleRecyclerItem {

    @NonNull
    public final ApiMovie.Movie data;

    public TopMovieVerItem(@NonNull ApiMovie.Movie data) {
        this.data = data;
    }

    @Override
    public void bind(@NotNull AdapterViewHolder holder) {
        MovieItemTopVerBinding binding = holder.binding(MovieItemTopVerBinding::bind);
        holder.attachOnClickListener(R.id.item_top_movie_ver);
        holder.attachImageLoader(R.id.img_cover);
    }

    @Override
    public int getLayout() {
        return R.layout.movie_item_top_ver;
    }
}
