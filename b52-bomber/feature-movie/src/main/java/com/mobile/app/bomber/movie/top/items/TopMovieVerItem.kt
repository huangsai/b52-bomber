package com.mobile.app.bomber.movie.top.items

import com.mobile.app.bomber.data.http.entities.ApiMovie
import com.mobile.app.bomber.movie.R
import com.mobile.app.bomber.movie.databinding.MovieItemTopVerBinding
import com.pacific.adapter.AdapterViewHolder
import com.pacific.adapter.SimpleRecyclerItem

class TopMovieVerItem(val data: ApiMovie.Movie) : SimpleRecyclerItem() {

    override fun bind(holder: AdapterViewHolder) {
        val binding = holder.binding(MovieItemTopVerBinding::bind)
        binding.txtLabel.text = data.name
        if (data.desc.isNullOrEmpty()) {
            binding.txtDesc.text = "视频简介 暂无"
        } else {
            binding.txtDesc.text = data.desc
        }
        holder.attachOnClickListener(R.id.item_top_movie_ver)
        holder.attachImageLoader(R.id.img_cover)
    }

    override fun getLayout(): Int {
        return R.layout.movie_item_top_ver
    }
}