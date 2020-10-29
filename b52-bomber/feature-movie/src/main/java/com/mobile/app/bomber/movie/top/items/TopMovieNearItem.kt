package com.mobile.app.bomber.movie.top.items

import com.mobile.app.bomber.data.http.entities.ApiMovie
import com.mobile.app.bomber.movie.R
import com.mobile.app.bomber.movie.databinding.MovieItemTopNearBinding
import com.mobile.app.bomber.movie.databinding.MovieItemTopVerBinding
import com.pacific.adapter.AdapterViewHolder
import com.pacific.adapter.SimpleRecyclerItem

class TopMovieNearItem(val data: ApiMovie.Movie) : SimpleRecyclerItem() {

    override fun bind(holder: AdapterViewHolder) {
        val binding = holder.binding(MovieItemTopNearBinding::bind)
        binding.txtLabel.text = data.name
        binding.txtDesc.text = data.desc
        holder.attachOnClickListener(R.id.item_top_movie_near)
        holder.attachImageLoader(R.id.img_cover)
    }

    override fun getLayout(): Int {
        return R.layout.movie_item_top_near
    }
}