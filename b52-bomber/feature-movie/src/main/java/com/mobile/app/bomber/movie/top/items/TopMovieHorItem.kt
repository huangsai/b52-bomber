package com.mobile.app.bomber.movie.top.items

import com.mobile.app.bomber.data.http.entities.ApiMovieHistory
import com.mobile.app.bomber.movie.R
import com.mobile.app.bomber.movie.databinding.MovieItemTopHorBinding
import com.pacific.adapter.AdapterViewHolder
import com.pacific.adapter.SimpleRecyclerItem

class TopMovieHorItem(val data: ApiMovieHistory.History) : SimpleRecyclerItem() {

    override fun bind(holder: AdapterViewHolder) {
        val binding: MovieItemTopHorBinding = holder.binding(MovieItemTopHorBinding::bind)
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