package com.mobile.app.bomber.movie.common.items

import com.mobile.app.bomber.data.http.entities.ApiMovie
import com.mobile.app.bomber.movie.R
import com.mobile.app.bomber.movie.databinding.MovieItemCommonMovieBinding
import com.pacific.adapter.AdapterViewHolder
import com.pacific.adapter.SimpleRecyclerItem

class CommonMovieItem(val data: ApiMovie.Movie) : SimpleRecyclerItem() {
    override fun bind(holder: AdapterViewHolder) {
        val binding: MovieItemCommonMovieBinding = holder.binding(MovieItemCommonMovieBinding::bind)
        holder.attachOnClickListener(R.id.item_common_movie)
        holder.attachImageLoader(R.id.img_cover)
        binding.txtLabel.text = data.name
        binding.txtDesc.text = data.desc
    }

    override fun getLayout(): Int {
        return R.layout.movie_item_common_movie
    }

}